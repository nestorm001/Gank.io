package nesto.gankio.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import nesto.gankio.BuildConfig;
import nesto.gankio.global.Host;
import nesto.gankio.model.Data;
import nesto.gankio.model.DataType;
import nesto.gankio.model.MyAdapterFactory;
import nesto.gankio.model.Results;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 2016/3/23.
 * By nesto
 */
public class HttpMethods {

    public static final String BASE_URL = Host.HOST;
    private static final int DEFAULT_TIMEOUT = 30;

    private NetworkService networkService;

    private HashMap<String, Subscription> requestList = new HashMap<>();

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //debug模式下打出请求日志
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClientBuilder.addInterceptor(logging);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(MyAdapterFactory.create())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        networkService = retrofit.create(NetworkService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 用于取消请求
     * 原理为
     * 想要取消请求的时候调用subscriber的unsubscribe方法就可以了
     */
    public void cancelRequest(String tag) {
        requestList.get(tag).unsubscribe();
        requestList.remove(tag);
    }

    public void cancelAllRequest() {
        for (Map.Entry<String, Subscription> entry : requestList.entrySet()) {
            entry.getValue().unsubscribe();
            requestList.remove(entry.getKey());
        }
    }

    //当前有相同请求任务时，取消前一任务
    public void addRequest(String tag, Subscription subscription) {
        if (requestList.containsKey(tag)) {
            cancelRequest(tag);
        }
        requestList.put(tag, subscription);
    }

    public <T> Observable.Transformer<T, T> setThreads() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public Subscription getData(Action0 doOnSubscribe, Action1<Results> onNext, Action1<Throwable> onError, String type,
                                Integer num, Integer page) {
        return networkService.getData(type, num, page)
                .map(new HttpResultFunc<Results>())
                .compose(this.<Results>setThreads())
                .doOnSubscribe(doOnSubscribe)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public Subscription getOneRandomPicture(Action1<String> onNext, Action1<Throwable> onError) {
        final int loadNum = 20;
//        return networkService.getRandom(DataType.BENEFIT.toString(), loadNum)
        int page = new Random().nextInt(10) + 1;
        return networkService.getData(DataType.BENEFIT.toString(), loadNum, page)
                .map(new HttpResultFunc<Results>())
                .compose(this.<Results>setThreads())
                .map(new Func1<Results, String>() {
                    @Override
                    public String call(Results results) {
                        ArrayList<Data> datas = results.getResults();
                        if (!datas.isEmpty()) {
                            return datas.get(new Random().nextInt(loadNum)).url();
                        }
                        return null;
                    }
                })
                .subscribe(onNext, onError);
    }

    public Subscription getRandomPictureUrls(Action1<Results> onNext, Action1<Throwable> onError) {
        final int loadNum = 20;
        // 随机很有问题的感觉，不如直接随机页数
        int page = new Random().nextInt(10) + 1;
        return networkService.getData(DataType.BENEFIT.toString(), loadNum, page)
                .map(new HttpResultFunc<Results>())
                .compose(this.<Results>setThreads())
                .subscribe(onNext, onError);
    }
}
