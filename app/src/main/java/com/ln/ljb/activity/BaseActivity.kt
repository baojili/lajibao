package com.ln.ljb.activity

import com.ln.base.activity.BaseActivity
import com.ln.base.tool.JsonUtils

open class BaseActivity : BaseActivity() {

    fun updateUserInfo(userInfo: com.ln.ljb.model.UserInfo?) {
        com.ln.ljb.constant.Config.USER_INFO = userInfo
        if (userInfo == null) {
            settings.edit().remove(com.ln.ljb.constant.SharedPreferKey.KEY_AUTH).commit()
        } else {
            settings.edit().putString(
                com.ln.ljb.constant.SharedPreferKey.KEY_AUTH,
                JsonUtils.toJson(userInfo)
            ).commit()
        }
    }

}
