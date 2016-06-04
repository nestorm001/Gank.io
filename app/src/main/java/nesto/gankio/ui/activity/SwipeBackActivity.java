package nesto.gankio.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Field;

import nesto.gankio.R;
import nesto.gankio.util.AppUtil;

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
    private int shadowWidth = 20;

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
            slidingPaneLayout.setSliderFadeColor(getResources().getColor(R.color.Transparent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setViews() {
        defaultTranslationX = AppUtil.dip2px(defaultTranslationX);
        shadowWidth = AppUtil.dip2px(shadowWidth);
        //behindFrameLayout
        FrameLayout behindFrameLayout = new FrameLayout(this);
        behindImageView = new ImageView(this);
        behindImageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        behindFrameLayout.addView(behindImageView, 0);

        //containerLayout
        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);
        containerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() + shadowWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        //you view container
        frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //add shadow
        ImageView shadowImageView = new ImageView(this);
        shadowImageView.setBackgroundResource(R.drawable.shadow);
        shadowImageView.setLayoutParams(new LinearLayout.LayoutParams(shadowWidth, LinearLayout.LayoutParams.MATCH_PARENT));
        containerLayout.addView(shadowImageView);
        containerLayout.addView(frameLayout);
        containerLayout.setTranslationX(-shadowWidth);
        //添加两个view
        slidingPaneLayout.addView(behindFrameLayout, 0);
        slidingPaneLayout.addView(containerLayout, 1);
    }

    @Override
    public void setContentView(int id) {
        setContentView(getLayoutInflater().inflate(id, null));
    }

    @Override
    public void setContentView(View v) {
        setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        try {
            behindImageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
        //duang duang duang 你可以在这里加入很多特效
        behindImageView.setTranslationX(v * defaultTranslationX - defaultTranslationX);
    }

    private Bitmap getBitmap() {
        File file = AppUtil.getScreenshotFile();
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}
