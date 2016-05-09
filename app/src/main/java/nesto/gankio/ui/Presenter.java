package nesto.gankio.ui;

/**
 * Created on 2016/4/11.
 * By nesto
 */
public interface Presenter<V> {

    void attachView(V view);

    void detachView();

}

