package nesto.gankio.ui;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;

import java.lang.reflect.Field;

/**
 * Created on 2016/6/5.
 * By nesto
 */
public class SwipeBackLayout extends SlidingPaneLayout {

    public SwipeBackLayout(Context context) {
        super(context);
        //通过反射来改变SlidingPaneLayout的值
        try {
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            f_overHang.set(this, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
