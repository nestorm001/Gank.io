package nesto.gankio.global;

import nesto.gankio.model.Data;

/**
 * Created on 2016/5/10.
 * By nesto
 */
public class C {
    public static final int LOAD_NUM = 20;

    public static final String FAVOURITE_TABLE = "gank_favourite";
    public static final String ID = "id";
    public static final String VALUE = "value";
    public static final String ORDER = "item_order";
    public static final int DB_VERSION = 1;

    public static final String FROM_SHARE = "收藏";

    public static final String REPO_URL = "https://github.com/nestorm001/Gank.io";
    public static final String NESTO = "nesto";
    public static final String TYPE = "nesto";
    public static final String DESCRIPTION = "the repo of this app";
    public static final Data TITLE_DATA = new Data(NESTO, DESCRIPTION, REPO_URL, TYPE);
}
