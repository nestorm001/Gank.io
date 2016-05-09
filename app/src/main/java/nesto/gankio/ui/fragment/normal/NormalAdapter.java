package nesto.gankio.ui.fragment.normal;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import nesto.gankio.R;
import nesto.gankio.model.Data;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public class NormalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Data> list;

    public NormalAdapter() {
        this.list = Collections.emptyList();
    }

    public NormalAdapter(List<Data> list) {
        this.list = list;
    }

    public void setList(List<Data> list, boolean hasMore) {
        this.list = list;
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
        //TODO
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {

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
