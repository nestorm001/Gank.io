package nesto.gankio.db;

import nesto.gankio.model.Data;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class DBHelper {
    private DBHelper dbHelper;

    private DBHelper() {
        //TODO
    }

    private static class SingletonHolder {
        private static final DBHelper INSTANCE = new DBHelper();
    }

    public static DBHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void add(Data data) {
        //TODO 
    }

    public void remove(Data data) {
        //TODO 
    }

    public boolean isExist(Data data) {
        //TODO 
        return true;
    }
}
