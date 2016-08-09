package nesto.gankio.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created on 2016/5/9.
 * By nesto
 */
@AutoValue
public abstract class Data implements Serializable{
//    _id: "56cc6d23421aa95caa707c56",
//    createdAt: "2015-08-07T01:21:33.518Z",
//    desc: "8.7——（2）",
//    publishedAt: "2015-08-07T03:57:47.229Z",
//    type: "福利",
//    url: "http://ww2.sinaimg.cn/large/7a8aed7bgw1eutsd0pgiwj20go0p0djn.jpg",
//    used: true,
//    who: "张涵宇"

    public abstract String _id();

    @SerializedName("createdAt")
    public abstract String createdAt();

    public abstract String desc();

    @SerializedName("publishedAt")
    public abstract String publishedAt();

    public abstract String type();

    public abstract String url();

    public abstract String who();

    public abstract boolean used();

    public abstract Builder toBuilder();

    private boolean favoured = false;

    public static TypeAdapter<Data> typeAdapter(Gson gson) {
        return new AutoValue_Data.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_Data.Builder()
                .createdAt("")
                .publishedAt("")
                .used(true)
                .who("");
    }

    public Data withDesc(String desc) {
        return toBuilder().desc(desc).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder _id(String _id);

        public abstract Builder createdAt(String createdAt);

        public abstract Builder desc(String desc);

        public abstract Builder publishedAt(String publishedAt);

        public abstract Builder type(String type);

        public abstract Builder url(String url);

        public abstract Builder who(String who);

        public abstract Builder used(boolean used);

        public abstract Data build();
    }

    public boolean isFavoured() {
        return favoured;
    }

    public void setFavoured(boolean favoured) {
        this.favoured = favoured;
    }
}
