package com.ln.ljb.fragment

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ln.base.activity.BaseActivity.auth
import com.ln.base.dialog.PickPhotoBottomDialog
import com.ln.base.fragment.BaseFragment
import com.ln.base.network.Base
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.AndroidUtils
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.activity.BaseActivity
import com.ln.ljb.activity.SettingActivity
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.fragment_mine.*
import java.io.File


class MineFragment : BaseFragment() {

    private var pickPhotoBottomDialog: PickPhotoBottomDialog? = null

    companion object {
        private const val PICK_PHOTO: Int = 100
        private const val SETTING_UPDATE: Int = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onBindListener() {
        super.onBindListener()
        pr_about_us.setOnClickListener(this)
        pr_check_update.setOnClickListener(this)
        iv_avatar.setOnClickListener(this)
        iv_message.setOnClickListener(this)
        iv_setting.setOnClickListener(this)
        iv_copy.setOnClickListener(this)
    }

    override fun onInitViewData() {
        super.onInitViewData()
        showAvatar()
        tv_nick_name.text = com.ln.ljb.constant.Config.USER_INFO?.name
        tv_invent_code.text =
            "${getString(R.string.invitation_code)}:${com.ln.ljb.constant.Config.USER_INFO?.applyCode}"
        pr_check_update.setHint(
            "v${AndroidUtils.getVersionName(context)}.${AndroidUtils.getVersionCode(
                context,
                0
            )}"
        )
        tv_current_day.text = com.ln.ljb.constant.Config.USER_INFO?.currentDayIntegral?.toString()
        tv_integral.text = com.ln.ljb.constant.Config.USER_INFO?.integral.toString()
        tv_garbage_gold.text = com.ln.ljb.constant.Config.USER_INFO?.gold.toString()

    }

    private fun showAvatar() {
        var options = RequestOptions().error(R.drawable.ic_avatar_default)
            .placeholder(R.drawable.ic_avatar_default)//图片加载出来前，显示的图片
            .fallback(R.drawable.ic_avatar_default) //url为空的时候,显示的图片
        Glide.with(iv_avatar).asBitmap().load(com.ln.ljb.constant.Config.USER_INFO?.picUrl)
            .apply(options)
            .into(iv_avatar)
    }

    override fun onResume() {
        super.onResume()
        showAvatar()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        when (view.id) {
            R.id.iv_avatar -> {
                showPickPhotoView()
            }
            R.id.pr_about_us -> {
                startActivity(Intent(this.context, com.ln.ljb.activity.AboutUsActivity::class.java))
            }
            R.id.pr_check_update -> {
                Beta.checkUpgrade()
            }
            R.id.iv_copy -> {
                this.context?.let {
                    AndroidUtils.copyToClipboard(
                        com.ln.ljb.constant.Config.USER_INFO?.applyCode.toString(),
                        it
                    )
                }
            }
            R.id.iv_setting -> {
                startActivityForResult(
                    Intent(this.context, SettingActivity::class.java),
                    SETTING_UPDATE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            var imageFile = pickPhotoBottomDialog?.getPhotoFileFromResult(baseActivity, data)
            Log.e(imageFile?.absolutePath)
            uploadAvatar(imageFile)
        } else if (requestCode == SETTING_UPDATE && resultCode == Activity.RESULT_OK) {
            tv_nick_name.text = com.ln.ljb.constant.Config.USER_INFO?.name
            Glide.with(iv_avatar).asBitmap().load(com.ln.ljb.constant.Config.USER_INFO?.picUrl)
                .into(iv_avatar)
        }
    }

    private fun showPickPhotoView() {
        if (pickPhotoBottomDialog == null) {
            pickPhotoBottomDialog =
                PickPhotoBottomDialog(baseActivity)
            pickPhotoBottomDialog?.fragment = this
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
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(
            baseActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                showToast(R.string.upload_success)
                (baseActivity as BaseActivity).updateUserInfo(result.data)
                Glide.with(iv_avatar).asBitmap().load(com.ln.ljb.constant.Config.USER_INFO?.picUrl)
                    .into(iv_avatar)
            }

        }.uploadOneFilePost()
    }
}