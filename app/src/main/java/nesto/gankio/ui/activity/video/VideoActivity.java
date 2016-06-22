package nesto.gankio.ui.activity.video;

import android.os.Bundle;

import nesto.gankio.ui.activity.ActionBarActivity;

/**
 * Created on 2016/5/11.
 * By nesto
 */
public class VideoActivity extends ActionBarActivity implements VideoMvpView {

    private VideoPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new VideoPresenter();
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
