package nesto.gankio.util;

import android.util.Log;

import nesto.gankio.BuildConfig;


/**
 * Created by Jack_long on 2015/7/17.
 * debug模式下打印日志
 */
public class L {
    public static final String TAG = "wtf";

    public static void d(String logInfo) {
        d(TAG, logInfo);
    }

    private static void d(String tag, String logInfo) {
        if (BuildConfig.DEBUG && tag != null && logInfo != null) {
            Log.d(tag, logInfo);
        }
    }

    public static void e(String logInfo) {
        e(TAG, logInfo);
    }

    private static void e(String tag, String logInfo) {
        if (BuildConfig.DEBUG && tag != null && logInfo != null) {
            Log.d(tag, getTraceInfo());
            Log.e(tag, logInfo);
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
}
