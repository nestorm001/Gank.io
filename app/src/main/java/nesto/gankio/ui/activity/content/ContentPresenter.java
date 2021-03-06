package nesto.gankio.ui.activity.content;

import nesto.gankio.db.DBHelper;
import nesto.gankio.model.Data;
import nesto.gankio.network.ErrorHandlerHelper;
import nesto.gankio.network.HttpMethods;
import nesto.gankio.ui.Presenter;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created on 2016/5/11.
 * By nesto
 */
public class ContentPresenter extends Presenter<ContentMvpView> {

    public void getRandomPicture() {
        Action1<String> onNext = new Action1<String>() {
            @Override
            public void call(String s) {
                if (isViewStillAlive) {
                    view.show(s);
                }
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
                        data.setFavoured(false);
                        if (isViewStillAlive) {
                            view.setFavourite(data);
                        }
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
                        data.setFavoured(true);
                        if (isViewStillAlive) {
                            view.setFavourite(data);
                        }
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
