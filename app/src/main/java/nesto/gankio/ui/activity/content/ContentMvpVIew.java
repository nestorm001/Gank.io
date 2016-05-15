package nesto.gankio.ui.activity.content;

import nesto.gankio.model.Data;
import nesto.gankio.ui.MvpView;

/**
 * Created on 2016/5/11.
 * By nesto
 */
public interface ContentMvpView extends MvpView {
    void show(String url);

    void setFavourite(Data data);
}
