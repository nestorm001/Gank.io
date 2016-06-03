package nesto.gankio.ui;

/**
 * Created on 2016/4/11.
 * By nesto
 */
public abstract class Presenter<V> {

    protected V view;
    protected boolean isViewStillAlive;

    public void attachView(V view) {
        this.view = view;
        isViewStillAlive = true;
    }

    public void detachView() {
        view = null;
        isViewStillAlive = false;
    }
}

