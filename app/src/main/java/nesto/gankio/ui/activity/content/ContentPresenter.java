package nesto.gankio.ui.activity.content;

import nesto.gankio.network.ErrorHandlerHelper;
import nesto.gankio.network.HttpMethods;
import nesto.gankio.ui.Presenter;
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
}
