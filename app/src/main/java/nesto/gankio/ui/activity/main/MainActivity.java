package nesto.gankio.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import nesto.gankio.ui.activity.ActionBarActivity;
import nesto.gankio.ui.activity.favourite.FavouriteActivity;
import nesto.gankio.ui.fragment.normal.NormalFragment;
import nesto.gankio.util.L;
import rx.functions.Action1;


public class MainActivity extends ActionBarActivity {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

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

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    private void loadFavourite() {
        DBHelper.getInstance()
                .getAll()
                .subscribe(new Action1<ArrayList<Data>>() {
                    @Override
                    public void call(ArrayList<Data> datas) {
                        L.d("收藏夹加载完成");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e(throwable.getLocalizedMessage());
                    }
                }).unsubscribe();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourite:
                startActivity(new Intent(this, FavouriteActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
