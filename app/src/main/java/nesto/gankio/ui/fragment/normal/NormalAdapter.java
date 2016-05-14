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

    public NormalAdapter(ArrayList<Data> list) {
        this.list = list;
    }

    public void setList(ArrayList<Data> list, boolean hasMore) {
        this.list = list;
        this.hasMore = hasMore;
    }

    public void add(ArrayList<Data> list) {
        this.list.addAll(list);
        hasMore = (list.size() == C.LOAD_NUM);
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
        if (data.getType().equals(DataType.BENEFIT.toString())) {
            viewHolder.title.setVisibility(View.GONE);
            viewHolder.text.setVisibility(View.GONE);
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
            setFavourite(viewHolder);
        }
        viewHolder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClicked(data);
                return true;
            }
        });
        viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavouriteClicked(data, viewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void onItemClicked(Data data) {
        Intent intent = data.getType().equals(DataType.VIDEO.toString()) ?
                new Intent(context, VideoActivity.class).putExtra(Intents.TRANS_DATA, data)
                : new Intent(context, ContentActivity.class).putExtra(Intents.TRANS_DATA, data);
        context.startActivity(intent);
    }

    private void onItemLongClicked(Data data) {
        Intent share = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, data.getUrl())
                .setType("text/html");
        context.startActivity(Intent.createChooser(share, context.getText(R.string.send_to)));
    }

    private void onFavouriteClicked(Data data, NormalViewHolder viewHolder) {
        if (viewHolder.isExist()) {
            DBHelper.getInstance().remove(data);
            viewHolder.setExist(false);
        } else {
            DBHelper.getInstance().add(data);
            viewHolder.setExist(true);
        }
        setFavourite(viewHolder);
    }

    private void setFavourite(NormalViewHolder viewHolder) {
        if (viewHolder.isExist()) {
            viewHolder.favourite.setImageResource(R.drawable.ic_action_favourited);
        } else {
            viewHolder.favourite.setImageResource(R.drawable.ic_action_favourite);
        }
    }
}
