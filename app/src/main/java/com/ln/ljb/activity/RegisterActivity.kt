package com.ln.ljb.activity

import android.os.Bundle
import android.view.View
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R

class RegisterActivity : com.ln.ljb.activity.BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.e("register")
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        if (view!!.id == R.id.bt_login_mobile) {
            //registerRequest("18773233021", "1234","0000","")
            getUserInfo(1)
            //loginWithWxRequest("JL_Len")
            //updateUserInfo()
            //getSMSMsgRequest("17375720935",0,1)
            //startActivity(Intent(this@RegisterActivity,TMsgActivity::class.java))
        }
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
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
                showToast("failed")
            }
        }.post()
    }


    private fun loginWithWxRequest(wxId: String) {
        val userWxInfo: HashMap<String, String> = hashMapOf()
        userWxInfo["wxId"] = wxId
        val baseReq: BaseReq<HashMap<String, String>> =
            BaseReq(userWxInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_LOGIN_WX.path())
                .addParams(baseReq).build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
                showToast("failed")
            }
        }.post()
    }

    private fun updateUserInfoRequest(userInfo: com.ln.ljb.model.UserInfo) {
        val baseReq: BaseReq<com.ln.ljb.model.UserInfo> =
            BaseReq(userInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_UPDATE.path()).addParams(baseReq)
                .build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
                showToast("failed")
            }
        }.post()
    }

    private fun updateUserInfo() {
        val userInfo: com.ln.ljb.model.UserInfo =
            com.ln.ljb.model.UserInfo()
        userInfo.id = 1
        userInfo.city = "changsha"
        userInfo.deviceToken = com.ln.ljb.constant.Config.DEVICE_TOKEN
        userInfo.province = "hunan"
        userInfo.name = "name"
        userInfo.sex = 1
        userInfo.wxNick = "wxNick"
        updateUserInfoRequest(userInfo)
    }
}
