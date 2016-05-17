package nesto.gankio.ui.activity.favourite;

import android.content.Context;

import java.util.ArrayList;

import nesto.gankio.R;
import nesto.gankio.db.DBHelper;
import nesto.gankio.model.Data;
import nesto.gankio.ui.fragment.normal.NormalAdapter;
import nesto.gankio.ui.fragment.normal.NormalViewHolder;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;
import rx.Subscriber;

/**
 * Created on 2016/5/15.
 * By nesto
 */
public class FavouriteAdapter extends NormalAdapter {
    public FavouriteAdapter(Context context, ArrayList<Data> list) {
        super(context, list);
    }

    @Override
    protected void removeFromFavourite(final Data data, final NormalViewHolder viewHolder, final int position) {
        DBHelper.getInstance()
                .remove(data)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        //其实用了同一个data对象，为了刷新列表状态，需要重设状态，使其有变化，暂时没啥更好的办法
                        data.setFavoured(true);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
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
}
