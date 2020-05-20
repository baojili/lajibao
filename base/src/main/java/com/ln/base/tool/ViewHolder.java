package com.ln.base.tool;

import android.util.SparseArray;
import android.view.View;

/**
 * 把原本adpter里面的getview 的 ViewHoder写法简单的封装下，省掉一些重复代码，不用在每个要用到的类里面建ViewHoler类；
 */
public class ViewHolder {
    private ViewHolder() {

    }

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
