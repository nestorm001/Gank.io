package nesto.gankio.ui.fragment.normal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.global.C;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public class NormalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ArrayList<Data> list;
    private boolean hasMore = true;

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

    private void initItemView(final int position, NormalViewHolder viewHolder) {
        Data data = list.get(position);
        if (data.getType().equals(DataType.BENEFIT.toString())) {
            Picasso.with(context)
                    .load(data.getUrl())
                    .into(viewHolder.image);
        } else {
            viewHolder.title.setText(data.getDesc());
            viewHolder.text.setText(data.getWho());
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.text)
        TextView text;

        public NormalViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
