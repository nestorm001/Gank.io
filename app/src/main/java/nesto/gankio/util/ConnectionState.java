package nesto.gankio.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created on 2016/3/30.
 * By nesto
 */
public enum ConnectionState {
    
    NETWORK_NONE("NETWORK_NONE"),
    //wifi连接  
    NETWORK_WIFI("NETWORK_WIFI"),
    //手机网络数据连接类型  
    NETWORK_2G("NETWORK_2G"),
    NETWORK_3G("NETWORK_3G"),
    NETWORK_4G("NETWORK_4G"),
    //未知移动网络
    NETWORK_MOBILE("NETWORK_MOBILE");

    private String name;

    ConnectionState(String name) {
        this.name = name;
    }
    
    public static ConnectionState getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //如果当前没有网络  
        if (connManager == null) {
            return NETWORK_NONE;
        }

        //获取当前网络类型，如果为空，返回无网络  
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }


        switch (activeNetInfo.getType()) {
            // 判断是不是连接的是不是wifi  
            case ConnectivityManager.TYPE_WIFI:
                return NETWORK_WIFI;
            // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等  
            case ConnectivityManager.TYPE_MOBILE:
                String strSubTypeName = activeNetInfo.getSubtypeName();
                switch (activeNetInfo.getSubtype()) {
                    //如果是2g类型  
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g  
                    case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g  
                    case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g  
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORK_2G;
                    //如果是3g类型  
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g  
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORK_3G;
                    //如果是4g类型  
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_4G;
                    default:
                        //中国移动 联通 电信 三种3G制式  
                        if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                || strSubTypeName.equalsIgnoreCase("WCDMA")
                                || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            return NETWORK_3G;
                        } else {
                            return NETWORK_MOBILE;
                        }
                }
            default:
                break;
        }
        return NETWORK_NONE;
    }

    @Override
    public String toString() {
        return name;
    }
}  
