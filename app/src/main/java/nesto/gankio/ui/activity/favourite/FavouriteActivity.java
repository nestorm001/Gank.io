package nesto.gankio.ui.activity.favourite;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.model.Data;
import nesto.gankio.ui.activity.ActionBarActivity;
import nesto.gankio.ui.fragment.normal.NormalAdapter;
import nesto.gankio.util.LogUtil;
import rx.functions.Action1;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class FavouriteActivity extends ActionBarActivity implements FavouriteMvpView {

    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private FavouritePresenter presenter;
    private NormalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initView();
        loadData();
        showOnBack();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_favourite);
    }

    private void init() {
        presenter = new FavouritePresenter();
        presenter.attachView(this);
        adapter = new NormalAdapter(this);
        setTitle(getString(R.string.favourite_list));
    }

    private void initView() {
        swipeRefreshLayout.setEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        DBHelper.getInstance()
                .getAll()
                .subscribe(new Action1<ArrayList<Data>>() {
                    @Override
                    public void call(ArrayList<Data> datas) {
                        //TODO
                        LogUtil.d(datas.toString());
                        adapter.add(datas);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //TODO
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
