package com.ln.ljb.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.base.activity.BaseActivity;
import com.ln.base.model.NoProguard;
import com.ln.base.network.BaseRsp;
import com.ln.base.network.Config;
import com.ln.base.network.HttpRequest;
import com.ln.base.network.PageReq;
import com.ln.base.network.PageResult;
import com.ln.base.network.PageRsp;
import com.ln.base.network.RequestEntity;
import com.ln.base.tool.JsonUtils;
import com.ln.base.tool.Log;
import com.ln.base.view.HolderListAdapter;
import com.ln.base.view.ListView;
import com.ln.ljb.R;
import com.ln.ljb.constant.ApiPath;
import com.ln.ljb.model.BasePage;
import com.ln.ljb.model.TMsg;

import java.util.ArrayList;
import java.util.List;

public class TMsgActivity extends BaseActivity implements ListView.OnPullRefreshListener, ListView.OnLoadMoreListener {

    private ListView mListView;
    private List<TMsg> listData = new ArrayList<>();
    private MyListAdapter mListAdapter;
    private RequestEntity requestEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmsg);
    }

    @Override
    protected void onInit() {
        super.onInit();
    }

    @Override
    protected void onFindViews() {
        super.onFindViews();
        mListView = findViewById(R.id.list_view);
    }

    @Override
    protected void onBindListener() {
        super.onBindListener();
    }

    @Override
    protected void onInitViewData() {
        super.onInitViewData();
        mListAdapter = new MyListAdapter(listData);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mListAdapter);
        mListView.setPullRefreshEnabled(this);
        mListView.setLoadMoreEnabled(this);
        getTmsgList(Config.LIST_REQ_PAGE_START_NUM);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    public void getTmsgList(int pageNo) {
        PageReq<BasePage> pageReq = new PageReq<>();
        BasePage basePage = new BasePage();
        basePage.setId(1l);
        basePage.setPageNum(pageNo);
        basePage.setPageSize(10);
        pageReq.setData(basePage);
        requestEntity = new RequestEntity.Builder(ApiPath.MSG_LIST.path()).addParams(pageReq).build();
        new HttpRequest<PageRsp<TMsg, PageResult<TMsg>>>(this, requestEntity, mListView) {
            @Override
            protected void onSuccess(PageRsp<TMsg, PageResult<TMsg>> result) {
                super.onSuccess(result);
                Log.i("success\n" + JsonUtils.toJsonViewStr(result));
            }

            @Override
            protected void onFail(BaseRsp result) {
                super.onFail(result);
                Log.e("fail -> " + JsonUtils.toJson(result));
            }
        }.post();
    }

    @Override
    public void onPullRefresh() {
        getTmsgList(Config.LIST_REQ_PAGE_START_NUM);
    }

    @Override
    public void onLoadMore() {
        getTmsgList(mListAdapter.getCount() / requestEntity.getPageSize() + Config.LIST_REQ_PAGE_START_NUM);
    }

    private class MyListAdapter extends
            HolderListAdapter<MyListAdapter.ViewHolder, TMsg> {

        public MyListAdapter(List<TMsg> list) {
            super(list, MyListAdapter.ViewHolder.class);
        }

        @Override
        protected void onFillItemView(int position, View view,
                                      MyListAdapter.ViewHolder holder, TMsg data) {
            holder.tvId.setText(data.getId().toString());
            holder.tvTitle.setText(data.getCreateTime());
            holder.tvContent.setText(data.getState());
        }

        @Override
        protected View onCreateItemView(ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.item_tmsg,
                    parent, false);
        }

        @Override
        protected void onItemViewClick(View view, int viewId, TMsg data) {
        }

        class ViewHolder implements NoProguard {
            TextView tvId;
            TextView tvTitle;
            TextView tvContent;
            ImageView ivRadio;
        }
    }
}
