package nesto.gankio.ui.activity.content;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.global.Intents;
import nesto.gankio.model.Data;
import nesto.gankio.ui.activity.ActionBarActivity;
import nesto.gankio.util.AppUtil;

/**
 * Created on 2016/5/11.
 * By nesto
 * TODO a lot more to do
 */
public class ContentActivity extends ActionBarActivity implements ContentMvpView {

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    @Bind(R.id.scroll)
    NestedScrollView scrollView;

    @Bind(R.id.app_bar)
    AppBarLayout appBarLayout;

    private MenuItem favourite;

    private ContentPresenter presenter;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatusBar(R.color.Transparent);
        presenter = new ContentPresenter();
        presenter.attachView(this);
        ButterKnife.bind(this);
        initWebView();
        load();
        showOnBack();
        initAppBarLayout();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_content);
    }

    private void initWebView() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(progressBar.getProgress() == 100 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                scrollView.scrollTo(0, 0);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);

        progressBar.setInterpolator(new LinearInterpolator());
    }

    private void initAppBarLayout() {
        // use an ugly way to solve the problem that the actionbar is cut at the beginning
        appBarLayout.post(new Runnable() {
            @Override
            public void run() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                if (toolbar != null) {
                    toolbar.setFitsSystemWindows(true);
                    
                    Rect statusBar = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(statusBar);
                    int statusBarHeight = statusBar.top;
                    
                    FrameLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
                    layoutParams.setMargins(0, statusBarHeight, 0, 0);
                }
            }
        });
    }

    private void load() {
        data = getIntent().getParcelableExtra(Intents.TRANS_DATA);
        if (data != null) {
            webView.loadUrl(data.getUrl());
            setTitle(data.getType());
        }
        presenter.getRandomPicture();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void show(String url) {
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                if (toolbar != null) {
                    toolbar.setFitsSystemWindows(false);
                    FrameLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
                }
            }

            @Override
            public void onError() {

            }
        };
        Picasso.with(this)
                .load(url)
                .into(image, callback);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            //可以后退，暂时不需要
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void onFavouriteClicked() {
        if (data.isFavoured()) {
            presenter.removeFromFavourite(data);
            data.setFavoured(false);
        } else {
            presenter.addToFavourite(data);
            data.setFavoured(true);
        }
        setFavourite(data);
    }

    @Override
    public void setFavourite(Data data) {
        if (data != null && data.isFavoured()) {
            favourite.setIcon(R.drawable.ic_action_favourited);
        } else {
            favourite.setIcon(R.drawable.ic_action_favourite);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(true);
        favourite = menu.findItem(R.id.favourite);
        setFavourite(data);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourite:
                onFavouriteClicked();
                break;
            case R.id.share:
                AppUtil.onShareClicked(data, getContext());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
