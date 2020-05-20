package com.ln.ljb.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.reflect.TypeToken
import com.ln.base.activity.BaseActivity
import com.ln.base.model.NoProguard
import com.ln.base.network.BaseReq
import com.ln.base.network.BaseRsp
import com.ln.base.network.HttpRequest
import com.ln.base.network.RequestEntity
import com.ln.base.tool.AndroidUtils
import com.ln.base.tool.JsonUtils
import com.ln.base.tool.Log
import com.ln.base.view.HolderListAdapter
import com.ln.ljb.R
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

class SearchActivity : BaseActivity() {


    private val popSearchListData: List<String> = ArrayList()
    private var mPopSearchListAdapter: SearchListAdapter? = null

    private val searchHistoryListData: List<String> = ArrayList()
    private var mSearchHistoryListAdapter: SearchListAdapter? = null

    private val listData: List<com.ln.ljb.model.JDGarbageVO> = ArrayList()
    private var mListAdapter: SearchResultListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun onBindListener() {
        super.onBindListener()
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable?.length == 0) {
                    ll_recommend.visibility = View.VISIBLE
                } else {
                    ll_recommend.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                list_view.visibility = View.GONE
            }
        })
        et_search.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    AndroidUtils.hideSoftKeyboard(this@SearchActivity, et_search)
                    getGarbageResult(
                        com.ln.ljb.constant.Config.BD_ADDRESSS.city,
                        et_search!!.text!!.toString()
                    )
                }
                return false
            }
        })
    }

    override fun onInitViewData() {
        super.onInitViewData()
        mPopSearchListAdapter = SearchListAdapter(this, popSearchListData)
        fl_pop_search?.adapter = mPopSearchListAdapter
        fl_pop_search?.setOnItemClickListener(mPopSearchListAdapter)

        mSearchHistoryListAdapter = SearchListAdapter(this, searchHistoryListData)
        fl_search_history?.adapter = mSearchHistoryListAdapter
        fl_search_history?.setOnItemClickListener(mSearchHistoryListAdapter)

        mListAdapter = SearchResultListAdapter(this, listData)
        list_view?.adapter = mListAdapter
        list_view?.onItemClickListener = mListAdapter

        getPopSearch()
        showSearchHistory()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        when (view.id) {
            R.id.tv_search -> {
                onBackPressed()
            }
            R.id.iv_crash -> {
                clearSearchHistory()
                showSearchHistory()
            }
        }
    }

    private fun showSearchHistory() {
        var searchHistoryStr =
            settings.getString(com.ln.ljb.constant.SharedPreferKey.KEY_SEARCH_HISTORY, "[]")
        var searchHistory: List<String?>? =
            JsonUtils.toEntity(searchHistoryStr, object : TypeToken<List<String?>?>() {}.type)
        mSearchHistoryListAdapter?.setDataList(searchHistory)
        fl_search_history.setOnItemClickListener(mSearchHistoryListAdapter)
    }

    private fun getGarbageResult(cityName: String, key: String) {
        if (cityName.isNullOrBlank() || key.isNullOrBlank()) return
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
                list_view.visibility = View.VISIBLE
                mListAdapter?.setDataList(result.data.datas)
                if (result.data.datas.size == 0) {
                    showToast(R.string.cannot_find_result)
                }
                saveSearchHistory(result.data?.text)
                showSearchHistory()

            }

        }.post()
    }

    private fun saveSearchHistory(searchWord: String?) {
        if (searchWord.isNullOrBlank()) return
        var searchHistoryStr =
            settings.getString(com.ln.ljb.constant.SharedPreferKey.KEY_SEARCH_HISTORY, "[]")
        var searchHistory: MutableList<String?>? = JsonUtils.toEntity(
            searchHistoryStr,
            object : TypeToken<MutableList<String?>?>() {}.type
        )
        searchHistory?.add(0, searchWord)

        settings.edit()
            .putString(
                com.ln.ljb.constant.SharedPreferKey.KEY_SEARCH_HISTORY,
                JsonUtils.toJson(searchHistory?.toSet())
            ).commit()
    }

    private fun clearSearchHistory() {
        settings.edit().putString(com.ln.ljb.constant.SharedPreferKey.KEY_SEARCH_HISTORY, "[]")
            .commit()
    }

    private fun getPopSearch() {
        val userIdInfo: HashMap<String, Any?> = hashMapOf()
        userIdInfo["id"] = auth?.id
        val baseReq: BaseReq<HashMap<String, Any?>> =
            BaseReq(userIdInfo)
        //Log.e(JsonUtils.toJson(userIdInfo))
        val requestEntity: RequestEntity =
            RequestEntity.Builder(com.ln.ljb.constant.ApiPath.GRABAGE_POP_SEARCH.path())
                .addParams(baseReq).build()
        object : HttpRequest<BaseRsp<com.ln.ljb.model.PopSearchResult>>(this, requestEntity) {
            override fun onSuccess(result: BaseRsp<com.ln.ljb.model.PopSearchResult>) {
                super.onSuccess(result)
                Log.d(JsonUtils.toJsonViewStr(result))
                mPopSearchListAdapter?.setDataList(result.data?.words)
                fl_pop_search?.setOnItemClickListener(mPopSearchListAdapter)
            }

        }.post()
    }

    private class SearchResultListAdapter :
        HolderListAdapter<SearchResultListAdapter.ViewHolder, com.ln.ljb.model.JDGarbageVO> {
        private var context: Activity

        constructor(context: Activity, list: List<com.ln.ljb.model.JDGarbageVO>) : super(
            list,
            ViewHolder::class.java
        ) {
            this.context = context
        }

        override fun onCreateItemView(parent: ViewGroup): View {
            return context.layoutInflater.inflate(
                R.layout.item_search,
                parent, false
            )
        }

        override fun onFillItemView(
            position: Int,
            view: View?,
            holder: ViewHolder?,
            data: com.ln.ljb.model.JDGarbageVO?
        ) {
            holder?.tvSearchStr?.text = data?.garbageName
            holder?.tvType?.text = context.getString(R.string.book_symbol, data?.cateName)

            when (data?.cateName) {
                context.getString(R.string.harmful_waste) -> {
                    holder?.tvType?.setTextColor(context.resources.getColor(R.color.type_red))
                }
                context.getString(R.string.recycle_object) -> {
                    holder?.tvType?.setTextColor(context.resources.getColor(R.color.type_blue))

                }
                context.getString(R.string.household_food_waste) -> {
                    holder?.tvType?.setTextColor(context.resources.getColor(R.color.type_yellow))
                }
                context.getString(R.string.dry_refuse_waste) -> {
                    holder?.tvType?.setTextColor(context.resources.getColor(R.color.type_gray))
                }
                else -> {
                    holder?.tvType?.setTextColor(context.resources.getColor(R.color.text))
                }
            }
        }

        override fun onItemViewClick(
            view: View?,
            viewId: Int,
            data: com.ln.ljb.model.JDGarbageVO?
        ) {
            super.onItemViewClick(view, viewId, data)
            SearchResultActivity.launch(context, data!!.garbageName, data.cateName)
        }

        private class ViewHolder : NoProguard {
            var tvSearchStr: TextView? = null
            var tvType: TextView? = null
        }
    }

    private class SearchListAdapter :
        HolderListAdapter<SearchListAdapter.ViewHolder, String> {
        private var context: SearchActivity

        constructor(context: SearchActivity, list: List<String>) : super(
            list,
            ViewHolder::class.java
        ) {
            this.context = context
        }

        override fun onCreateItemView(parent: ViewGroup): View {
            return context.layoutInflater.inflate(
                R.layout.item_pop_search,
                parent, false
            )
        }

        override fun onFillItemView(
            position: Int,
            view: View?,
            holder: ViewHolder?,
            data: String?
        ) {
            holder?.tvName?.text = data
        }

        override fun onItemViewClick(view: View?, viewId: Int, data: String?) {
            super.onItemViewClick(view, viewId, data)
            context.et_search?.setText(data)
            context.et_search?.text?.toString()?.let {
                context.getGarbageResult(
                    com.ln.ljb.constant.Config.BD_ADDRESSS.city,
                    it
                )
            }
        }

        private class ViewHolder : NoProguard {
            var tvName: TextView? = null
        }
    }
}