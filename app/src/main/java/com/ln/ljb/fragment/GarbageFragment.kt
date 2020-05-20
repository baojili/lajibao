package com.ln.ljb.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ln.base.dialog.PickPhotoBottomDialog
import com.ln.base.fragment.BaseFragment
import com.ln.base.network.Base
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.activity.*
import com.ln.ljb.view.VoiceRecognizeBottomDialog
import kotlinx.android.synthetic.main.fragment_garbage.*
import java.io.File

class GarbageFragment : BaseFragment() {

    private var pickPhotoBottomDialog: PickPhotoBottomDialog? = null
    private var mRecordBottomDialog: VoiceRecognizeBottomDialog? = null

    companion object {
        private const val PICK_PHOTO: Int = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View {
        return inflater.inflate(R.layout.fragment_garbage, container, false)
    }

    override fun onBindListener() {
        super.onBindListener()
        ll_harmful.setOnClickListener(this)
        ll_recycle.setOnClickListener(this)
        ll_household_food.setOnClickListener(this)
        ll_dry_refuse.setOnClickListener(this)
        rl_search.setOnClickListener(this)
        iv_record.setOnClickListener(this)
        iv_camera.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        super.onClick(view)
        when (view.id) {
            R.id.ll_harmful -> {
                startActivity(Intent(this.context, HarmfulWasteActivity::class.java))
            }
            R.id.ll_recycle -> {
                startActivity(Intent(this.context, RecyclableActivity::class.java))
            }
            R.id.ll_household_food -> {
                startActivity(Intent(this.context, HouseholdFoodWasteActivity::class.java))
            }
            R.id.ll_dry_refuse -> {
                startActivity(Intent(this.context, DryRefuseActivity::class.java))
            }
            R.id.rl_search -> {
                startActivity(Intent(this.context, SearchActivity::class.java))
            }
            R.id.iv_record -> {
                showVoiceRecognize()
            }
            R.id.iv_camera -> {
                showPickPhotoView()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Log.e("requestCode -> $requestCode")
        //Log.e("resultCode -> $resultCode")
        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            var imageFile = pickPhotoBottomDialog?.getPhotoFileFromResult(baseActivity, data)
            Log.e(imageFile?.absolutePath)
            imageSearch(imageFile)
        }
    }

    private fun showVoiceRecognize() {
        if (mRecordBottomDialog == null) {
            mRecordBottomDialog = object : VoiceRecognizeBottomDialog(context) {
                override fun onFinishRecord(recordFile: File?) {
                    super.onFinishRecord(recordFile)
                    voiceSearch(recordFile)
                }
            }
        }
        mRecordBottomDialog?.showStable()
    }

    private fun voiceSearch(voiceFile: File?) {
        if (voiceFile == null) return
        val baseInfo = Base()
        val uploadAvatarInfo = hashMapOf<String, Any?>()
        uploadAvatarInfo["operation"] = baseInfo.operation
        uploadAvatarInfo["sign"] = baseInfo.sign
        uploadAvatarInfo["timestamp"] = baseInfo.timestamp
        uploadAvatarInfo["version"] = baseInfo.version
        uploadAvatarInfo["cityName"] = com.ln.ljb.constant.Config.BD_ADDRESSS.city
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.GRABAGE_VOICE.path())
                .addParams(uploadAvatarInfo).addFiles(voiceFile).build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.GarbageResult?>>(
            baseActivity,
            requestEntity,
            mRecordBottomDialog
        ) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.GarbageResult?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                SearchResultActivity.launch(
                    activity as Context,
                    result.data?.datas?.firstOrNull()?.garbageName,
                    result.data?.datas?.firstOrNull()?.cateName
                )
            }

        }.uploadOneFilePost()
    }


    private fun showPickPhotoView() {
        if (pickPhotoBottomDialog == null) {
            pickPhotoBottomDialog =
                PickPhotoBottomDialog(baseActivity)
            pickPhotoBottomDialog?.fragment = this
        }
        pickPhotoBottomDialog?.show(PICK_PHOTO)
    }

    private fun imageSearch(searchFile: File?) {
        val baseInfo = Base()
        val uploadAvatarInfo = hashMapOf<String, Any?>()
        uploadAvatarInfo["operation"] = baseInfo.operation
        uploadAvatarInfo["sign"] = baseInfo.sign
        uploadAvatarInfo["timestamp"] = baseInfo.timestamp
        uploadAvatarInfo["version"] = baseInfo.version
        uploadAvatarInfo["cityName"] = com.ln.ljb.constant.Config.BD_ADDRESSS.city
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.GRABAGE_IMAGE.path())
                .addParams(uploadAvatarInfo).addFiles(searchFile).build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.GarbageResult?>>(
            baseActivity,
            requestEntity,
            toastDialog
        ) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.GarbageResult?>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                SearchResultActivity.launch(
                    activity as Context,
                    result.data?.datas?.firstOrNull()?.garbageName,
                    result.data?.datas?.firstOrNull()?.cateName
                )
            }

        }.uploadOneFilePost()
    }
}