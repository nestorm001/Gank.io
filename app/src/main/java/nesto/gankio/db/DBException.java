package nesto.gankio.db;

/**
 * Created on 2016/5/14.
 * By nesto
 */
public class DBException extends RuntimeException {
    public DBException(String detailMessage) {
        super(detailMessage);
    }
}
