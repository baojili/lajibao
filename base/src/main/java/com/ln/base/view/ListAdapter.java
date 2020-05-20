package com.ln.base.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import java.util.Collection;
import java.util.List;

public abstract class ListAdapter<V extends View, D> extends BaseAdapter implements OnClickListener,
        OnItemClickListener {

    protected List<D> mList;

    public ListAdapter(List<D> list) {
        mList = list;
    }

    @Override
    public final int getCount() {
        return mList.size();
    }

    @Override
    public final D getItem(int position) {
        return mList.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final View getView(int position, View convertView, ViewGroup parent) {
        final V view;
        if (convertView == null) {
            if (getViewTypeCount() > 1) {
                view = onCreateItemView(parent, position);
            } else {
                view = onCreateItemView(parent);
            }
            onInitItemView(position, view);
        } else {
            view = (V) convertView;
        }
        onFillItemView(position, view, getItem(position));
        return view;
    }

    protected V onCreateItemView(ViewGroup parent, int position) {
        return null;
    }

    protected V onCreateItemView(ViewGroup parent) {
        return null;
    }

    protected void onInitItemView(int position, V view) {
    }

    protected abstract void onFillItemView(int position, V view, D data);

    @Override
    public void onClick(View view) {
        D data = null;
        if (view.getTag() instanceof Integer) {
            data = getItem((Integer) view.getTag());
        }
        onItemViewClick(view, view.getId(), data);
    }

    /**
     * ListView may have header views, use id as real position
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id < 0) {
            onItemViewClick(view, view.getId(), null);
        } else {
            onItemViewClick(view, view.getId(), getItem((int) id));
        }
    }

    protected void onItemViewClick(View view, int viewId, D data) {
    }

    public final void refresh() {
        notifyDataSetChanged();
    }

    public final List<D> getDataList() {
        return mList;
    }

    @SuppressWarnings("unchecked")
    public final void setDataList(List<?> list) {
        mList = (List<D>) list;
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public final void addDataList(List<?> list) {
        if (list != null) {
            mList.addAll((Collection<? extends D>) list);
        }
        notifyDataSetChanged();
    }

    public final void removeItem(D item) {
        mList.remove(item);
        notifyDataSetChanged();
    }

    public final D removeItem(int position) {
        D item = mList.remove(position);
        notifyDataSetChanged();
        return item;
    }

    public final void clearItems() {
        mList.clear();
        notifyDataSetChanged();
    }

}