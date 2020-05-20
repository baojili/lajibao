package com.ln.base.tool.language;

import android.content.Context;

import java.util.Locale;

public interface LanguageLocalListener {

    /**
     * 获取选择设置语言
     *
     * @param context
     * @return
     */
    Locale getSelectLanguageLocale(Context context);
}
