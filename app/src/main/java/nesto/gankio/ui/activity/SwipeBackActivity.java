package nesto.gankio.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.Field;

import nesto.gankio.R;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.SwipeBackHelper;

/**
 * Created on 2016/6/3.
 * By nesto
 * From https://github.com/bushijie/ParallaxSwipeBack
 */
public abstract class SwipeBackActivity extends ActionBarActivity implements SlidingPaneLayout.PanelSlideListener {
    private SlidingPaneLayout slidingPaneLayout;
    private FrameLayout frameLayout;
    private ImageView behindImageView;
    private int defaultTranslationX = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSlidingPaneLayout();
        setViews();
        super.onCreate(savedInstanceState);
    }

    private void setSlidingPaneLayout() {
        //通过反射来改变SlidingPaneLayout的值
        try {
            slidingPaneLayout = new SlidingPaneLayout(this);
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            f_overHang.set(slidingPaneLayout, 0);
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setShadowResourceLeft(R.drawable.shadow);
            slidingPaneLayout.setSliderFadeColor(getResources().getColor(R.color.Transparent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setViews() {
        defaultTranslationX = AppUtil.dip2px(defaultTranslationX);
        // put screenshot
        behindImageView = new ImageView(this);
        // put current activity
        frameLayout = new FrameLayout(this);
        //添加两个view
        slidingPaneLayout.addView(behindImageView, 0);
        slidingPaneLayout.addView(frameLayout, 1);
    }

    @Override
    public void setContentView(int id) {
        setContentView(getLayoutInflater().inflate(id, null));
    }

    @Override
    public void setContentView(View v) {
        setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        try {
//            behindImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            behindImageView.setImageBitmap(getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setContentView(View v, ViewGroup.LayoutParams params) {
        super.setContentView(slidingPaneLayout, params);
        frameLayout.removeAllViews();
        frameLayout.addView(v, params);
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
        behindImageView.setTranslationX(v * defaultTranslationX - defaultTranslationX);
    }

    private Bitmap getBitmap() {
        File file = SwipeBackHelper.getScreenshotFile();
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}
