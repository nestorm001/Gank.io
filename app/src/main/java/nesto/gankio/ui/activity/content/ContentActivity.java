package nesto.gankio.ui.activity.content;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.global.Intents;
import nesto.gankio.model.Data;
import nesto.gankio.ui.activity.ActionBarActivity;

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

    private Data data;

    private ContentPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatusBar(R.color.Transparent);
        presenter = new ContentPresenter();
        presenter.attachView(this);
        ButterKnife.bind(this);
        initWebView();
        load();
        setTitle(data.getType());
        showOnBack();
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
                progressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
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
    }

    private void load() {
        data = getIntent().getParcelableExtra(Intents.TRANS_DATA);
        if (data != null) {
            webView.loadUrl(data.getUrl());
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
        Picasso.with(this)
                .load(url)
                .into(image);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}