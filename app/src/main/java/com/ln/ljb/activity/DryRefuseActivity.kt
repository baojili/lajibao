package com.ln.ljb.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.ln.ljb.R
import kotlinx.android.synthetic.main.activity_dry_refuse.*


class DryRefuseActivity : com.ln.ljb.activity.BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dry_refuse)
    }

    override fun onBindListener() {
        super.onBindListener()
        ll_content.children.forEachIndexed { index, view ->
            run {
                if (index != 0) {
                    (view as LinearLayout).children.forEach {
                        it.setOnClickListener(this@DryRefuseActivity)
                    }
                }
            }

        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        if (view is LinearLayout) {
            SearchResultActivity.launch(
                this,
                (view.getChildAt(1) as TextView).text.toString(),
                getString(R.string.dry_refuse_waste)
            )
        }
    }

}
