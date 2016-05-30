package nesto.gankio.ui.activity.favourite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nesto.gankio.R;
import nesto.gankio.db.DBException;
import nesto.gankio.db.DBHelper;
import nesto.gankio.global.A;
import nesto.gankio.global.C;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import nesto.gankio.ui.Presenter;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class FavouritePresenter implements Presenter<FavouriteMvpView> {

    private FavouriteMvpView view;
    private boolean isLoad = false;

    @Override
    public void attachView(FavouriteMvpView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    public void dealWithIntent(Intent intent) {
        LogUtil.d(intent.toString());
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleText(intent); // 处理发送来的文字
            } else if (type.startsWith("image/")) {
                handleImage(intent); // 处理发送来的图片
            }
        }
    }

    public void loadFavourite(final Intent intent) {
        if (isLoad) {
            dealWithIntent(intent);
        } else {
            DBHelper.getInstance()
                    .getAll()
                    .subscribe(new Action1<ArrayList<Data>>() {
                        @Override
                        public void call(ArrayList<Data> datas) {
                            LogUtil.d("收藏夹加载完成");
                            dealWithIntent(intent);
                            isLoad = true;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            LogUtil.e(throwable.getLocalizedMessage());
                            if (throwable instanceof DBException && throwable.getMessage().equals("no result")) {
                                dealWithIntent(intent);
                                isLoad = true;
                            }
                        }
                    }).unsubscribe();
        }
    }

    private void handleText(Intent intent) {
        String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        String content = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (content.contains("http")) {
            int position = content.indexOf("http");
            title = (title == null || title.isEmpty()) ? content.substring(0, position) : title;
            String url = content.substring(position, content.length());
            String id = AppUtil.getCurrentTime() + Integer.toHexString(url.hashCode());
            Data data = new Data(id, title, url, C.FROM_SHARE);
            if (title.trim().isEmpty()) {
                view.showInputDialog(data);
            } else {
                addToFavourite(data);
            }
        } else {
            AppUtil.showToast(A.getContext().getString(R.string.not_supported));
        }
    }

    private void handleImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            LogUtil.d("handleImage " + imageUri.toString());
            saveImage(imageUri);
        }
    }

    private void saveImage(final Uri uri) {
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = Picasso.with(view.getContext()).load(uri).get();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<Bitmap, Uri>() {
                    @Override
                    public Uri call(Bitmap bitmap) {
                        Uri bmpUri;
                        try {
                            File file = new File(view.getContext().getCacheDir() + File.separator + "images",
                                    AppUtil.getCurrentTime() + ".png");
                            //noinspection ResultOfMethodCallIgnored
                            file.getParentFile().mkdirs();
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.close();
                            bmpUri = Uri.fromFile(file);
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                        return bmpUri;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AppUtil.showToast(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Uri uri) {
                        String id = AppUtil.getCurrentTime() + Integer.toHexString(uri.toString().hashCode());
                        Data data = new Data(id, "", uri.toString(), DataType.BENEFIT.toString());
                        addToFavourite(data);
                    }
                });
    }

    public void addToFavourite(final Data data) {
        DBHelper.getInstance()
                .add(data)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        AppUtil.showToast(A.getContext().getString(R.string.fail_to_add_to_favourite));
                        DBHelper.getInstance().getFavouriteList().remove(0);
                    }
                })
                .onErrorReturn(new Func1<Throwable, Object>() {
                    @Override
                    public Object call(Throwable throwable) {
                        return null;
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        view.addItem();
                    }
                })
                .subscribe();
    }
}
