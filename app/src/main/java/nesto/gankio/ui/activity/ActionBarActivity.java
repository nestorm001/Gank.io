package nesto.gankio.ui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewParent;
import android.view.WindowManager;

import nesto.gankio.R;

/**
 * Created on 2016/4/26.
 * By nesto
 */
public abstract class ActionBarActivity extends BaseActivity {

    protected ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initActionBar();
        setTranslucentStatusBar(R.color.colorPrimary);
    }

    public abstract void setContentView();

    public void initActionBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        setTitle(title);
    }

    public void showOnBack() {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initActionBar() {
        initActionBar(null);
    }

    public void setTitle(String title) {
        if (actionBar != null && title != null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                ViewParent view = toolbar.getParent();
                if (view instanceof CollapsingToolbarLayout) {
                    ((CollapsingToolbarLayout) view).setTitle(title);
                    return;
                }
            }
            actionBar.setTitle(title);
        }
    }

    public void setTranslucentStatusBar(int color) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(color);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTranslucentStatusBarLollipop(int color) {
        getWindow().setStatusBarColor(getResources().getColor(color));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatusBarKiKat() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}
