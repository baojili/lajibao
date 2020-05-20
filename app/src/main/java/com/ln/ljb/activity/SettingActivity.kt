package com.ln.ljb.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ln.base.dialog.InputDialog
import com.ln.base.dialog.PickPhotoBottomDialog
import com.ln.base.network.*
import com.ln.base.tool.AndroidUtils
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.fragment_mine.iv_avatar
import java.io.File


class SettingActivity : WeChatLoginActivity() {

    private var pickPhotoBottomDialog: PickPhotoBottomDialog? = null

    companion object {
        private const val PICK_PHOTO: Int = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }

    override fun onInitViewData() {
        super.onInitViewData()
        var options = RequestOptions().error(R.drawable.ic_avatar_default)
            .placeholder(R.drawable.ic_avatar_default)//图片加载出来前，显示的图片
            .fallback(R.drawable.ic_avatar_default) //url为空的时候,显示的图片
        Glide.with(iv_avatar).asBitmap().load(com.ln.ljb.constant.Config.USER_INFO?.picUrl)
            .apply(options)
            .into(iv_avatar)
        pr_nick_name.setHint(com.ln.ljb.constant.Config.USER_INFO?.name)
        showWeChatCode()
        pr_invitation_code.setHint(com.ln.ljb.constant.Config.USER_INFO?.applyCode.toString())
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.ll_avatar -> {
                showPickPhotoView()
            }
            R.id.pr_nick_name -> {
                showUpdateNickNameDialog()
            }
            R.id.pr_we_chat -> {
                sendWeChatAuth()
            }
            R.id.pr_invitation_code -> {
                AndroidUtils.copyToClipboard(pr_invitation_code?.hintView?.text.toString(), this)
            }
            R.id.pr_phone_number -> {
                if (com.ln.ljb.constant.Config.USER_INFO != null && !com.ln.ljb.constant.Config.USER_INFO.mobile.isNullOrBlank()) {
                    startActivity(Intent(this, BoundMobileActivity::class.java))
                } else {
                    startActivity(Intent(this, BindMobileActivity::class.java))
                }
            }
            R.id.pr_logout -> {
                auth?.id = null
                updateUserInfo(null)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            var imageFile = pickPhotoBottomDialog?.getPhotoFileFromResult(this, data)
            Log.e(imageFile?.absolutePath)
            uploadAvatar(imageFile)
        }
    }

    override fun onGetWeChatCode(weChatCode: String) {
        super.onGetWeChatCode(weChatCode)
        bindWeChatByWeChatCode(weChatCode)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    private fun bindWeChatByWeChatCode(weChatCode: String) {
        val userIdInfo: HashMap<String, Any?> = hashMapOf()
        userIdInfo["code"] = weChatCode
        userIdInfo["id"] = auth?.id
        val baseReq: BaseReq<HashMap<String, Any?>> =
            BaseReq(userIdInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_BIND_We_CHAT.path())
                .addParams(baseReq).build()
        object :
            HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo?>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                showToast(R.string.login_success)
                auth.id = result.data?.id
                updateUserInfo(result.data)
                showWeChatCode()
            }

        }.post()
    }

    private fun showUpdateNickNameDialog() {
        var dialog: InputDialog = object : InputDialog(
            this,
            getString(R.string.nick_name),
            pr_nick_name.hintView.text.toString()
        ) {
            override fun onConfirm(input: String?) {
                if (input.isNullOrBlank()) {
                    showToast(R.string.pls_input_nick_name)
                    return
                } else {
                    updateUserInfo(input)
                }
                super.onConfirm(input)

            }
        }
        dialog.setDefaultButton()
        dialog.show()
    }

    private fun showWeChatCode() {
        if (com.ln.ljb.constant.Config.USER_INFO == null || com.ln.ljb.constant.Config.USER_INFO?.wxId == null || com.ln.ljb.constant.Config.USER_INFO?.wxId.isNullOrBlank()) {
            pr_we_chat.setHint(R.string.to_bind)
            pr_we_chat.setHintColor(resources.getColor(R.color.base))
            pr_we_chat.setIcon(R.drawable.ic_arrow_right)
            pr_we_chat.setOnClickListener(this)
            pr_we_chat.setBackgroundResource(R.drawable.bg_white)
        } else {
            pr_we_chat.setHint(com.ln.ljb.constant.Config.USER_INFO?.wxId)
            pr_we_chat.setHintColor(resources.getColor(R.color.text_light))
            pr_we_chat.setIcon(null)
            pr_we_chat.setOnClickListener(null)
            pr_we_chat.setBackgroundResource(R.color.white)
        }
    }

    private fun showPickPhotoView() {
        if (pickPhotoBottomDialog == null) {
            pickPhotoBottomDialog =
                PickPhotoBottomDialog(this)
        }
        pickPhotoBottomDialog?.show(Companion.PICK_PHOTO)
    }

    private fun uploadAvatar(avatarFile: File?) {
        val baseInfo = Base()
        val uploadAvatarInfo = hashMapOf<String, Any?>()
        uploadAvatarInfo["operation"] = baseInfo.operation
        uploadAvatarInfo["sign"] = baseInfo.sign
        uploadAvatarInfo["timestamp"] = baseInfo.timestamp
        uploadAvatarInfo["version"] = baseInfo.version
        uploadAvatarInfo["id"] = auth?.id
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_UPLOAD.path())
                .addParams(uploadAvatarInfo).addFiles(avatarFile).build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                showToast(R.string.upload_success)
                this@SettingActivity.updateUserInfo(result.data)
                Glide.with(iv_avatar).asBitmap().load(com.ln.ljb.constant.Config.USER_INFO?.picUrl)
                    .into(iv_avatar)
            }

        }.uploadOneFilePost()
    }

    private fun updateUserInfo(nickName: String) {
        val userIdInfo: com.ln.ljb.model.UserInfo =
            com.ln.ljb.model.UserInfo()
        userIdInfo.id = auth?.id
        userIdInfo.name = nickName
        val baseReq: BaseReq<com.ln.ljb.model.UserInfo> =
            BaseReq(userIdInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_UPDATE.path()).addParams(baseReq)
                .build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                showToast(R.string.upload_success)
                updateUserInfo(result.data)
                pr_nick_name.setHint(result.data?.name)
            }

        }.post()
    }
}
