package nesto.gankio.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/5/9.
 * By nesto
 */
public enum DataType {
    @SerializedName("福利")
    BENEFIT("福利"),
    @SerializedName("休息视频")
    VIDEO("休息视频"),
    @SerializedName("拓展资源")
    RESOURCE("拓展资源"),
    @SerializedName("瞎推荐")
    RECOMMEND("瞎推荐"),
    @SerializedName("Android")
    ANDROID("Android"),
    @SerializedName("iOS")
    IOS("iOS");

    private String name;

    DataType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
