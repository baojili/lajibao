package com.ln.ljb.tools;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationManager {

    private static LocationManager mLocationManager;

    private LocationClient mLocationClient = null;

    public LocationManager() {
    }

    public static LocationManager getInstance() {
        if (mLocationManager == null) {
            synchronized (LocationManager.class) {
                if (mLocationManager == null)
                    mLocationManager = new LocationManager();
            }
        }
        return mLocationManager;
    }

    public void init(Context context, BDAbstractLocationListener bdAbstractLocationListener) {
        mLocationClient = new LocationClient(context.getApplicationContext());
        mLocationClient.registerLocationListener(bdAbstractLocationListener);
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    public void start() {
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }
}
