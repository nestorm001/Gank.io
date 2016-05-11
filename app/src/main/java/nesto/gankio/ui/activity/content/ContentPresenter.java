package nesto.gankio.ui.activity.content;

import nesto.gankio.ui.Presenter;

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
}
