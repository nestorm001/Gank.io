package nesto.gankio.ui.activity;

import nesto.gankio.util.AppUtil;

/**
 * Created on 2016/4/25.
 * By nesto
 */
public abstract class FullScreenActivity extends BaseActivity {
    @Override
    protected void onResume() {
        super.onResume();
        AppUtil.acquireScreenOn(this);
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        showSystemUI();
        AppUtil.releaseScreenOn(this);
        super.onPause();
    }
}
