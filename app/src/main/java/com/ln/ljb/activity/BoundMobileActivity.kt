package com.ln.ljb.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ln.ljb.R
import kotlinx.android.synthetic.main.activity_bound_mobile.*

class BoundMobileActivity : com.ln.ljb.activity.BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bound_mobile)
    }

    override fun onClick(view: View) {
        super.onClick(view)
        if (view.id == R.id.bt_modify) {
            startActivity(Intent(this, ModifyPasswordActivity::class.java))
        }
    }

    override fun onInitViewData() {
        super.onInitViewData()
        tv_mobile.text = getString(
            R.string.your_mobile_number,
            com.ln.ljb.constant.Config.USER_INFO?.mobile
        )
    }
}