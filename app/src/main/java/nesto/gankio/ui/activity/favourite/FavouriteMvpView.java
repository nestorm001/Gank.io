package nesto.gankio.ui.activity.favourite;

import nesto.gankio.model.Data;
import nesto.gankio.ui.MvpView;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public interface FavouriteMvpView extends MvpView {
    void addItem();

    void showInputDialog(Data data);

    void backToMain();
}
