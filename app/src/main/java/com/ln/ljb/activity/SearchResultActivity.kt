package com.ln.ljb.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ln.ljb.R
import kotlinx.android.synthetic.main.activity_search_result.*


class SearchResultActivity : com.ln.ljb.activity.BaseActivity() {

    private var type: String? = null
    private var garbageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
    }

    companion object {
        fun launch(context: Context?, garbageName: String?, type: String?) {
            var intent = Intent(context, SearchResultActivity::class.java)
            intent.putExtra("garbageName", garbageName)
            intent.putExtra("type", type)
            context?.startActivity(intent)
        }
    }

    override fun onInit() {
        super.onInit()
        type = this.intent!!.getStringExtra("type")
        garbageName = this.intent!!.getStringExtra("garbageName")
        if (type == null || garbageName == null) {
            finish()
        }
    }

    override fun onInitViewData() {
        super.onInitViewData()
        when (type) {
            getString(R.string.harmful_waste) -> {
                titleBar.setTitle(getString(R.string.harmful_waste))
                tv_garbage.text = "${garbageName.toString()} ${getString(R.string.beyond)}"
                iv_type_icon.setImageResource(R.drawable.ic_harmful_red)
                tv_type.setText(R.string.harmful_waste)
                tv_type_extra.setText(R.string.harmful_waste_extra)
                tv_type.setTextColor(resources.getColor(R.color.type_red))
                tv_type_extra.setTextColor(resources.getColor(R.color.type_red))
                tv_type_tip_1.setText(R.string.harmful_tips_1)
                tv_type_tip_1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_red,
                    0,
                    0,
                    0
                )
                tv_type_tip_2.setText(R.string.harmful_tips_2)
                tv_type_tip_2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_red,
                    0,
                    0,
                    0
                )
                tv_type_tip_3.setText(R.string.harmful_tips_3)
                tv_type_tip_3.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_red,
                    0,
                    0,
                    0
                )
            }
            getString(R.string.recycle_object) -> {
                titleBar.setTitle(getString(R.string.recycle_object))
                tv_garbage.text = "${garbageName.toString()} ${getString(R.string.beyond)}"
                iv_type_icon.setImageResource(R.drawable.ic_recyclable_blue)
                tv_type.setText(R.string.recycle_object)
                tv_type_extra.setText(R.string.recycle_object_extra)
                tv_type.setTextColor(resources.getColor(R.color.type_blue))
                tv_type_extra.setTextColor(resources.getColor(R.color.type_blue))
                tv_type_tip_1.setText(R.string.recycle_tips_1)
                tv_type_tip_1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_blue,
                    0,
                    0,
                    0
                )
                tv_type_tip_2.setText(R.string.recycle_tips_2)
                tv_type_tip_2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_blue,
                    0,
                    0,
                    0
                )
                tv_type_tip_3.setText(R.string.recycle_tips_3)
                tv_type_tip_3.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_blue,
                    0,
                    0,
                    0
                )

            }
            getString(R.string.household_food_waste), getString(R.string.household_food_waste_extra2) -> {
                titleBar.setTitle(getString(R.string.household_food_waste))
                tv_garbage.text = "${garbageName.toString()} ${getString(R.string.beyond)}"
                iv_type_icon.setImageResource(R.drawable.ic_household_food_yellow)
                tv_type.setText(R.string.household_food_waste)
                tv_type_extra.setText(R.string.household_food_waste_extra)
                tv_type.setTextColor(resources.getColor(R.color.type_yellow))
                tv_type_extra.setTextColor(resources.getColor(R.color.type_yellow))
                tv_type_tip_1.setText(R.string.household_tips_1)
                tv_type_tip_1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_yellow,
                    0,
                    0,
                    0
                )
                tv_type_tip_2.setText(R.string.household_tips_2)
                tv_type_tip_2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_yellow,
                    0,
                    0,
                    0
                )
                tv_type_tip_3.setText(R.string.household_tips_3)
                tv_type_tip_3.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_yellow,
                    0,
                    0,
                    0
                )
            }
            getString(R.string.dry_refuse_waste) -> {
                titleBar.setTitle(getString(R.string.dry_refuse_waste))
                tv_garbage.text = "${garbageName.toString()} ${getString(R.string.beyond)}"
                iv_type_icon.setImageResource(R.drawable.ic_dry_refuse_gray)
                tv_type.setText(R.string.dry_refuse_waste)
                tv_type_extra.setText(R.string.dry_refuse_waste_extra)
                tv_type.setTextColor(resources.getColor(R.color.type_gray))
                tv_type_extra.setTextColor(resources.getColor(R.color.type_gray))
                tv_type_tip_1.setText(R.string.dry_tips_1)
                tv_type_tip_1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_gray,
                    0,
                    0,
                    0
                )
                tv_type_tip_2.setText(R.string.dry_tips_2)
                tv_type_tip_2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_gray,
                    0,
                    0,
                    0
                )
                tv_type_tip_3.setText(R.string.dry_tips_3)
                tv_type_tip_3.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_gray,
                    0,
                    0,
                    0
                )
            }
            else -> {
                titleBar.setTitle(getString(R.string.dry_refuse_waste))
                tv_garbage.text = "${garbageName.toString()} ${getString(R.string.beyond)}"
                iv_type_icon.setImageResource(R.drawable.ic_dry_refuse_gray)
                tv_type.setText(R.string.dry_refuse_waste)
                tv_type_extra.setText(R.string.dry_refuse_waste_extra)
                tv_type.setTextColor(resources.getColor(R.color.type_gray))
                tv_type_extra.setTextColor(resources.getColor(R.color.type_gray))
                tv_type_tip_1.setText(R.string.dry_tips_1)
                tv_type_tip_1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_gray,
                    0,
                    0,
                    0
                )
                tv_type_tip_2.setText(R.string.dry_tips_2)
                tv_type_tip_2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_gray,
                    0,
                    0,
                    0
                )
                tv_type_tip_3.setText(R.string.dry_tips_3)
                tv_type_tip_3.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_point_gray,
                    0,
                    0,
                    0
                )
            }
        }
    }

}
