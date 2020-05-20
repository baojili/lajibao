package com.ln.base.network;

import com.ln.base.BuildConfig;

public class Config {
    public static final int LIST_REQ_PAGE_START_NUM = 0;
    public static final String PAGE_SIZE = "pageSize";
    //public static final String PAGE_NUM = "pageNum";
    public static final String TOTAL_NUM = "total";
    //public static final String CODE = "code";
    //public static final String MSG = "msg";
    //public static final String DATA = "data";
    public static final String PAGE_NUM = "pageNo";
    public static final String BASE = "info";
    public static final String CODE = "errCode";
    public static final String MSG = "errMsg";
    public static final String DATA = "data";
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void isDebug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean isDebug() {
        return DEBUG;
    }
}