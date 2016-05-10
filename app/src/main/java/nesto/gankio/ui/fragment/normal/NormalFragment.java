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

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.global.C;
import nesto.gankio.model.DataType;
import nesto.gankio.model.Results;
import nesto.gankio.network.ErrorHandlerHelper;
import nesto.gankio.network.HttpMethods;
import nesto.gankio.util.LogUtil;
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

    private int pageNum = 0;
    private boolean isRefreshing = false;

    public NormalFragment setType(DataType type) {
        this.type = type;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.bind(this, view);
        NormalAdapter adapter = new NormalAdapter(container.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setListener();
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NormalAdapter adapter = (NormalAdapter) recyclerView.getAdapter();
        if (adapter.getItemCount() == 0) {
            getData();
        }
    }

    private void getData() {
        isRefreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        Action1<Results> onNext = new Action1<Results>() {
            @Override
            public void call(Results results) {
                NormalAdapter adapter = (NormalAdapter) recyclerView.getAdapter();
                adapter.add(results.getResults());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                isRefreshing = false;
            }
        };
        Action1<Throwable> onError = new ErrorHandlerHelper().createOnError(null);
        HttpMethods.getInstance().getData(onNext, onError, type.toString(), C.LOAD_NUM, ++pageNum);
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
                LogUtil.d("lastVisibleItem " + lastVisibleItem);
                NormalAdapter adapter = (NormalAdapter) recyclerView.getAdapter();
                if (lastVisibleItem >= adapter.getItemCount() - 5
                        && adapter.getItemCount() > 0) {
                    if (adapter.isHasMore() && !isRefreshing) {
                        getData();
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNum = 0;
        ((NormalAdapter) recyclerView.getAdapter()).clearData();
        getData();
    }
}
