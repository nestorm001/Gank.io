package nesto.gankio.ui.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import nesto.gankio.R;
import nesto.gankio.global.A;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;

/**
 * Created on 2016/6/3.
 * By nesto
 * From https://github.com/bushijie/ParallaxSwipeBack
 */
public abstract class SwipeBackActivity extends ActionBarActivity implements SlidingPaneLayout.PanelSlideListener {
    private SlidingPaneLayout slidingPaneLayout;
    private int defaultTranslationX = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isSupportSwipeBack() && A.getInstance().getPreviousActivity() != null) {
            LogUtil.d("isSupportSwipeBack");
            setSlidingPaneLayout();
            setViews();
        }
        super.onCreate(savedInstanceState);
    }

    private void setSlidingPaneLayout() {
        //通过反射来改变SlidingPaneLayout的值
        try {
            slidingPaneLayout = new SlidingPaneLayout(this);
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            f_overHang.set(slidingPaneLayout, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        slidingPaneLayout.setPanelSlideListener(this);
        slidingPaneLayout.setSliderFadeColor(getResources().getColor(R.color.Transparent));
    }

    private void setViews() {
        defaultTranslationX = AppUtil.dip2px(defaultTranslationX);

        View leftView = new View(this);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        slidingPaneLayout.addView(leftView);

        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundColor(getResources().getColor(android.R.color.white));
        decor.removeView(decorChild);
        decor.addView(slidingPaneLayout);
        slidingPaneLayout.addView(decorChild);
    }

    @Override
    public void onPanelClosed(View view) {
        LogUtil.d("onPanelClosed");
    }

    @Override
    public void onPanelOpened(View view) {
        finish();
        this.overridePendingTransition(0, 0);
    }

    @Override
    public void onPanelSlide(View view, float v) {
        Drawable shadow = getResources().getDrawable(R.drawable.shadow);
        if (shadow != null) {
            shadow.setAlpha((int) (255 - v * 255));
            slidingPaneLayout.setShadowDrawableLeft(shadow);
        }
        Activity previousActivity = A.getInstance().getPreviousActivity();
        previousActivity.getWindow().getDecorView().setTranslationX(v * defaultTranslationX - defaultTranslationX);
    }

    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
