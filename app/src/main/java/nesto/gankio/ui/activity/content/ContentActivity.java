package nesto.gankio.ui.activity.content;

import android.os.Bundle;

import nesto.gankio.ui.activity.ActionBarActivity;

/**
 * Created on 2016/5/11.
 * By nesto
 */
public class ContentActivity extends ActionBarActivity implements ContentMvpView {

    private ContentPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ContentPresenter();
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
