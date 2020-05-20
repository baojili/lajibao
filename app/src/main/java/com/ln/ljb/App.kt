package com.ln.ljb

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.ln.base.AppBase
import com.ln.base.network.HttpCacheWrapper
import com.ln.base.network.RetrofitWrapper
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.activity.MainActivity
import com.ln.ljb.constant.Config
import com.ln.ljb.tools.LocationManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent


class App : AppBase() {

    companion object {
        var instance: App? = null
    }

    override fun onDefaultProcessInit() {
        initBugly()
        super.onDefaultProcessInit()
        instance = this
        initLog()
        //initCrash()
        initRetrofit()
        initLocation()
        initUmeng()
    }

    private fun initBugly() {
        Beta.largeIconId = R.mipmap.ic_logo
        Beta.smallIconId = R.mipmap.ic_logo
        Beta.defaultBannerId = R.drawable.bg_upgrade_banner
        Beta.canShowUpgradeActs.add(MainActivity::class.java)
        Beta.autoDownloadOnWifi = false
        Beta.canShowApkInfo = false
        Beta.upgradeDialogLayoutId = R.layout.dialog_upgrade
        Bugly.init(this, Config.BUGLY_APP_ID, Config.DEBUG)
    }

    private fun initLocation() {
        LocationManager.getInstance().init(this, object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                Log.e(JsonUtils.toJsonViewStr(location?.address))
                Config.BD_ADDRESSS = location?.address
                /*val addr: String = location!!.addrStr //获取详细地址信息
                val country: String = location.getCountry() //获取国家
                val province: String = location.getProvince() //获取省份
                val city: String = location.getCity() //获取城市
                val district: String = location.getDistrict() //获取区县
                val street: String = location.getStreet() //获取街道信息*/
            }
        })
        LocationManager.getInstance().start()
    }

    private fun initUmeng() {
        UMConfigure.init(
            this,
            Config.U_MENG_APP_KEY,
            "ln",
            UMConfigure.DEVICE_TYPE_PHONE,
            Config.U_MENG_MSG_SECRET
        )
        //获取消息推送代理示例
        val mPushAgent = PushAgent.getInstance(this)
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {
            override fun onSuccess(deviceToken: String) { //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.e("注册成功：deviceToken：-------->  $deviceToken")
                Config.DEVICE_TOKEN = deviceToken
            }

            override fun onFailure(s: String, s1: String) {
                Log.e("注册失败：-------->  s:$s,s1:$s1")
            }
        })
    }

    private fun initLog() {
        Log.disableLog(!BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) {
            Log.isLoggable("ln", android.util.Log.VERBOSE, true)
        } else {
            Log.isLoggable("ln", android.util.Log.INFO, false)
        }
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .tag(Config.LOG_TAG)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return if (BuildConfig.DEBUG) {
                    true
                } else {
                    priority >= Logger.INFO
                }
            }
        })
        /*FormatStrategy csvFormatStrategy = JdxCsvFormatStrategy.newBuilder()
                .tag(Config.LOG_TAG)
                .logPath(StorageUtils.getCustomFilesDirectory(this, "logs").getAbsolutePath())
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(csvFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                if (BuildConfig.DEBUG) {
                    return true;
                } else {
                    return false;
                }
            }
        });*/
    }

    private fun initRetrofit() { /*Map<String,String> headMap = new HashMap<>();
        headMap.put("appID","Config.ROBOT_WE_CHAT_APP_ID");*/
        RetrofitWrapper.getInstance().init(Config.SERVER_HOST)
        HttpCacheWrapper.instance().initDiskCache(this)
    }
}