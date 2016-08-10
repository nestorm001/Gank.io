package nesto.gankio.util;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import nesto.gankio.BuildConfig;


@SuppressWarnings("PointlessBooleanExpression")
public class LogUtil {
    public static final String TAG = "wtf";

    public static void d(Object logInfo) {
        d(TAG, logInfo);
    }

    public static void d(String tag, Object logInfo) {
        if (BuildConfig.DEBUG && tag != null && logInfo != null) {
//        if (true) {
            Log.d(tag, String.valueOf(logInfo));
        }
    }

    public static void e(Object logInfo) {
        e(TAG, logInfo);
    }

    public static void e(String tag, Object logInfo) {
        if (BuildConfig.DEBUG && tag != null && logInfo != null) {
//        if (true) {
//            Log.e(tag, getTraceInfo());
            Log.e(tag, String.valueOf(logInfo));
        }
    }

    private static String getTraceInfo() {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        if (stacksLen > 3) {
            StackTraceElement element = stacks[3];
            sb.append("here is the log from class: ").append(element.getClassName())
                    .append("; method: ").append(element.getMethodName())
                    .append("; number: ").append(element.getLineNumber());
        }
        return sb.toString();
    }

    public static void printLog(String log) {
        String name = "";
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        if (stacksLen > 2) {
            String[] names = stacks[1].getClassName().split("\\.");
            name = names[names.length - 1];
        }
        d(TAG, name + ": " + log);
    }

    public static void logWithThread(String log) {
        d("call on " + Thread.currentThread().getName() + " " + log);
    }

    public static void logRect(Rect rect) {
        d(rect.toString() + " height: " + rect.height() + " width: " + rect.width());
    }

    public static void logBitmap(Bitmap bitmap) {
        d("bitmap.getWidth(): " + bitmap.getWidth() + " bitmap.getHeight(): " + bitmap.getHeight());
    }
}
