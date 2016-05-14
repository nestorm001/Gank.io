package nesto.gankio.ui.activity.favourite;

import nesto.gankio.ui.Presenter;

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
}
