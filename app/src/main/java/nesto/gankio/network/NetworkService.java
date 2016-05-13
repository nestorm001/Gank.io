package nesto.gankio.network;

import nesto.gankio.model.Results;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created on 2016/3/15.
 * By nesto
 */
public interface NetworkService {

    @GET("data/{type}/{num}/{page}")
    Observable<Result<Results>>
    getData(@Path("type") String type,
            @Path("num") Integer num,
            @Path("page") Integer page);

    @GET("random/data/{type}/{num}")
    Observable<Result<Results>>
    getRandom(@Path("type") String type,
              @Path("num") Integer num);
}