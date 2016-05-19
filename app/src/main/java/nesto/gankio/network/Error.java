package nesto.gankio.network;

/**
 * Created on 2016/4/7.
 * By nesto
 */
public class Error {
    final private String message;

    public Error(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return message;
    }
}
