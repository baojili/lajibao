package com.ln.ljb.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ln.base.fragment.FragmentPagerAdapter
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.fragment.GarbageFragment
import com.ln.ljb.fragment.HomeFragment
import com.ln.ljb.fragment.MineFragment


class MainActivity : TabActivity() {

    private var pagerAdapter: FragmentPagerAdapter? = null
    private var garbageFragment: GarbageFragment? = null
    private var homeFragment: HomeFragment? = null
    private var mineFragment: MineFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onInit() {
        super.onInit()
        if (com.ln.ljb.constant.Config.USER_INFO == null) {
            com.ln.ljb.constant.Config.USER_INFO = JsonUtils.toEntity(
                settings.getString(
                    com.ln.ljb.constant.SharedPreferKey.KEY_AUTH,
                    null
                ), com.ln.ljb.model.UserInfo::class.java
            )
        }

        viewPager?.offscreenPageLimit = 3

        garbageFragment = GarbageFragment()
        homeFragment = HomeFragment()
        mineFragment = MineFragment()
        /*pagerAdapter = FragmentPagerAdapter(
            supportFragmentManager,
            homeFragment
        )

        pagerAdapter!!.addFragments(garbageClassificationFragment, mineFragment)*/
        pagerAdapter = FragmentPagerAdapter(
            supportFragmentManager,
            garbageFragment
        )

        pagerAdapter!!.addFragments(mineFragment)

        setTabAdapter(pagerAdapter!!)
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        Log.e("id ->" + view?.id)
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume")
        if (auth == null || auth?.id == null) {
            Log.e("onResume2")
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            updateUserDeviceToken()
        }
    }

    private fun getGrabageResult(cityName: String, key: String) {
        val garbageInfo: HashMap<String, Any> = hashMapOf()
        garbageInfo["cityName"] = cityName
        garbageInfo["text"] = key
        val baseReq: BaseReq<HashMap<String, Any>> =
            BaseReq(garbageInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.GRABAGE_TEXT.path())
                .addParams(baseReq).build()
        object :
            HttpRequest<BaseRsp<com.ln.ljb.model.GarbageResult>>(this, requestEntity, toastDialog) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.GarbageResult>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
            }

            override fun onFail(result: BaseRsp<*>) {
                super.onFail(result)
                showToast("failed")
            }
        }.post()
    }

    private fun updateUserDeviceToken() {
        if (com.ln.ljb.constant.Config.USER_INFO == null) return
        val userIdInfo = com.ln.ljb.model.UserInfo()
        //检查deviceToken是否需要更新
        if (!com.ln.ljb.constant.Config.DEVICE_TOKEN.isNullOrBlank() && com.ln.ljb.constant.Config.DEVICE_TOKEN != com.ln.ljb.constant.Config.USER_INFO?.deviceToken) {
            userIdInfo.deviceToken = com.ln.ljb.constant.Config.DEVICE_TOKEN
        }
        //检查地址信息是否需要更新
        if (com.ln.ljb.constant.Config.BD_ADDRESSS != null && !com.ln.ljb.constant.Config.BD_ADDRESSS.city.isNullOrBlank() && com.ln.ljb.constant.Config.BD_ADDRESSS.city != com.ln.ljb.constant.Config.USER_INFO.city) {
            userIdInfo.city = com.ln.ljb.constant.Config.BD_ADDRESSS.city
            userIdInfo.province = com.ln.ljb.constant.Config.BD_ADDRESSS.province
        }
        if (userIdInfo.deviceToken == null && userIdInfo.city == null && userIdInfo.province == null) return
        userIdInfo.id = auth?.id
        val baseReq: BaseReq<com.ln.ljb.model.UserInfo> =
            BaseReq(userIdInfo)
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.USER_UPDATE.path()).addParams(baseReq)
                .build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.UserInfo>>(this, requestEntity) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.UserInfo>) {
                super.onSuccess(result)
                updateUserInfo(result.data)
            }

            override fun onFail(result: BaseRsp<*>) {
                //super.onFail(result)
            }
        }.post()
    }
}
