package nesto.gankio.network;

/**
 * Created on 2016/4/7.
 * By nesto
 */
public class InternetException extends RuntimeException {
    private String message;

    public InternetException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
