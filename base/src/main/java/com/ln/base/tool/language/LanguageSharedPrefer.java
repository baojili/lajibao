package com.ln.base.tool.language;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class LanguageSharedPrefer {

    private static volatile LanguageSharedPrefer instance;
    private final String SP_NAME = "language_setting";
    private final String TAG_SELECT_LANGUAGE = "language_select";
    private final String TAG_SYSTEM_LANGUAGE = "system_language";
    private final SharedPreferences mSharedPreferences;

    private Locale systemCurrentLocal = Locale.ENGLISH;


    public LanguageSharedPrefer(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static LanguageSharedPrefer getInstance(Context context) {
        if (instance == null) {
            synchronized (LanguageSharedPrefer.class) {
                if (instance == null) {
                    instance = new LanguageSharedPrefer(context);
                }
            }
        }
        return instance;
    }

    public void saveLanguage(Language language) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_SELECT_LANGUAGE, language.code);
        edit.commit();
    }

    public int getSelectLanguage() {
        return mSharedPreferences.getInt(TAG_SELECT_LANGUAGE, 0);
    }

    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }
}
