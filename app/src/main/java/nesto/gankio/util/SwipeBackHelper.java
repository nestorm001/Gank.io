package nesto.gankio.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nesto.gankio.global.A;
import nesto.gankio.ui.activity.SwipeBackActivity;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created on 2016/6/4.
 * By nesto
 */
public class SwipeBackHelper {

    public static Bitmap bitmap;

    public static void screenshots(Activity activity, boolean isFullScreen) {
        //TODO not good enough
        //View是你需要截图的View
        View decorView = activity.getWindow().getDecorView().getRootView();
        decorView.buildDrawingCache();
        decorView.setDrawingCacheEnabled(true);
        Bitmap window = decorView.getDrawingCache();
        // 获取屏幕长和高 Get screen width and height
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        if (isFullScreen) {
            bitmap = Bitmap.createBitmap(window, 0, 0, width, height);
        } else {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            bitmap = Bitmap.createBitmap(window, 0, statusBarHeight, width, height - statusBarHeight);
        }
        decorView.destroyDrawingCache();
        saveScreenshot();
    }

    private static void saveScreenshot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(getScreenshotFile());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static File getScreenshotFile() {
        final String windowBitmap = "screenshots.jpg";
        return new File(A.getContext().getCacheDir(), windowBitmap);
    }

    public static void startSwipeActivity(Activity activity, Intent intent) {
        startSwipeActivity(activity, intent, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT));
    }

    public static void startSwipeActivity(final Activity activity, final Intent intent, final boolean isFullScreen) {
        Observable.create(new Observable.OnSubscribe<Class>() {
            @Override
            public void call(Subscriber<? super Class> subscriber) {
                String className = intent.getComponent().getClassName();
                if (className != null) {
                    try {
                        subscriber.onNext(Class.forName(className));
                        subscriber.onCompleted();
                    } catch (ClassNotFoundException e) {
                        subscriber.onError(e);
                    }
                }
            }
        }).map(new Func1<Class, Boolean>() {
            @Override
            public Boolean call(Class activity) {
                if (SwipeBackActivity.class.isAssignableFrom(activity)) {
                    return true;
                }
                throw new RuntimeException("activity not instance of SwipeBackActivity");
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                activity.startActivity(intent);
            }

            @Override
            public void onNext(Boolean b) {
                screenshots(activity, isFullScreen);
                activity.startActivity(intent);
            }
        });
    }
}
