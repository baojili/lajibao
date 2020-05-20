package com.ln.ljb.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ln.base.activity.WebViewActivity
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R


class LoginActivity : WeChatLoginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.e("login")
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view!!.id) {
            R.id.ll_we_chat_login -> {
                sendWeChatAuth()
            }
            R.id.ll_mobile_login -> {
                startActivity(Intent(this@LoginActivity, MobileLoginActivity::class.java))
                finish()
            }
            R.id.tv_service -> {
                WebViewActivity.launch(
                    this@LoginActivity,
                    getString(R.string.service_protocol_extra),
                    com.ln.ljb.constant.Config.SERVICE_URL
                )
            }
            R.id.tv_privacy -> {
                WebViewActivity.launch(
                    this@LoginActivity,
                    getString(R.string.privacy_policy_extra),
                    com.ln.ljb.constant.Config.PRIVACY_URL
                )
            }

        }
    }

    override fun onGetWeChatCode(weChatCode: String) {
        super.onGetWeChatCode(weChatCode)
        loginByWeChatCode(weChatCode)
    }

    private fun getUserInfo(userId: Long) {
        val userIdInfo: HashMap<String, Long> = hashMapOf()
        userIdInfo["id"] = userId
        val baseReq: BaseReq<HashMap<String, Long>> =
            BaseReq(userIdInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_INFO.path()).addParams(baseReq)
                .build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                updateUserInfo(result.data)
            }

        }.post()
    }

    private fun loginByWeChatCode(weChatCode: String) {
        val userIdInfo: HashMap<String, String> = hashMapOf()
        userIdInfo["code"] = weChatCode
        val baseReq: BaseReq<HashMap<String, String>> =
            BaseReq(userIdInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_LOGIN_WX.path())
                .addParams(baseReq).build()
        object :
            HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo?>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                showToast(R.string.login_success)
                auth.id = result.data?.id
                updateUserInfo(result.data)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }.post()
    }
}
