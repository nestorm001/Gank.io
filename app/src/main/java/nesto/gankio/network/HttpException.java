package nesto.gankio.network;

/**
 * Created on 2016/4/6.
 * By nesto
 */
public class HttpException extends RuntimeException {

    private int errorCode;
    private String message;

    public HttpException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
