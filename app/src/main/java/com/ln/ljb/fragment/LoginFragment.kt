package com.ln.ljb.fragment

import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ln.base.activity.BaseActivity.auth
import com.ln.base.fragment.BaseFragment
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.activity.BaseActivity
import com.ln.ljb.activity.ForgetPasswordActivity
import com.ln.ljb.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onBindListener() {
        super.onBindListener()
        bt_login.setOnClickListener(this)
        ll_forget_password.setOnClickListener(this)
        iv_show_hide.setOnClickListener(this)
        addTextChangedListeners()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        if (view.id == R.id.bt_login) {
            onLoginBtnClicked()
        } else if (view.id == R.id.ll_forget_password) {
            startActivity(Intent(context, ForgetPasswordActivity::class.java))
            //getActivity().finish();
        } else if (view.id == R.id.iv_show_hide) {
            if (et_password!!.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                et_password!!.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_show_hide!!.setImageResource(R.drawable.ic_show)
            } else {
                et_password!!.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
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
        et_password.addTextChangedListener(textWatcher)
    }

    private fun updateBtnEnableStatus() {
        val mobile = et_mobile?.text?.toString()
        val password = et_password?.text?.toString()
        bt_login.isEnabled =
            !(mobile.isNullOrBlank() || password.isNullOrBlank())
    }

    private fun onLoginBtnClicked() {
        val mobile = et_mobile?.text?.toString()
        val password = et_password?.text?.toString()
        loginRequest(mobile, password)
    }

    private fun loginRequest(mobile: String?, loginPwd: String?) {
        if ((mobile.isNullOrBlank() || loginPwd.isNullOrBlank())) return
        val loginInfo = com.ln.ljb.model.LoginInfo()
        loginInfo.mobile = mobile
        loginInfo.loginPwd = loginPwd
        val baseReq: BaseReq<Any?> =
            BaseReq(loginInfo)
        val requestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_LOGIN_MOBILE.path())
                .addParams(baseReq).build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo?>?>(
            this.baseActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.gson().toJson(result))
                showToast(R.string.login_success)
                auth.id = result.data?.id
                (activity as BaseActivity?)!!.updateUserInfo(result.data)
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
            }
        }.post()
    }
}