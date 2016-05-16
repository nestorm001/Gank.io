package nesto.gankio.ui.activity.content;

import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.global.A;
import nesto.gankio.model.Data;
import nesto.gankio.network.ErrorHandlerHelper;
import nesto.gankio.network.HttpMethods;
import nesto.gankio.ui.Presenter;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.L;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created on 2016/5/11.
 * By nesto
 */
public class ContentPresenter implements Presenter<ContentMvpView> {

    private ContentMvpView view;

    @Override
    public void attachView(ContentMvpView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    public void getRandomPicture() {
        Action1<String> onNext = new Action1<String>() {
            @Override
            public void call(String s) {
                view.show(s);
            }
        };
        Action1<Throwable> onError = new ErrorHandlerHelper().createOnError(null);
        HttpMethods.getInstance().getOneRandomPicture(onNext, onError);
    }

    public void addToFavourite(final Data data) {
        DBHelper.getInstance()
                .add(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        L.d(e.getLocalizedMessage());
                        AppUtil.showToast(A.getContext().getString(R.string.fail_to_add_to_favourite));
                        data.setFavoured(false);
                        view.setFavourite(data);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    public void removeFromFavourite(final Data data) {
        DBHelper.getInstance()
                .remove(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        L.d(e.getLocalizedMessage());
                        AppUtil.showToast(A.getContext().getString(R.string.fail_to_remove_from_favourite));
                        data.setFavoured(false);
                        view.setFavourite(data);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
