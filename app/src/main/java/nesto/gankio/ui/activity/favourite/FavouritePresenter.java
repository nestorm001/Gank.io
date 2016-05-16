package nesto.gankio.ui.activity.favourite;

import android.content.Intent;

import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.global.A;
import nesto.gankio.global.C;
import nesto.gankio.model.Data;
import nesto.gankio.ui.Presenter;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;
import rx.Subscriber;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class FavouritePresenter implements Presenter<FavouriteMvpView> {

    private FavouriteMvpView view;

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

    private void handleText(Intent intent) {
        String content = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (content.contains("http")) {
            int position = content.indexOf("http");
            String title = content.substring(0, position);
            String url = content.substring(position, content.length());
            String id = Integer.toHexString(AppUtil.getCurrentTime().hashCode());
            addToFavourite(new Data(id, title, url, C.FROM_SHARE));
        } else {
            AppUtil.showToast(A.getContext().getString(R.string.not_supported));
        }
    }

    private void handleImage(Intent intent) {
        //TODO
        AppUtil.showToast("TODO");
    }

    public void addToFavourite(final Data data) {
        DBHelper.getInstance()
                .add(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        view.addItem();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getLocalizedMessage());
                        AppUtil.showToast(A.getContext().getString(R.string.fail_to_add_to_favourite));
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
