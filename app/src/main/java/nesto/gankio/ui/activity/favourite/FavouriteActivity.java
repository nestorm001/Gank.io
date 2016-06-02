package nesto.gankio.ui.activity.favourite;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.model.Data;
import nesto.gankio.ui.activity.ActionBarActivity;
import nesto.gankio.ui.activity.main.MainActivity;
import nesto.gankio.util.AppUtil;
import rx.Subscriber;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class FavouriteActivity extends ActionBarActivity
        implements FavouriteMvpView,
        FavouriteAdapter.OnDeleteListener {

    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private FavouritePresenter presenter;
    private FavouriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initView();
        showOnBack();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_favourite);
    }

    private void init() {
        presenter = new FavouritePresenter();
        presenter.attachView(this);
        adapter = new FavouriteAdapter(this, DBHelper.getInstance().getFavouriteList());
        adapter.setOnDeleteListener(this);
        setTitle(getString(R.string.favourite_list));
        presenter.loadFavourite(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        presenter.loadFavourite(intent);
    }

    private void initView() {
        swipeRefreshLayout.setEnabled(false);

        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(alphaAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dealSwipeAndDrag();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void dealSwipeAndDrag() {
        ItemTouchHelper.Callback callBack = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                final int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                final Data from = adapter.getList().remove(fromPosition);
                adapter.getList().add(toPosition, from);
                DBHelper.getInstance()
                        .move(fromPosition, toPosition)
                        .subscribe(new Subscriber<Object>() {
                            @Override
                            public void onCompleted() {
                                adapter.notifyItemMoved(fromPosition, toPosition);
                            }

                            @Override
                            public void onError(Throwable e) {
                                AppUtil.showToast(getString(R.string.swap_failed));
                                adapter.getList().remove(from);
                                adapter.getList().add(fromPosition, from);
                            }

                            @Override
                            public void onNext(Object o) {

                            }
                        });
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Data data = adapter.getList().get(position);
                adapter.notifyItemRemoved(position);
                DBHelper.getInstance()
                        .remove(data)
                        .subscribe(new Subscriber<Object>() {
                            @Override
                            public void onCompleted() {
                                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                showSnackbar(data);
                            }

                            @Override
                            public void onError(Throwable e) {
                                adapter.getList().add(position, data);
                                adapter.notifyItemInserted(position);
                            }

                            @Override
                            public void onNext(Object o) {

                            }
                        });
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callBack);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void addItem() {
        adapter.notifyItemInserted(0);
        adapter.notifyItemRangeChanged(1, adapter.getItemCount());
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void showInputDialog(final Data data) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.input_region, null);
        final EditText text = (EditText) view.findViewById(R.id.text);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (text != null) {
                    String title = text.getText().toString().trim();
                    if (!title.isEmpty()) {
                        data.setDesc(text.getText().toString());
                        presenter.addToFavourite(data);
                    }
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onDelete(Data data) {
        showSnackbar(data);
    }

    @Override
    public void backToMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getContext(), MainActivity.class));
                finish();
            }
        }, 1000);

    }

    private void showSnackbar(final Data data) {
        showSnackbar(recyclerView, getString(R.string.need_cancel_hint), getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addToFavourite(data);
            }
        });
    }
}
