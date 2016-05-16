package nesto.gankio.ui.fragment.normal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.global.C;
import nesto.gankio.global.Intents;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import nesto.gankio.ui.activity.content.ContentActivity;
import nesto.gankio.ui.activity.video.VideoActivity;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;
import rx.Subscriber;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public class NormalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context context;

    protected ArrayList<Data> list;
    protected boolean hasMore = true;

    public NormalAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public NormalAdapter(Context context, ArrayList<Data> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(ArrayList<Data> list, boolean hasMore) {
        this.list = list;
        this.hasMore = hasMore;
    }

    public ArrayList<Data> getList() {
        return list;
    }

    public void add(ArrayList<Data> list) {
        this.list.addAll(list);
        hasMore = (list.size() == C.LOAD_NUM);
        notifyDataSetChanged();
    }

    public void clearData() {
        list.clear();
        this.hasMore = false;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new NormalViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        initItemView(position, (NormalViewHolder) viewHolder);
    }

    protected void initItemView(final int position, final NormalViewHolder viewHolder) {
        final Data data = list.get(position);
        viewHolder.title.setVisibility(View.GONE);
        viewHolder.text.setVisibility(View.GONE);
        viewHolder.image.setVisibility(View.GONE);
        data.setFavoured(DBHelper.getInstance().isExist(data));
        setFavourite(data, viewHolder);
        if (data.getType().equals(DataType.BENEFIT.toString())) {
            viewHolder.image.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(data.getUrl())
                    .into(viewHolder.image);
        } else {
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.text.setVisibility(View.VISIBLE);
            viewHolder.title.setText(data.getDesc());
            viewHolder.text.setText(data.getWho());
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked(data);
                }
            });
        }
        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClicked(data);
            }
        });
        viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavouriteClicked(data, viewHolder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void onItemClicked(Data data) {
        Intent intent = data.getType().equals(DataType.VIDEO.toString()) ?
                new Intent(context, VideoActivity.class).putExtra(Intents.TRANS_DATA, data) :
                new Intent(context, ContentActivity.class).putExtra(Intents.TRANS_DATA, data);
        context.startActivity(intent);
    }

    private void onShareClicked(Data data) {
        Intent share = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TITLE, data.getDesc())
                .putExtra(Intent.EXTRA_TEXT, data.getUrl())
                .setType("text/html");
        context.startActivity(Intent.createChooser(share, context.getText(R.string.send_to)));
    }

    protected void onFavouriteClicked(Data data, NormalViewHolder viewHolder, int position) {
        if (data.isFavoured()) {
            removeFromFavourite(data, viewHolder, position);
            data.setFavoured(false);
        } else {
            addToFavourite(data, viewHolder, position);
            data.setFavoured(true);
        }
        setFavourite(data, viewHolder);
    }

    protected void addToFavourite(final Data data, final NormalViewHolder viewHolder, int position) {
        DBHelper.getInstance()
                .add(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getLocalizedMessage());
                        AppUtil.showToast(context.getString(R.string.fail_to_add_to_favourite));
                        data.setFavoured(false);
                        setFavourite(data, viewHolder);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    protected void removeFromFavourite(final Data data, final NormalViewHolder viewHolder, int position) {
        DBHelper.getInstance()
                .remove(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getLocalizedMessage());
                        AppUtil.showToast(context.getString(R.string.fail_to_remove_from_favourite));
                        data.setFavoured(false);
                        setFavourite(data, viewHolder);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    protected void setFavourite(Data data, NormalViewHolder viewHolder) {
        if (data.isFavoured()) {
            viewHolder.favourite.setImageResource(R.drawable.ic_action_favourited);
        } else {
            viewHolder.favourite.setImageResource(R.drawable.ic_action_favourite);
        }
    }
}
