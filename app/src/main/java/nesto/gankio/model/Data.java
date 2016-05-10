package nesto.gankio.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public class Data {
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

    @Override
    public String toString() {
        return "Data{" +
                "_id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", who='" + who + '\'' +
                ", used=" + used +
                '}';
    }
}
