package nesto.gankio.network;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import nesto.gankio.R;
import nesto.gankio.global.A;
import nesto.gankio.util.ConnectionState;
import nesto.gankio.util.LogUtil;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.functions.Func1;

/**
 * 用来统一处理Http的result,抛出errorCode
 *
 * @param <T> Subscriber真正需要的数据类型
 */
public class HttpResultFunc<T> implements Func1<Result<T>, T> {
    private Context context = A.getContext();

    @Override
    public T call(Result<T> httpResult) {
        if (httpResult.isError()) {
            handleInternetError(httpResult.error());
            LogUtil.d(httpResult.error().getLocalizedMessage());
        }
        Response<T> response = httpResult.response();
        if (!response.isSuccessful()) {
            String errorMessage;
            try {
                //似乎error body里面才是服务器给的错误信息
                Error error = new Gson().fromJson(response.errorBody().string(), Error.class);
                errorMessage = error.toString();
            } catch (IOException e) {
                errorMessage = context.getString(R.string.unknown_error);
            }
            throw new HttpException(response.code(), errorMessage);
        }
        return response.body();
    }

    private void handleInternetError(Throwable error) {
        if (error instanceof ConnectException) {
            boolean hasNetworkConnect =
                    ConnectionState.getNetworkState(A.getContext())
                            != ConnectionState.NETWORK_NONE;
            if (hasNetworkConnect) {
                throw new InternetException(context.getString(R.string.cannot_connect_to_server));
            } else {
                throw new InternetException(context.getString(R.string.no_internet));
            }
        } else if (error instanceof SocketTimeoutException) {
            throw new InternetException(context.getString(R.string.time_out));
        } else {
            throw new InternetException(context.getString(R.string.unknown_error));
        }
    }
}
