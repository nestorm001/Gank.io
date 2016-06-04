package nesto.gankio.ui.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.global.C;
import nesto.gankio.global.Intents;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import nesto.gankio.ui.activity.ActionBarActivity;
import nesto.gankio.ui.activity.content.ContentActivity;
import nesto.gankio.ui.activity.favourite.FavouriteActivity;
import nesto.gankio.ui.activity.image_view.ImageViewActivity;
import nesto.gankio.ui.fragment.normal.NormalFragment;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;
import nesto.gankio.util.SwipeBackHelper;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;


public class MainActivity extends ActionBarActivity {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        loadFavourite();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        showAnimation();
    }

    private void setTitleLink() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(context, ContentActivity.class)
                            .putExtra(Intents.TRANS_DATA, C.TITLE_DATA);
                    SwipeBackHelper.startSwipeActivity((Activity) context, intent);
                    return true;
                }
            });
        } else {
            LogUtil.d("title not found");
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
        setTitleLink();
    }

    private void loadFavourite() {
        subscription = DBHelper.getInstance()
                .getAll()
                .subscribe(new Action1<ArrayList<Data>>() {
                    @Override
                    public void call(ArrayList<Data> datas) {
                        LogUtil.d("收藏夹加载完成");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(throwable.getLocalizedMessage());
                        subscription.unsubscribe();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        subscription.unsubscribe();
                    }
                });
    }

    @Override
    protected void initForAnimation() {
        super.initForAnimation();
        tabLayout.setAlpha(0);
    }

    @Override
    protected void doAfterAnimation() {
        super.doAfterAnimation();
        ViewCompat.animate(tabLayout).alpha(1).start();
    }

    private void setupViewPager(ViewPager viewPager) {
        final MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager());
        for (DataType type : DataType.values()) {
            adapter.addFragment(new NormalFragment().setType(type), type.toString());
        }
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getSupportActionBar() != null) {
                    setTitle(adapter.getPageTitle(Math.round(position + positionOffset)));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.view_image).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourite:
                SwipeBackHelper.startSwipeActivity(this, new Intent(this, FavouriteActivity.class));
                break;
            case R.id.view_image:
                if (isSucceed()) {
                    startActivity(new Intent(this, ImageViewActivity.class));
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Random random = new Random();

    private boolean isSucceed() {
        if (random.nextInt(100) > 90) {
            return true;
        } else {
            AppUtil.showToast(getString(R.string.fun_hint));
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
