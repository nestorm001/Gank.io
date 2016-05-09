package nesto.gankio.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created on 2016/4/26.
 * By nesto
 */
public class DateTimeUtil {

    public static String toData(String timeUTC) {
        String srcFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        String dstFormat = "yyyy年MM月dd日";
        return convertTimeString(timeUTC, srcFormat, dstFormat);
    }

    public static String toTime(String timeUTC) {
        String srcFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        String dstFormat = "HH:mm";
        return convertTimeString(timeUTC, srcFormat, dstFormat);
    }

    private static String convertTimeString(String src, String srcFormat, String dstFormat) {
        SimpleDateFormat srcFormatter = new SimpleDateFormat(srcFormat, Locale.getDefault());
        SimpleDateFormat dstFormatter = new SimpleDateFormat(dstFormat, Locale.getDefault());
        Date date;
        try {
            date = srcFormatter.parse(src);
        } catch (ParseException e) {
            return "";
        }
        return dstFormatter.format(date);
    }

    public static boolean isSameData(String dataString1, String format1, String dataString2, String format2) {
//        String dstFormat = "yyyy年MM月dd日";
        SimpleDateFormat formatter1 = new SimpleDateFormat(format1, Locale.getDefault());
        SimpleDateFormat formatter2 = new SimpleDateFormat(format2, Locale.getDefault());
        Date date1;
        Date date2;
        try {
            date1 = formatter1.parse(dataString1);
            date2 = formatter2.parse(dataString2);
            return date1.equals(date2);
        } catch (ParseException e) {
            return false;
        }
    }
}
