package nesto.gankio.ui.fragment.normal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.global.C;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import nesto.gankio.model.Results;
import nesto.gankio.network.ErrorHandler;
import nesto.gankio.network.ErrorHandlerHelper;
import nesto.gankio.network.HttpMethods;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created on 2016/5/4.
 * By nesto
 */
public class NormalFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private DataType type;

    private NormalAdapter adapter;

    private int pageNum = 0;
    private boolean isLoading = false;
    //控件是否已经初始化
    private boolean isCreateView = false;
    //是否已经加载过数据
    private boolean isNoData = true;

    private Subscription subscription;

    public NormalFragment setType(DataType type) {
        this.type = type;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NormalAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.bind(this, view);
        initRecycleView();
        setListener();
        isCreateView = true;
        return view;
    }

    private void initRecycleView() {
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(alphaAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setItemAnimator(new LandingAnimator());

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyLoad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavouriteState();
    }

    private void refreshFavouriteState() {
        if (adapter.getItemCount() != 0) {
            ArrayList<Data> list = adapter.getList();
            for (int i = 0; i < list.size(); i++) {
                Data data = list.get(i);
                boolean isExist = DBHelper.getInstance().isExist(data);
                if (data.isFavoured() != isExist) {
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    private void lazyLoad() {
        //如果没有加载过就加载，否则就不再加载了
        if (adapter.getItemCount() == 0) {
            //加载数据操作
            onRefresh();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoad();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isCreateView = false;
        if (subscription != null) {
            subscription.unsubscribe();
        }
        swipeRefreshLayout.setRefreshing(false);
        ButterKnife.unbind(this);
    }

    private void getData() {
        Action0 doOnSubscribe = new Action0() {
            @Override
            public void call() {
                isLoading = true;
                swipeRefreshLayout.setRefreshing(true);
            }
        };
        Action1<Results> onNext = new Action1<Results>() {
            @Override
            public void call(Results results) {
                if (isNoData) {
                    adapter.clearData();
                }
                adapter.add(results.getResults());
                setRefreshFinished();
            }
        };
        Action1<Throwable> onError = new ErrorHandlerHelper().createOnError(new ErrorHandler() {
            @Override
            public void doAfterHandle() {
                setRefreshFinished();
            }
        });
        subscription = HttpMethods.getInstance().getData(doOnSubscribe, onNext, onError, type.toString(), C.LOAD_NUM, ++pageNum);
    }

    private void setRefreshFinished() {
        swipeRefreshLayout.setRefreshing(false);
        isLoading = false;
        isNoData = false;
    }

    private void setListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (lastVisibleItem >= adapter.getItemCount() - 5
                        && adapter.getItemCount() > 0) {
                    if (adapter.isHasMore() && !isLoading) {
                        getData();
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNum = 0;
        isNoData = true;
        // avoid exception in instant run
        if (type != null) {
            getData();
        }
    }
}
