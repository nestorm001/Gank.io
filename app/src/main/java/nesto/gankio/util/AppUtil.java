package nesto.gankio.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nesto.gankio.R;
import nesto.gankio.global.A;
import nesto.gankio.model.Data;

/**
 * Created on 2016/4/8.
 * By nesto
 */
public class AppUtil {

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    public @interface Duration {
    }

    private static Toast toast = null;

    public static void showToast(String text, @Duration int length) {
        //先检查是否在主线程中运行，再进行处理
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Context context = A.getContext();
            if (toast == null) {
                toast = Toast.makeText(context, text, length);
            } else {
                toast.setText(text);
                toast.setDuration(length);
            }
            toast.show();
        }
    }

    public static void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static String getCurrentTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        return dateFormat.format(now);
    }

    public static boolean checkPhoneNum(String input) {
        Pattern p = Pattern.compile("^(?:(?:\\+|00)?(86|886|852|853))?(1\\d{10})$");
        Matcher m = p.matcher(input.replaceAll("-", ""));
        boolean isPhoneNumber = true;
        // group中 0为原字符串 
        // 1为匹配到的移动电话国家/地区码 
        // 2为国内有效号码
        while (m.find()) {
            isPhoneNumber = (m.group(2) != null);
        }
        return m.matches() && isPhoneNumber;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取文件名称 拍照时调用
     *
     * @param path dir path
     * @return image path
     */
    public static String getPictureName(String path) {
        String pictureName = "";
        String state = Environment.getExternalStorageState();// 获取sd卡根目录
        if (!state.equals(Environment.MEDIA_MOUNTED)) {// 检测sd卡是否可以读写
            showToast("can't find your storage!", Toast.LENGTH_LONG);
        }
        File directory = new File(path);
        if (!directory.exists()) {// 判断文件（夹）是否存在
            Log.v("takePicture", "Making directory.");
            if (!directory.mkdirs()) {// 创建一个文件（夹），如不成功toast
                showToast("can't create folder", Toast.LENGTH_LONG);
            }
        }
        pictureName = getCurrentTime() + ".jpg";

        return path + File.separator + pictureName;
    }

    /**
     * 添加动态扫描库
     *
     * @param absolutePath 绝对路径
     */
    public static void addToMedia(final String absolutePath) {
        final Context context = A.getContext();
        if (absolutePath == null)
            return;
        Uri data = Uri.parse(absolutePath);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
        new MediaScannerConnection.MediaScannerConnectionClient() {// 添加扫描库
            MediaScannerConnection msc = null;

            {
                msc = new MediaScannerConnection(context, this);
                msc.connect();
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                msc.disconnect();
            }

            @Override
            public void onMediaScannerConnected() {
                msc.scanFile(absolutePath, null);
            }
        };
    }

    public static void acquireScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void releaseScreenOn(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void onShareClicked(Data data, Context context) {
        Intent share = new Intent(Intent.ACTION_SEND)
                //学习知乎，各种应用不接收title，所有内容全放在text里面
                .putExtra(Intent.EXTRA_TEXT, data.getDesc() + " " + data.getUrl())
                .setType("text/html");
        context.startActivity(Intent.createChooser(share, context.getText(R.string.send_to)));
    }
}
