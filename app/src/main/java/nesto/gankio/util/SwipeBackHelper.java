package nesto.gankio.util;

import android.app.Activity;

import java.util.Stack;

/**
 * Created on 2016/6/5.
 * By nesto
 */
public class SwipeBackHelper {
    private Stack<Activity> goBackStack;

    private SwipeBackHelper() {
        goBackStack = new Stack<>();
    }

    private static class SingletonHolder {
        private static final SwipeBackHelper INSTANCE = new SwipeBackHelper();
    }

    public static SwipeBackHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Activity getPreviousActivity() {
        if (!goBackStack.empty()) {
            return goBackStack.peek();
        } else {
            return null;
        }
    }

    public void removePreviousActivity() {
        if (!goBackStack.empty()) {
            goBackStack.pop();
        }
    }

    public void addPreviousActivity(Activity activity) {
        goBackStack.push(activity);
    }
}
