package nesto.gankio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public class Data implements Parcelable {
//    _id: "56cc6d23421aa95caa707c56",
//    createdAt: "2015-08-07T01:21:33.518Z",
//    desc: "8.7——（2）",
//    publishedAt: "2015-08-07T03:57:47.229Z",
//    type: "福利",
//    url: "http://ww2.sinaimg.cn/large/7a8aed7bgw1eutsd0pgiwj20go0p0djn.jpg",
//    used: true,
//    who: "张涵宇"

    private String _id;
    @SerializedName("createdAt")
    private String createdAt;
    private String desc;
    @SerializedName("publishedAt")
    private String publishedAt;
    private String type;
    private String url;
    private String who;
    private boolean used;
    private boolean favoured = false;

    public Data(String _id, String createdAt, String desc, String publishedAt, String type, String url, String who, boolean used) {
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.type = type;
        this.url = url;
        this.who = who;
        this.used = used;
    }

    public Data(String _id, String desc, String url, String type) {
        this._id = _id;
        this.desc = desc;
        this.url = url;
        this.type = type;
    }

    public String get_id() {
        return _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getWho() {
        return who;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isFavoured() {
        return favoured;
    }

    public void setFavoured(boolean favoured) {
        this.favoured = favoured;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return _id.equals(data._id);
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.createdAt);
        dest.writeString(this.desc);
        dest.writeString(this.publishedAt);
        dest.writeString(this.type);
        dest.writeString(this.url);
        dest.writeString(this.who);
        dest.writeByte(this.used ? (byte) 1 : (byte) 0);
        dest.writeByte(this.favoured ? (byte) 1 : (byte) 0);
    }

    protected Data(Parcel in) {
        this._id = in.readString();
        this.createdAt = in.readString();
        this.desc = in.readString();
        this.publishedAt = in.readString();
        this.type = in.readString();
        this.url = in.readString();
        this.who = in.readString();
        this.used = in.readByte() != 0;
        this.favoured = in.readByte() != 0;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
