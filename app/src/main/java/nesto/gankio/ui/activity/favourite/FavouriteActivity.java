package nesto.gankio.ui.activity.favourite;

import android.os.Bundle;

import nesto.gankio.ui.activity.ActionBarActivity;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class FavouriteActivity extends ActionBarActivity implements FavouriteMvpView {

    private FavouritePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new FavouritePresenter();
        presenter.attachView(this);
    }

    @Override
    public void setContentView() {
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
