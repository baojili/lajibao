package com.ln.ljb.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.text.TextWatcher
import android.view.View
import com.ln.base.activity.TabActivity
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import kotlinx.android.synthetic.main.activity_forget_password.*
import java.util.*

class ForgetPasswordActivity : TabActivity() {

    private var timer: Timer? = null
    private var smsCodeTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
    }


    override fun onBindListener() {
        super.onBindListener()
        addTextChangedListeners()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        if (view.id == R.id.bt_submit) {
            onFindBtnClicked()
        } else if (view.id == R.id.tv_get_sms_code) {
            onSmsCodeClicked()
        } else if (view.id == R.id.iv_show_hide) {
            if (et_password.inputType == TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                et_password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                et_password_again.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_show_hide.setImageResource(R.drawable.ic_show)
            } else {
                et_password.inputType = TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                et_password_again.inputType = TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_show_hide.setImageResource(R.drawable.ic_hide)
            }
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
        bt_submit.isEnabled =
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
        getSmsMsgRequest(mobile, 0, 2)
    }

    private fun onFindBtnClicked() {
        val mobile = et_mobile?.text?.toString()
        val smsCode: String? = et_sms_code?.text?.toString()
        val password: String? = et_password?.text?.toString()
        val passwordAgain: String? = et_password_again?.text?.toString()
        resetPwdRequest(mobile, smsCode, password, passwordAgain)
    }


    private fun resetPwdRequest(
        mobile: String?,
        verifyCode: String?,
        loginPwd: String?,
        reLoginPwd: String?
    ) {
        if (mobile.isNullOrBlank() || verifyCode.isNullOrBlank() || loginPwd.isNullOrBlank() || reLoginPwd.isNullOrBlank()) return
        val resetPasswordInfo = com.ln.ljb.model.ResetPasswordInfo()
        resetPasswordInfo.mobile = mobile
        resetPasswordInfo.loginPwd = loginPwd
        resetPasswordInfo.verfiyCode = verifyCode
        resetPasswordInfo.reLoginPwd = reLoginPwd
        val baseReq: BaseReq<com.ln.ljb.model.ResetPasswordInfo> =
            BaseReq<com.ln.ljb.model.ResetPasswordInfo>(resetPasswordInfo)
        val requestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_RESET_LOGIN_PWD.path())
                .addParams(baseReq).build()
        object : HttpRequest<BaseRsp<String?>?>(
            this@ForgetPasswordActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<String?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                showToast(R.string.submit_success)
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
        val baseReq: BaseReq<com.ln.ljb.model.SmsMsg> =
            BaseReq<com.ln.ljb.model.SmsMsg>(smsMsg)
        val requestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.SMS_SEND.path()).addParams(baseReq)
                .build()
        object : HttpRequest<BaseRsp<String>>(
            this@ForgetPasswordActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<String>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                showToast(R.string.get_sms_code_success)
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
                Log.e("onFail")
            }
        }.post()
    }
}
