package nesto.gankio.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;

import nesto.gankio.R;
import nesto.gankio.global.A;
import nesto.gankio.global.C;
import nesto.gankio.model.Data;
import nesto.gankio.network.HttpMethods;
import nesto.gankio.util.AppUtil;
import nesto.gankio.util.LogUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class DBHelper {
    private Gson gson;
    private BriteDatabase db;
    private ArrayList<Data> favouriteList;

    private DBHelper() {
        gson = new Gson();
        favouriteList = new ArrayList<>();
        db = SqlBrite.create().wrapDatabaseHelper(new SQLiteHelper(), Schedulers.io());
    }

    private static class SingletonHolder {
        private static final DBHelper INSTANCE = new DBHelper();
    }

    public static DBHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Observable<Object> add(final Data data) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if (favouriteList.contains(data)) {
                    LogUtil.d("data already exists");
                    subscriber.onError(new DBException(A.getContext().getString(R.string.already_exists)));
                } else {
                    favouriteList.add(0, data);
                    BriteDatabase.Transaction transaction = db.newTransaction();
                    try {
                        db.insert(C.FAVOURITE_TABLE, makeData(favouriteList.size() - 1, data));
//                    throw new RuntimeException("hehe");
                        transaction.markSuccessful();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    } finally {
                        transaction.end();
                        subscriber.onCompleted();
                    }
                }
            }
        }).compose(HttpMethods.getInstance().setThreads())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        if (throwable instanceof DBException) {
                            AppUtil.showToast(throwable.getLocalizedMessage());
                        } else {
                            favouriteList.remove(0);
                            AppUtil.showToast(A.getContext().getString(R.string.fail_to_add_to_favourite));
                        }
                    }
                });
    }

    public Observable<Object> remove(final Data data) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                int start = favouriteList.indexOf(data);
                favouriteList.remove(data);
                int end = favouriteList.size();
                BriteDatabase.Transaction transaction = db.newTransaction();
                try {
                    //ugly
                    db.delete(C.FAVOURITE_TABLE, C.ID + " = '" + data.get_id() + "'");
                    for (int i = 0; i < start; i++) {
                        db.update(C.FAVOURITE_TABLE,
                                makeData(end - 1 - i, favouriteList.get(i)),
                                C.ID + " = '" + favouriteList.get(i).get_id() + "'");
                    }
//                    throw new RuntimeException("hehe");
                    transaction.markSuccessful();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    transaction.end();
                    subscriber.onCompleted();
                }
            }
        }).compose(HttpMethods.getInstance().setThreads())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        AppUtil.showToast(A.getContext().getString(R.string.fail_to_remove_from_favourite));
                    }
                });
    }

    public Observable<Object> move(final int from, final int to) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                BriteDatabase.Transaction transaction = db.newTransaction();
                try {
                    int start = from < to ? from : to;
                    int end = from > to ? from : to;
                    for (int i = start; i < end + 1; i++) {
                        db.update(C.FAVOURITE_TABLE,
                                makeData(favouriteList.size() - i - 1, favouriteList.get(i)),
                                C.ID + " = '" + favouriteList.get(i).get_id() + "'");
                    }
//                    throw new RuntimeException("hehe");
                    transaction.markSuccessful();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    transaction.end();
                    subscriber.onCompleted();
                }
            }
        }).compose(HttpMethods.getInstance().setThreads());
    }

    public boolean isExist(Data data) {
        return favouriteList.contains(data);
    }

    public Observable<ArrayList<Data>> getAll() {
        return db.createQuery(C.FAVOURITE_TABLE, "SELECT * FROM " + C.FAVOURITE_TABLE +
                " ORDER BY " + C.ORDER + " DESC")
                .map(new Func1<SqlBrite.Query, ArrayList<Data>>() {
                    @Override
                    public ArrayList<Data> call(SqlBrite.Query query) {
                        Cursor cursor = query.run();
                        if (cursor == null || !cursor.moveToFirst()) {
                            throw new DBException("no result");
                        }
                        favouriteList.clear();
                        do {
                            int name = cursor.getColumnIndex(C.VALUE);
                            favouriteList.add(toData(cursor.getString(name)));
                        } while (cursor.moveToNext());
                        return favouriteList;
                    }
                }).first()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private ContentValues makeData(int order, Data data) {
        ContentValues values = new ContentValues();
        values.put(C.ID, data.get_id());
        values.put(C.VALUE, toJson(data));
        values.put(C.ORDER, order);
        return values;
    }

    private String toJson(Data data) {
        return gson.toJson(data);
    }

    private Data toData(String json) {
        return gson.fromJson(json, Data.class);
    }

    public ArrayList<Data> getFavouriteList() {
        return favouriteList;
    }
}
