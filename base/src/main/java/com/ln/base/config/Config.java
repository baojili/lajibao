package com.ln.base.config;

import com.ln.base.BuildConfig;

public class Config {

    public static final boolean DEBUG = BuildConfig.ENABLE_DEBUG;

    public static final String OS_TYPE = "1";

    public static final String APP_STORAGE_DIR = "ln/base";

    public static final String APP_IMAGE_SAVE_DIR = APP_STORAGE_DIR + "/image";

    public static final String APP_SHARE_LOGO_URL = "http://baidu.com/app_logo.png";

    public static final int PHOTO_COMPRESS_WIDTH = 720;
    public static final int PHOTO_COMPRESS_HEIGHT = 1280;

    public static final int PHOTO_COMPRESS_WIDTH_BIG = 1080;
    public static final int PHOTO_COMPRESS_HEIGHT_BIG = 1920;

    public static final int PHOTO_REQUEST = 1000;
    public static final int PERMISSION_REQUEST_CODE = 1001;

    public static final String DEFAULT_SHARED_PREFERENCES = "settings";


}