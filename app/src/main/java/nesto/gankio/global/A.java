package nesto.gankio.global;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.Stack;

import nesto.gankio.util.LogUtil;


/**
 * Created on 2016/3/15.
 * By nesto
 */
public class A extends Application {
    private static A instance;
    private Stack<Activity> activityStack;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static A getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getBaseContext();
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        LogUtil.d("finishAllActivity: " + String.valueOf(activityStack.size()));
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void exit() {
        finishAllActivity();
        System.exit(0);
    }
}
