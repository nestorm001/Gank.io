package nesto.gankio.ui.activity;

import android.os.Bundle;

import nesto.gankio.ui.MvpView;
import nesto.gankio.ui.Presenter;

/**
 * Created on 2016/6/20.
 * By nesto
 */
public abstract class BaseMvpActivity<V extends MvpView, P extends Presenter<V>> extends BaseActivity {
    public P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //noinspection unchecked
        presenter.attachView((V) this);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    // 实例化 presenter
    public abstract P initPresenter();
}
