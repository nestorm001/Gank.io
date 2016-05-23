package nesto.gankio.ui.activity.favourite;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.model.Data;
import nesto.gankio.ui.activity.ActionBarActivity;
import rx.Subscriber;

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
    private FavouriteAdapter adapter;
    private Intent intent;

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

        recyclerView.setItemAnimator(new LandingAnimator());

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
                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置重新交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(adapter.getList(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(adapter.getList(), i, i - 1);
                    }
                }
                DBHelper.getInstance()
                        .move(fromPosition, toPosition)
                        .subscribe(new Subscriber<Object>() {
                            @Override
                            public void onCompleted() {
                                
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (fromPosition < toPosition) {
                                    //分别把中间所有的item的位置重新交换
                                    for (int i = fromPosition; i < toPosition; i++) {
                                        Collections.swap(adapter.getList(), i + 1, i);
                                    }
                                } else {
                                    for (int i = fromPosition; i > toPosition; i--) {
                                        Collections.swap(adapter.getList(), i - 1, i);
                                    }
                                }
                            }

                            @Override
                            public void onNext(Object o) {

                            }
                        });
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.getList().remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callBack);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void addItem() {
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        adapter.notifyItemRangeChanged(2, adapter.getItemCount());
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
}
