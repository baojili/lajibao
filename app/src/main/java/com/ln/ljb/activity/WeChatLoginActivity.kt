package com.ln.ljb.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ln.ljb.R
import com.ln.ljb.wxapi.WXEntryActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


open class WeChatLoginActivity : com.ln.ljb.activity.BaseActivity() {

    // IWXAPI 是第三方app和微信通信的openApi接口
    private var api: IWXAPI? = null
    private var mWeiChatLoginReceiver: BroadcastReceiver? = null
    private var mWeiChatRegisterAppReceiver: BroadcastReceiver? = null

    override fun onInit() {
        super.onInit()
        initWeChatLoginReceiver()
        regToWx()
    }

    override fun onDestroy() {
        if (null != mWeiChatLoginReceiver) {
            LocalBroadcastManager.getInstance(this@WeChatLoginActivity).unregisterReceiver(
                mWeiChatLoginReceiver!!
            )
        }
        if (null != mWeiChatRegisterAppReceiver) {
            unregisterReceiver(mWeiChatRegisterAppReceiver)
        }
        super.onDestroy()
    }

    fun sendWeChatAuth() {
        // send oauth request
        var req: SendAuth.Req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        //req.state = "wechat_sdk_demo_test"
        api?.sendReq(req)
    }

    private fun regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, com.ln.ljb.constant.Config.WE_CHAT_APP_ID, true)
        // 将应用的appId注册到微信
        api?.registerApp(com.ln.ljb.constant.Config.WE_CHAT_APP_ID)
        //建议动态监听微信启动广播进行注册到微信
        mWeiChatRegisterAppReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // 将该app注册到微信
                api?.registerApp(com.ln.ljb.constant.Config.WE_CHAT_APP_ID)
            }
        }
        registerReceiver(
            mWeiChatRegisterAppReceiver,
            IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP)
        )
    }

    open fun onGetWeChatCode(weChatCode: String) {

    }

    /**微信登录授权后的结果会发送到到指定的WXEntryActivity上，需要通过广播传回来 */
    private fun initWeChatLoginReceiver() {
        if (mWeiChatLoginReceiver == null) {
            mWeiChatLoginReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    when {
                        WXEntryActivity.WE_CHAT_CANCEL_RSP == intent.action -> {
                            showToast(R.string.we_chat_login_fail_try_again)
                        }
                        WXEntryActivity.WE_CHAT_ERROR_RSP == intent.action -> {
                            showToast(R.string.we_chat_login_fail_try_again)
                        }
                        WXEntryActivity.WE_CHAT_OK_RSP == intent.action -> {
                            val bundle = intent.extras ?: return
                            val weChatCode =
                                bundle[WXEntryActivity.WE_CHAT_CODE] as String
                            onGetWeChatCode(weChatCode)
                        }
                    }
                }
            }
            var mWeChatFilter = IntentFilter()
            mWeChatFilter.addAction(WXEntryActivity.WE_CHAT_OK_RSP)
            mWeChatFilter.addAction(WXEntryActivity.WE_CHAT_CANCEL_RSP)
            mWeChatFilter.addAction(WXEntryActivity.WE_CHAT_ERROR_RSP)
            LocalBroadcastManager.getInstance(this@WeChatLoginActivity)
                .registerReceiver(mWeiChatLoginReceiver!!, mWeChatFilter)
        }
    }
}
