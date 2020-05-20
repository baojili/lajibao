package com.ln.base.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.model.NoProguard;
import com.ln.base.tool.DimenUtils;
import com.ln.base.view.HolderListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SelectorWindow extends DropdownWindow {

    /**
     * !注意：为了适配安卓7.0，应采用带shadowHeight参数的构造方法
     */
    private ListView mListView;
    private SelectorListAdapter selectorListAdapter;

    public SelectorWindow(Context context) {
        this(context, new ArrayList<String>(), 0);
    }

    public SelectorWindow(Context context, String[] strings) {
        this(context, Arrays.asList(strings), 0);
    }

    /**
     * 适配安卓7.0
     *
     * @param context
     * @param list
     * @param shadowHeight 阴影面的高度
     */
    public SelectorWindow(Context context, List<String> list, int shadowHeight) {
        super(context, (long) shadowHeight);
        selectorListAdapter = new SelectorListAdapter(list);
        mListView = new ListView(context);
        mListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dp2px(context, 44 * list.size())));
        addContent(mListView);
        mListView.setAdapter(selectorListAdapter);
        mListView.setOnItemClickListener(selectorListAdapter);
    }

    protected abstract void onItemViewClick(int position, String data);

    public final List<String> getDataList() {
        return selectorListAdapter.getDataList();
    }

    public final void setDataList(List<String> list) {
        selectorListAdapter.setDataList(list);
    }

    public final void setDataList(String[] strings) {
        this.setDataList(Arrays.asList(strings));
    }

    public final void addDataList(List<String> list) {
        selectorListAdapter.addDataList(list);
    }

    public final void removeItem(String item) {
        selectorListAdapter.removeItem(item);
    }

    public final String removeItem(int position) {
        return selectorListAdapter.removeItem(position);
    }

    public final void clearItems() {
        selectorListAdapter.clearItems();
    }

    public final void addDataList(String[] strings) {
        this.addDataList(Arrays.asList(strings));
    }

    private class SelectorListAdapter extends
            HolderListAdapter<SelectorListAdapter.ViewHolder, String> {

        public SelectorListAdapter(List<String> list) {
            super(list, SelectorListAdapter.ViewHolder.class);
        }

        @Override
        protected void onInitItemView(View view, ViewHolder holder) {
            super.onInitItemView(view, holder);
        }

        @Override
        protected void onFillItemView(int position, View view,
                                      ViewHolder holder, String data) {
            holder.tv.setText(data);
        }

        @Override
        protected View onCreateItemView(ViewGroup parent) {
            return ((BaseActivity) getContentlLayout().getContext())
                    .getLayoutInflater().inflate(R.layout.item_selector,
                            parent, false);
        }

        @Override
        protected void onItemViewClick(View view, int viewId, String data) {
            dismiss();
            SelectorWindow.this.onItemViewClick(getDataList().indexOf(data),
                    data);
        }

        class ViewHolder implements NoProguard {
            TextView tv;
        }
    }
}