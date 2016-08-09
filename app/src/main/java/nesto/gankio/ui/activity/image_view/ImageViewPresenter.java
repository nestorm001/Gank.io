package nesto.gankio.ui.activity.image_view;

import java.util.ArrayList;
import java.util.Random;

import nesto.gankio.model.Data;
import nesto.gankio.model.Results;
import nesto.gankio.network.ErrorHandlerHelper;
import nesto.gankio.network.HttpMethods;
import nesto.gankio.ui.Presenter;
import rx.functions.Action1;

/**
 * Created on 2016/5/17.
 * By nesto
 */
public class ImageViewPresenter extends Presenter<ImageViewMvpView> {

    private ArrayList<String> urls;
    private Random random;

    
    @Override
    public void attachView(ImageViewMvpView view) {
        super.attachView(view);
        urls = new ArrayList<>();
        random = new Random();
    }

    public void getRandomPicture() {
        if (urls.isEmpty()) {
            Action1<Results> onNext = new Action1<Results>() {
                @Override
                public void call(Results results) {
                    for (Data data : results.getResults()) {
                        urls.add(data.url());
                    }
                    show();
                }
            };
            Action1<Throwable> onError = new ErrorHandlerHelper().createOnError(null);
            HttpMethods.getInstance().getRandomPictureUrls(onNext, onError);
        } else {
            show();
        }
    }

    private void show() {
        int position = random.nextInt(urls.size());
        String url = urls.get(position);
        urls.remove(position);
        if (isViewStillAlive) {
            view.show(url);
        }
    }
}
