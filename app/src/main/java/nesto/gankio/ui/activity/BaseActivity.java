package nesto.gankio.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import nesto.gankio.R;
import nesto.gankio.global.A;
import nesto.gankio.ui.MvpView;
import nesto.gankio.util.LogUtil;


/**
 * Created on 2015/10/15 14:38
 */
@SuppressWarnings("unused")
public class BaseActivity extends AppCompatActivity implements MvpView {

    public static final int ANIMATION_DURATION = 500;

    private ProgressDialog progressDialog;

    private Snackbar snackbar;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A.getInstance().addActivity(this);
        context = this;
    }

    protected void hideSystemUI() {
        View mDecorView = getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
    }

    //取消全屏模式
    protected void showSystemUI() {
        View mDecorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        A.getInstance().finishActivity(this);
        A.getInstance().removePreviousActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        A.getInstance().addPreviousActivity(this);
        enterAnimation();
    }


    public void enterAnimation() {
        LogUtil.d("enterAnimation");
        this.overridePendingTransition(R.anim.anim_enter_fade_in, R.anim.anim_enter_fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitAnimation();
    }

    public void exitAnimation() {
        LogUtil.d("exitAnimation");
        this.overridePendingTransition(R.anim.anim_exit_fade_in, R.anim.anim_exit_fade_out);
    }

    public void showProcessDialog(String title) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setProgress(0);
        }
        progressDialog.setTitle(title);
        progressDialog.show();
    }

    public void setDialogProgress(int progress) {
        if (progressDialog == null) {
            showProcessDialog(null);
        }
        progressDialog.setProgress(progress);
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    public void showSnackbar(View view, String text, String actionName, View.OnClickListener listener, Snackbar.Callback callback) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.White));
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            snackbar.setText(text);
        }

        if (listener != null) {
            snackbar.setAction(actionName, listener);
        }
        if (callback != null) {
            snackbar.setCallback(callback);
        }

        snackbar.show();
    }

    public void showSnackbar(View view, String text, String actionName, View.OnClickListener listener) {
        showSnackbar(view, text, actionName, listener, null);
    }

    public void showSnackbar(View view, String text) {
        showSnackbar(view, text, null, null, null);
    }

    protected void setVisibilityVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            LogUtil.d("setVisibilityVisible " + view.toString());
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            showViewAnimation(view);
        }
    }

    protected void setVisibilityGone(final View view) {
        if (view.getVisibility() != View.GONE) {
            LogUtil.d("setVisibilityGone " + view.toString());
            view.setAlpha(1);
            showHideAnimation(view);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.GONE);
                }

            }, ANIMATION_DURATION);
        }
    }

    protected void showViewAnimation(View view) {
        ViewPropertyAnimatorCompat viewAnimator = ViewCompat.animate(view)
                .alpha(1)
                .setStartDelay(0)
                .setDuration(ANIMATION_DURATION);
        viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
    }

    protected void showHideAnimation(View view) {
        ViewPropertyAnimatorCompat viewAnimator = ViewCompat.animate(view)
                .alpha(0)
                .setStartDelay(0)
                .setDuration(ANIMATION_DURATION);
        viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
    }

    public void showAnimation() {
        initForAnimation();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            toolbar.getViewTreeObserver().removeOnPreDrawListener(this);
                            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

                            toolbar.measure(widthSpec, heightSpec);
                            collapseToolbar(toolbar);
                            return true;
                        }
                    });
        }
    }

    private void collapseToolbar(final Toolbar toolbar) {
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int toolBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;  // 屏幕高度（像素）

        ValueAnimator valueHeightAnimator = ValueAnimator.ofInt(height, toolBarHeight);
        valueHeightAnimator.setDuration(300);
        valueHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                lp.height = (Integer) animation.getAnimatedValue();
                toolbar.setLayoutParams(lp);
            }
        });

        valueHeightAnimator.start();
        valueHeightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                doAfterAnimation();
            }
        });
    }

    protected void initForAnimation() {

    }

    protected void doAfterAnimation() {

    }

    @Override
    public Context getContext() {
        return this;
    }
}

