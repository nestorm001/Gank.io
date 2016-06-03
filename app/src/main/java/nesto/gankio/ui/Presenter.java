package nesto.gankio.ui;

/**
 * Created on 2016/4/11.
 * By nesto
 */
public abstract class Presenter<V> {

    protected V view;

    public void attachView(V view) {
        this.view = view;
    }

    public void detachView() {
        view = null;
    }
}

