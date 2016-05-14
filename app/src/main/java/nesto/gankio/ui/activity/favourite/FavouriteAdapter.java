package nesto.gankio.ui.activity.favourite;

import android.content.Context;
import android.view.View;

import nesto.gankio.ui.fragment.normal.NormalAdapter;
import nesto.gankio.ui.fragment.normal.NormalViewHolder;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public class FavouriteAdapter extends NormalAdapter {
    
    public FavouriteAdapter(Context context) {
        super(context);
    }

    @Override
    protected void initItemView(final int position, NormalViewHolder viewHolder) {
        viewHolder.favourite.setVisibility(View.INVISIBLE);
        super.initItemView(position, viewHolder);
    }
}
