package com.ln.ljb.fragment

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ln.base.fragment.BaseFragment
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.activity.MobileLoginActivity
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*

class RegisterFragment : BaseFragment() {

    private var timer: Timer? = null
    private var smsCodeTime: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onBindListener() {
        super.onBindListener()
        addTextChangedListeners()
        bt_register.setOnClickListener(this)
        tv_get_sms_code.setOnClickListener(this)
        iv_show_hide.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        super.onClick(view)
        if (view.id == R.id.bt_register) {
            onRegisterBtnClicked()
        } else if (view.id == R.id.tv_get_sms_code) {
            onSmsCodeClicked()
        } else if (view.id == R.id.iv_show_hide) {
            if (et_password?.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                et_password!!.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_show_hide!!.setImageResource(R.drawable.ic_show)
            } else {
                et_password?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_show_hide!!.setImageResource(R.drawable.ic_hide)
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
        et_invitation_code.addTextChangedListener(textWatcher)
    }

    private fun updateBtnEnableStatus() {
        val mobile = et_mobile?.text?.toString()
        val password = et_password?.text?.toString()
        val smsCode = et_sms_code?.text?.toString()
        val invitationCode = et_invitation_code?.text?.toString()
        bt_register.isEnabled =
            !(mobile.isNullOrBlank() || password.isNullOrBlank() || smsCode.isNullOrBlank())
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
        getSmsMsgRequest(mobile, 0, 1)
    }

    private fun onRegisterBtnClicked() {
        val mobile = et_mobile?.text?.toString()
        val password = et_password?.text?.toString()
        val smsCode = et_sms_code?.text?.toString()
        val invitationCode = et_invitation_code?.text?.toString()
        registerRequest(mobile, password, smsCode, invitationCode)
    }

    private fun registerRequest(
        mobile: String?,
        loginPwd: String?,
        verifyCode: String?,
        applyCode: String?
    ) {
        if (mobile.isNullOrBlank() || loginPwd.isNullOrBlank() || verifyCode.isNullOrBlank()) return
        val registerInfo = com.ln.ljb.model.RegisterInfo()
        registerInfo.mobile = mobile
        registerInfo.loginPwd = loginPwd
        registerInfo.verfiyCode = verifyCode
        registerInfo.applyCode = applyCode
        val baseReq: BaseReq<com.ln.ljb.model.RegisterInfo?> =
            BaseReq(registerInfo)
        val requestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_REGISTER.path())
                .addParams(baseReq).build()
        object : HttpRequest<BaseRsp<String?>?>(
            baseActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<String?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                showToast(R.string.register_success)
                (activity as MobileLoginActivity?)!!.showFragment(LoginFragment::class.java.simpleName)
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
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
            baseActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<String?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                showToast(R.string.get_sms_code_success)
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
            }
        }.post()
    }
}