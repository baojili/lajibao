package com.ln.ljb.activity

import android.os.Bundle
import android.view.View
import com.ln.base.activity.WebViewActivity
import com.ln.base.tool.AndroidUtils
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.UpgradeInfo
import kotlinx.android.synthetic.main.activity_about_us.*


class AboutUsActivity : com.ln.ljb.activity.BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }

    override fun onInitViewData() {
        super.onInitViewData()
        showUpgradeInfo()
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.pr_official_contact -> {
                AndroidUtils.openPhoneDial(this, getString(R.string.contact_number_extra))
            }
            R.id.tv_service -> {
                WebViewActivity.launch(
                    this@AboutUsActivity,
                    getString(R.string.service_protocol_extra),
                    com.ln.ljb.constant.Config.SERVICE_URL
                )
            }
            R.id.tv_privacy -> {
                WebViewActivity.launch(
                    this@AboutUsActivity,
                    getString(R.string.privacy_policy_extra),
                    com.ln.ljb.constant.Config.PRIVACY_URL
                )
            }
        }
    }

    private fun showUpgradeInfo() {
        try {
            var upgradeInfo: UpgradeInfo? = Beta.getUpgradeInfo()
            Log.e(JsonUtils.toJsonViewStr(upgradeInfo))
            if (upgradeInfo == null) {
                pr_beta_version?.setHint(
                    "v${AndroidUtils.getVersionName(this)}.${AndroidUtils.getVersionCode(
                        this,
                        0
                    )}"
                )
                ll_upgrade_info.visibility = View.GONE
            } else {
                pr_beta_version?.setHint("v${upgradeInfo.versionName}.${upgradeInfo.versionCode}")
                if (upgradeInfo.newFeature.isNullOrBlank()) {
                    ll_upgrade_info.visibility = View.GONE
                } else {
                    ll_upgrade_info.visibility = View.VISIBLE
                    tv_upgrade_info_content.text = upgradeInfo.newFeature
                }
            }
        } catch (e: Exception) {
            pr_beta_version?.setHint(
                "v${AndroidUtils.getVersionName(this)}.${AndroidUtils.getVersionCode(
                    this,
                    0
                )}"
            )
            ll_upgrade_info.visibility = View.GONE
            e.printStackTrace()
        }
    }
}
