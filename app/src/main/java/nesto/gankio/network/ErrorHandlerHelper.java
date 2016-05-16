package nesto.gankio.network;

import android.support.annotation.Nullable;

import nesto.gankio.util.AppUtil;
import nesto.gankio.util.L;
import rx.functions.Action1;

/**
 * Created on 2016/4/8.
 * By nesto
 */
public class ErrorHandlerHelper {

    public Action1<Throwable> createOnError(@Nullable final ErrorHandler handler) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                boolean shouldContinue = true;
                if (handler != null) {
                    shouldContinue = handler.doBeforeHandle(throwable);
                }
                if (shouldContinue) {
                    handle(throwable, handler);
                }
                if (handler != null) {
                    handler.doAfterHandle();
                }
            }
        };
    }

    private void handle(Throwable throwable, ErrorHandler handler) {
        handle(throwable, handler, true);
    }

    private void handle(Throwable throwable, @Nullable ErrorHandler handler, boolean shouldHandle401) {
        if (throwable instanceof InternetException) {
            AppUtil.showToast(throwable.getMessage());
        } else if (throwable instanceof HttpException) {
            L.e("getErrorCode:" + ((HttpException) throwable).getErrorCode());
            L.e("getMessage:" + throwable.getLocalizedMessage());
            boolean isUnauthorized = ((HttpException) throwable).getErrorCode() == 401;
            boolean is404 = ((HttpException) throwable).getErrorCode() == 404;
            if (isUnauthorized && shouldHandle401) {
                // do nothing
            } else if (!is404) {
                AppUtil.showToast(((HttpException) throwable).getErrorCode() + ": " + throwable.getMessage());
            } else {
                if (handler != null) {
                    handler.dealWith404();
                }
            }
        } else {
            L.e("error:" + throwable);
            AppUtil.showToast(throwable.getLocalizedMessage());
        }
    }
}
