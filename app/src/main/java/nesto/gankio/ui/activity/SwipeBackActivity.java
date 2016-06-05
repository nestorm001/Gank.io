package nesto.gankio.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import nesto.gankio.R;
import nesto.gankio.ui.SwipeBackLayout;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.SwipeBackHelper;

/**
 * Created on 2016/6/3.
 * By nesto
 * edit from https://github.com/bushijie/ParallaxSwipeBack and so on
 */
public abstract class SwipeBackActivity extends ActionBarActivity implements SlidingPaneLayout.PanelSlideListener {
    private SlidingPaneLayout slidingPaneLayout;
    private static final int X = AppUtil.dip2px(100);
    private View previousView;
    private Drawable shadow;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Activity previousActivity = SwipeBackHelper.getInstance().getPreviousActivity();
        if (isSupportSwipeBack() && previousActivity != null) {
            setSlidingPaneLayout();
            setViews();
            previousView = previousActivity.getWindow().getDecorView();
        }
        super.onCreate(savedInstanceState);
    }

    private void setSlidingPaneLayout() {
        slidingPaneLayout = new SwipeBackLayout(this);
        slidingPaneLayout.setPanelSlideListener(this);
        slidingPaneLayout.setSliderFadeColor(getResources().getColor(R.color.Transparent));
    }

    private void setViews() {
        View leftView = new View(this);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        slidingPaneLayout.addView(leftView);

        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundColor(getResources().getColor(android.R.color.white));
        decor.removeView(decorChild);
        decor.addView(slidingPaneLayout);
        slidingPaneLayout.addView(decorChild);

        shadow = getResources().getDrawable(R.drawable.shadow);
    }

    @Override
    public void onPanelClosed(View view) {
    }

    @Override
    public void onPanelOpened(View view) {
        finish();
        this.overridePendingTransition(0, 0);
    }

    @Override
    public void onPanelSlide(View view, float v) {
        if (!isFinished) {
            float factor = 1 - v;
            if (shadow != null) {
                shadow.setAlpha((int) (factor * 255));
                slidingPaneLayout.setShadowDrawableLeft(shadow);
            }
            previousView.setTranslationX(-factor * X);
        }
    }

    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isFinished = true;
        if (previousView != null) {
            // get real translation and time, the animation should be similar to {@anim/anim_exit_fade_in.xml}
            int translationX = (int) previousView.getTranslationX();
            translationX = translationX == 0 ? -X : translationX;
            int duration = getResources().getInteger(R.integer.animation_previous_length) *
                    (translationX / -X);
            startAnimation(previousView, translationX, 0, duration, 0);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // the animation should be similar to {@anim/anim_enter_fade_out.xml}
        startAnimation(getWindow().getDecorView(), 0, -X,
                getResources().getInteger(R.integer.animation_previous_length), 150);
    }

    // overridePendingTransition not work for windowIsTranslucent
    // use animator to show transition animation
    private void startAnimation(final View view, int start, int end, int duration, int delay) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setTranslationX(0);
            }
        });
        animator.start();
    }
}
