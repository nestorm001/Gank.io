package nesto.gankio.ui.activity.video;

import nesto.gankio.ui.Presenter;

/**
 * Created on 2016/5/11.
 * By nesto
 */
public class VideoPresenter implements Presenter<VideoMvpView> {

    private VideoMvpView view;

    @Override
    public void attachView(VideoMvpView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
