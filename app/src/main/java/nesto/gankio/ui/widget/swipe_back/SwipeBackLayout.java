package nesto.gankio.ui.widget.swipe_back;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.MotionEvent;

import java.lang.reflect.Field;

/**
 * Created on 2016/6/5.
 * By nesto
 */
public class SwipeBackLayout extends SlidingPaneLayout {

    public static final int EDGE_SIZE = 150;

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            return ev.getX() < EDGE_SIZE;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
