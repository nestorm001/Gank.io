package nesto.gankio.ui.activity.favourite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
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
import rx.Subscriber;
import rx.Subscription;
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
public class FavouritePresenter extends Presenter<FavouriteMvpView> {

    private boolean isLoad = false;
    private Subscription subscription;

    public void loadFavourite(final Intent intent) {
        if (isLoad) {
            dealWithIntent(intent);
        } else {
            subscription = DBHelper.getInstance()
                    .getAll()
                    .subscribe(new Action1<ArrayList<Data>>() {
                        @Override
                        public void call(ArrayList<Data> datas) {
                            LogUtil.d("收藏夹加载完成");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            LogUtil.e(throwable.getLocalizedMessage());
                            if (throwable instanceof DBException && throwable.getMessage().equals("no result")) {
                                finishSubscribe(intent);
                            }
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            finishSubscribe(intent);
                        }
                    });
        }
    }

    private void finishSubscribe(Intent intent) {
        dealWithIntent(intent);
        isLoad = true;
        subscription.unsubscribe();
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

    private void handleText(Intent intent) {
        String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        String content = intent.getStringExtra(Intent.EXTRA_TEXT);
        LogUtil.d(content);
        if (content.contains("http")) {
            int position = content.indexOf("http");
            title = (title == null || title.isEmpty()) ? content.substring(0, position) : title;
            String prefix = "分享";
            if (title.startsWith(prefix)) {
                title = title.replace(prefix, "");
            }
            String url = content.substring(position, content.length());
            // deal with special urls
            //eg 分享竹井詩織里的单曲《桜色》: http://163.fm/SHyQROr
            // (来自@网易云音乐)
            if (url.contains("\n")) {
                url = url.substring(0, url.indexOf("\n"));
            }
            if (url.contains(" ")) {
                url = url.substring(0, url.indexOf(" "));
            }
            String id = Integer.toHexString(title.hashCode()) + Integer.toHexString(url.hashCode());
            Data data = new Data(id, title, url, C.FROM_SHARE);
            if (title.trim().isEmpty()) {
                if (isViewStillAlive) {
                    view.showInputDialog(data);
                }
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
        AppUtil.createBitmapObservable(uri.toString(), view.getContext())
                .map(new Func1<Bitmap, Uri>() {
                    @Override
                    public Uri call(Bitmap bitmap) {
                        Uri bmpUri;
                        try {
                            File file = AppUtil.saveBitmapFile(view.getContext(), AppUtil.getCurrentTime(), bitmap);
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
                        String id = Integer.toHexString(DataType.BENEFIT.toString().hashCode()) +
                                Integer.toHexString(uri.toString().hashCode());
                        Data data = new Data(id, "", uri.toString(), DataType.BENEFIT.toString());
                        addToFavourite(data);
                    }
                });
    }

    public void addToFavourite(final Data data) {
        DBHelper.getInstance()
                .add(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        if (isViewStillAlive) {
                            view.addItem();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof DBException) {
                            if (isViewStillAlive) {
                                view.backToMain();
                            }
                        }
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
