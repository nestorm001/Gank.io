package nesto.gankio.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nesto.gankio.global.A;
import nesto.gankio.global.C;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + C.FAVOURITE_TABLE + " (" +
                    C.ID + " TEXT PRIMARY KEY," +
                    C.ORDER + " INTEGER," +
                    C.VALUE + " TEXT)";
    private static final String SQL_DROP = "DROP TABLE IF EXISTS " + C.FAVOURITE_TABLE;

    public SQLiteHelper() {
        super(A.getContext(), C.FAVOURITE_TABLE, null, C.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }
}
