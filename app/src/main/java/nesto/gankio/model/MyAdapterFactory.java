package nesto.gankio.model;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 * Created on 2016/8/8.
 * By nesto
 */
@GsonTypeAdapterFactory
public abstract class MyAdapterFactory implements TypeAdapterFactory{
    public static TypeAdapterFactory create() {
        return new AutoValueGson_MyAdapterFactory();
    }
}
