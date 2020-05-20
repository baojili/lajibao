package com.ln.ljb.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.model.UserInfo
import kotlinx.android.synthetic.main.activity_bind_mobile.*
import java.util.*

class BindMobileActivity : com.ln.ljb.activity.BaseActivity() {

    private var timer: Timer? = null
    private var smsCodeTime: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_mobile)
    }

    override fun onBindListener() {
        super.onBindListener()
        addTextChangedListeners()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        if (view.id == R.id.bt_bind) {
            onBtnClicked()
        } else if (view.id == R.id.tv_get_sms_code) {
            onSmsCodeClicked()
        }
    }

    private fun addTextChangedListeners() {
        var textWatcher = object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                updateBtnEnableStatus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        et_mobile.addTextChangedListener(textWatcher)
        et_sms_code.addTextChangedListener(textWatcher)
        et_password.addTextChangedListener(textWatcher)
        et_password_again.addTextChangedListener(textWatcher)
    }

    private fun updateBtnEnableStatus() {
        val mobile = et_mobile?.text?.toString()
        val password = et_password?.text?.toString()
        val smsCode = et_sms_code?.text?.toString()
        val passwordAgain = et_password_again?.text?.toString()
        bt_bind.isEnabled =
            !(mobile.isNullOrBlank() || password.isNullOrBlank() || smsCode.isNullOrBlank() || passwordAgain.isNullOrBlank())
    }

    private fun onSmsCodeClicked() {
        val mobile = et_mobile?.text?.toString()
        if (mobile.isNullOrBlank() || getString(R.string.get_sms_code) != tv_get_sms_code.text.toString()) {
            return
        }
        if (timer == null) {
            timer = Timer()
        }
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                tv_get_sms_code.post(Runnable {
                    if (60 - smsCodeTime == 0) {
                        tv_get_sms_code.text = getString(R.string.get_sms_code)
                        smsCodeTime = 0
                        timer?.cancel()
                        timer = null
                    } else {
                        tv_get_sms_code.text = getString(R.string.left_second, 60 - smsCodeTime)
                        smsCodeTime += 1
                    }
                })
            }

        }, 0, 1000)
        getSmsMsgRequest(mobile, 0, 3)
    }

    private fun onBtnClicked() {
        val mobile = et_mobile?.text?.toString()
        val password = et_password?.text?.toString()
        val smsCode = et_sms_code?.text?.toString()
        val passwordAgain = et_password_again?.text?.toString()
        bindRequest(mobile, password, smsCode, passwordAgain)
    }

    private fun bindRequest(
        mobile: String?,
        loginPwd: String?,
        verifyCode: String?,
        reLoginPwd: String?
    ) {
        if (mobile.isNullOrBlank() || loginPwd.isNullOrBlank() || verifyCode.isNullOrBlank() || reLoginPwd.isNullOrBlank()) return
        val bindMobileInfo = com.ln.ljb.model.ResetPasswordInfo()
        bindMobileInfo.id = auth?.id
        bindMobileInfo.mobile = mobile
        bindMobileInfo.loginPwd = loginPwd
        bindMobileInfo.verfiyCode = verifyCode
        bindMobileInfo.reLoginPwd = reLoginPwd
        val baseReq: BaseReq<com.ln.ljb.model.ResetPasswordInfo?> =
            BaseReq(bindMobileInfo)
        val requestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_BIND_MOBILE.path())
                .addParams(baseReq).build()
        object : HttpRequest<BaseRsp<UserInfo?>?>(
            this,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<UserInfo?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                updateUserInfo(result.data)
                showToast(R.string.bind_success)
                finish()
            }

        }.post()
    }

    private fun getSmsMsgRequest(mobile: String?, smsType: Int, type: Int) {
        if (mobile.isNullOrBlank()) return
        val smsMsg = com.ln.ljb.model.SmsMsg()
        smsMsg.mobile = mobile
        smsMsg.smsType = smsType
        smsMsg.type = type
        val baseReq: BaseReq<com.ln.ljb.model.SmsMsg?> =
            BaseReq(smsMsg)
        val requestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.SMS_SEND.path()).addParams(baseReq)
                .build()
        object : HttpRequest<BaseRsp<String?>?>(
            this,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<String?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                showToast(R.string.get_sms_code_success)
            }

        }.post()
    }
}