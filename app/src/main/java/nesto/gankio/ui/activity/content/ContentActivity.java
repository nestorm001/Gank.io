package nesto.gankio.ui.activity.content;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

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

    private Data data;

    private ContentPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ContentPresenter();
        presenter.attachView(this);
        ButterKnife.bind(this);
        load();
        showOnBack();
        presenter.getRandomPicture();
        setTranslucentStatusBar(R.color.Transparent);
    }

    private void load() {
        data = getIntent().getParcelableExtra(Intents.TRANS_DATA);
        if (data != null) {
            webView.loadUrl(data.getUrl());
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_content);
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
}
