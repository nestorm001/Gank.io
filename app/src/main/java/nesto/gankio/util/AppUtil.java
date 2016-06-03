package nesto.gankio.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nesto.gankio.R;
import nesto.gankio.global.A;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
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
    public static int dip2px(float dpValue) {
        final float scale = A.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = A.getContext().getResources().getDisplayMetrics().density;
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
        if (data.getType().equals(DataType.BENEFIT.toString())) {
            shareImage(data, context);
        } else {
            shareUrl(data, context);
        }
    }

    private static void shareUrl(Data data, Context context) {
        Intent share = new Intent(Intent.ACTION_SEND)
                //学习知乎，各种应用不接收title，所有内容全放在text里面
                .putExtra(Intent.EXTRA_TEXT, data.getDesc() + " " + data.getUrl())
                .setType("text/html");
        context.startActivity(Intent.createChooser(share, context.getText(R.string.send_to)));
    }


    private static void shareImage(final Data data, final Context context) {
        createBitmapObservable(data.getUrl(), context)
                .map(new Func1<Bitmap, Uri>() {
                    @Override
                    public Uri call(Bitmap bitmap) {
                        Uri bmpUri;
                        try {
                            File file = saveBitmapFile(context, "image", bitmap);
                            bmpUri = FileProvider.getUriForFile(context, "nesto.gankio.fileprovider", file);
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                        return bmpUri;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showToast(context.getString(R.string.dealing_with_image));
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Uri uri) {
                        Intent share = new Intent(Intent.ACTION_SEND)
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                .putExtra(Intent.EXTRA_STREAM, uri)
                                .setType("image/jpeg");
                        context.startActivity(Intent.createChooser(share, context.getText(R.string.send_to)));
                    }
                });
    }

    public static Observable<Bitmap> createBitmapObservable(final String url, final Context context) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = Picasso.with(context).load(url).get();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static File saveBitmapFile(Context context, String imageName, Bitmap bitmap) throws IOException {
        File file = new File(context.getCacheDir() + File.separator + "images",
                imageName + ".png");
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.close();
        return file;
    }

    public static void screenshots(Activity activity, boolean isFullScreen) {
        try {
            //View是你需要截图的View
            View decorView = activity.getWindow().getDecorView();
            decorView.setDrawingCacheEnabled(true);
            decorView.buildDrawingCache();
            Bitmap b1 = decorView.getDrawingCache();
            // 获取状态栏高度 /
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            // 获取屏幕长和高 Get screen width and height
            int width = activity.getWindowManager().getDefaultDisplay().getWidth();
            int height = activity.getWindowManager().getDefaultDisplay().getHeight();
            // 去掉标题栏 Remove the statusBar Height
            Bitmap bitmap;
            if (isFullScreen) {
                bitmap = Bitmap.createBitmap(b1, 0, 0, width, height);
            } else {
                bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
            }
            decorView.destroyDrawingCache();
            FileOutputStream out = new FileOutputStream(getScreenshotFile());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getScreenshotFile() {
        final String windowBitmap = "screenshots.jpg";
        return new File(A.getContext().getCacheDir(), windowBitmap);
    }

    public static void startSwipeActivity(Activity activity, Intent intent) {
        startSwipeActivity(activity, intent, false);
    }

    public static void startSwipeActivity(Activity activity, Intent intent, boolean isFullScreen) {
        screenshots(activity, isFullScreen);
        activity.startActivity(intent);
    }
}
