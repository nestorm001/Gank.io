package nesto.gankio.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nesto.gankio.global.A;

/**
 * Created on 2016/4/7.
 * By nesto
 */
public class SharedPreferenceHelper {
    
    private SharedPreferences preferences;

    private SharedPreferenceHelper() {
        preferences = PreferenceManager.getDefaultSharedPreferences(A.getContext());
    }

    private static class SingletonHolder {
        private static final SharedPreferenceHelper INSTANCE = new SharedPreferenceHelper();
    }

    public static SharedPreferenceHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return preferences.getInt(key, -1);
    }
}
