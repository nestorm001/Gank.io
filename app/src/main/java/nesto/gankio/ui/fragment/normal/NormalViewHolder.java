package nesto.gankio.ui.fragment.normal;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import nesto.gankio.R;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class NormalViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.card_view)
    public CardView item;
    @Bind(R.id.image)
    public ImageView image;
    @Bind(R.id.title)
    public TextView title;
    @Bind(R.id.text)
    public TextView text;
    @Bind(R.id.favourite)
    public ImageButton favourite;

    private boolean isExist = false;

    public NormalViewHolder(View convertView) {
        super(convertView);
        ButterKnife.bind(this, convertView);
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }
}
