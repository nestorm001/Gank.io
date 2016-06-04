package nesto.gankio.ui;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created on 2016/4/11.
 * By nesto
 */
public abstract class Presenter<V> {

    protected V view;
    protected boolean isViewStillAlive;
    protected ArrayList<Subscription> subscriptionList;

    public void attachView(V view) {
        this.view = view;
        isViewStillAlive = true;
        subscriptionList = new ArrayList<>();
    }

    public void detachView() {
        view = null;
        isViewStillAlive = false;
        for (Subscription subscription : subscriptionList) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }
}

