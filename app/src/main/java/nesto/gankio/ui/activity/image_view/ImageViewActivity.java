package nesto.gankio.ui.activity.image_view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.ui.activity.FullScreenActivity;

/**
 * Created on 2016/5/17.
 * By nesto
 */
public class ImageViewActivity extends FullScreenActivity implements ImageViewMvpView {

    @Bind(R.id.image)
    ImageView image;

    private static final int DURATION = 500;

    private ImageViewPresenter presenter;
    private Callback callback;
    private Animation fadeIn;
    private Animation fadeOut;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ButterKnife.bind(this);
        context = this;
        initCallback();
        presenter = new ImageViewPresenter();
        presenter.attachView(this);
        presenter.getRandomPicture();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void initCallback() {
        final AtomicBoolean playAnimation = new AtomicBoolean(true);

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(DURATION * 3);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(DURATION);

        callback = new Callback() {
            @Override
            public void onSuccess() {
                if (playAnimation.get()) {
                    image.startAnimation(fadeIn);
                }
                playAnimation.set(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.getRandomPicture();
                    }
                }, 5000);
            }

            @Override
            public void onError() {

            }
        };
    }

    @Override
    public void show(final String url) {
        image.startAnimation(fadeOut);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Picasso.with(context)
                        .load(url)
                        .into(image, callback);

            }
        }, DURATION);
    }
}
