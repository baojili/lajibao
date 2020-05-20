package com.ln.ljb.constant;

import com.baidu.location.Address;
import com.ln.ljb.BuildConfig;
import com.ln.ljb.model.UserInfo;

public class Config extends com.ln.base.config.Config {

    //异常重启的最小间隔；当发生异常时，本次发生的时间与上次异常发生的时间间隔大于该值，程序会尝试重新启动，否则直接退出
    public static final long RELAUNCH_TIME_GAP_MINI = 30000;//30秒

    //API地址
    public static final String SERVER_HOST = BuildConfig.SERVER_HOST;


    public static final String SERVICE_URL = SERVER_HOST + ApiPath.USER_SERVICE.path();
    public static final String PRIVACY_URL = SERVER_HOST + ApiPath.USER_PRIVACY.path();

    //默认API分页请求大小
    public static final int PAGE_SIZE = 10;

    //默认全局Log的tag
    public static final String LOG_TAG = "ln";
    //we chat 的 appId
    public static final String WE_CHAT_APP_ID = "wx17cae10e994904b1";
    //Umeng信息
    //public static final String U_MENG_APP_KEY = "5df306d2570df349db0008ca";
    //public static final String U_MENG_MSG_SECRET = "a398bfd8f41c92af4d813e760c052bdd";
    public static final String U_MENG_APP_KEY = "5df6282b4ca3578341000dcc";
    public static final String U_MENG_MSG_SECRET = "049063098bc0d0e49c0305d36ae3136d";
    public static Address BD_ADDRESSS = null;
    public static String WE_CHAT_CODE = null;
    public static UserInfo USER_INFO = null;
    public static String DEVICE_TOKEN = null;

    //Buly
    public static String BUGLY_APP_ID = "269fe39f75";
}