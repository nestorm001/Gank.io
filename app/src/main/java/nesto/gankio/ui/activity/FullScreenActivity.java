package nesto.gankio.ui.activity;

/**
 * Created on 2016/4/25.
 * By nesto
 */
public class FullScreenActivity extends BaseActivity {
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        showSystemUI();
        super.onPause();
    }
}
