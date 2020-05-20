package com.ln.base;

import android.content.Context;
import android.content.res.Configuration;

import androidx.multidex.MultiDexApplication;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.language.LanguageLocalListener;
import com.ln.base.tool.language.LanguageSharedPrefer;
import com.ln.base.tool.language.MultiLanguage;

import java.util.Locale;

public class AppBase extends MultiDexApplication {

    private static AppBase baseInstance;

    public static AppBase getBaseInstance() {
        return baseInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        String processName = AndroidUtils.getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(getPackageName());
            if (defaultProcess) {
                onDefaultProcessInit();
            } else {
                onOtherProcessInit(processName);
            }
        }
    }

    protected void onDefaultProcessInit() {
        baseInstance = this;
        AndroidThreeTen.init(this);
        initMultiLanguage();
    }

    protected void onOtherProcessInit(String processName) {
    }

    @Override
    protected void attachBaseContext(Context base) {
        //第一次进入app时保存系统选择语言(为了选择随系统语言时使用，如果不保存，切换语言后就拿不到了）
        LanguageSharedPrefer.getInstance(base).setSystemCurrentLocal(MultiLanguage.getSystemLocal());
        super.attachBaseContext(MultiLanguage.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //用户在系统设置页面切换语言时保存系统选择语言(为了选择随系统语言时使用，如果不保存，切换语言后就拿不到了）
        LanguageSharedPrefer.getInstance(getApplicationContext()).setSystemCurrentLocal(MultiLanguage.getSystemLocal());
        MultiLanguage.onConfigurationChanged(getApplicationContext());
    }

    /**
     * 定制化语言时，复写该方法，改变LanguageLocalListener其回调方法的Locate值
     */
    protected void initMultiLanguage() {
        MultiLanguage.init(new LanguageLocalListener() {
            @Override
            public Locale getSelectLanguageLocale(Context context) {
                //返回自己本地保存选择的语言设置
                return LanguageSharedPrefer.getInstance(context).getSystemCurrentLocal();
            }
        });
        MultiLanguage.setApplicationLanguage(this);
    }
}
