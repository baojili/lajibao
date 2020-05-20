package com.ln.base.dialog;

import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.model.CityModel;
import com.ln.base.model.NoProguard;
import com.ln.base.model.ProvinceModel;
import com.ln.base.tool.StringUtils;
import com.ln.base.tool.XmlParserHandler;
import com.ln.base.view.HolderListAdapter;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class AddrSelectorWindow extends DropdownWindow {
    /**
     * !注意：为了适配安卓7.0，应采用带shadowHeight参数的构造方法
     */

    private List<ProvinceModel> provinceList = null;
    private ListView proListView, cityListView;
    private ProvinceListAdapter proAdapter;
    private CityListAdapter cityAdapter;
    private String mCurProvince = "";
    private String mCurCity = "";
    private int curProvicePosition = 0, curCityPosition = 0;

    private BaseActivity mContext = null;

    public AddrSelectorWindow(BaseActivity context, int shadowHeight) {
        this(context, "", "", shadowHeight);
    }

    /**
     * 为了适配安卓7.0
     *
     * @param context
     * @param province
     * @param city
     * @param shadowHeight 计算阴影面积的高度
     */
    public AddrSelectorWindow(BaseActivity context, String province, String city, int shadowHeight) {
        super(context, (long) shadowHeight);
        mContext = context;
        mCurProvince = province;
        mCurCity = city;
        initData();
        initView();
        proListView.setSelection(curProvicePosition);
        cityListView.setSelection(curCityPosition);
    }

    public void update(String province, String city) {
        update(province, city, false);
    }

    private void update(String province, String city, boolean isAuto) {
        mCurProvince = province;
        mCurCity = city;
        initData();
        proAdapter.refresh();
        if (!isAuto)
            proListView.setSelection(curProvicePosition);
        cityAdapter.setDataList(provinceList.get(curProvicePosition)
                .getCityList());
        cityListView.setSelection(curCityPosition);
    }

    private void initView() {
        if (getContentlLayout().getChildCount() > 0 || provinceList == null
                || provinceList.size() == 0)
            return;

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_address_selector, getContentlLayout(), false);
        proListView = view.findViewById(R.id.pro_list_view);
        proAdapter = new ProvinceListAdapter(provinceList);
        proListView.setAdapter(proAdapter);
        proListView.setOnItemClickListener(proAdapter);
        cityListView = view.findViewById(R.id.city_list_view);
        cityAdapter = new CityListAdapter(provinceList.get(curProvicePosition)
                .getCityList());
        cityListView.setAdapter(cityAdapter);
        cityListView.setOnItemClickListener(cityAdapter);
        addContent(view);
    }

    protected void onCitySelected(String province, String city) {
    }

    private void initData() {
        try {
            if (provinceList == null) {
                AssetManager asset = mContext.getAssets();
                InputStream input = asset.open("abd.xml");
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser parser = spf.newSAXParser();
                XmlParserHandler handler = new XmlParserHandler();
                parser.parse(input, handler);
                input.close();
                provinceList = handler.getDataList();
            }
            if (mCurProvince == null || mCurCity == null) {
                return;
            }
            curProvicePosition = 0;
            curCityPosition = 0;
            for (int i = 0; i < provinceList.size(); i++) {
                if (i == 0 && !StringUtils.isValid(mCurProvince)) {
                    mCurProvince = provinceList.get(i).getName();
                }
                if (provinceList.get(i).getName().startsWith(mCurProvince)
                        || mCurProvince
                        .startsWith(provinceList.get(i).getName())) {
                    curProvicePosition = i;
                    for (int j = 0; j < provinceList.get(i).getCityList()
                            .size(); j++) {
                        if (StringUtils.isValid(mCurCity)
                                && (provinceList.get(i).getCityList().get(j)
                                .getName().startsWith(mCurCity) || mCurCity
                                .startsWith(provinceList.get(i)
                                        .getCityList().get(j).getName()))) {
                            curCityPosition = j;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
        }
    }

    private class ProvinceListAdapter extends
            HolderListAdapter<ProvinceListAdapter.ViewHolder, ProvinceModel> {

        public ProvinceListAdapter(List<ProvinceModel> list) {
            super(list, ViewHolder.class);
        }

        @Override
        protected void onInitItemView(View view, ViewHolder holder) {
            super.onInitItemView(view, holder);
        }

        @Override
        protected void onFillItemView(int position, View view,
                                      ViewHolder holder, ProvinceModel data) {
            holder.tv.setText(data.getName());
            if (curProvicePosition == position) {
                holder.contentLayout.setBackgroundResource(R.drawable.bg_white);
            } else {
                holder.contentLayout.setBackgroundResource(R.drawable.bg_gray_lighter);
            }
        }

        @Override
        protected View onCreateItemView(ViewGroup parent) {
            return ((BaseActivity) getContentlLayout().getContext())
                    .getLayoutInflater().inflate(R.layout.item_selector,
                            parent, false);
        }

        @Override
        protected void onItemViewClick(View view, int viewId, ProvinceModel data) {
            update(data.getName(), "", true);
        }

        class ViewHolder implements NoProguard {
            TextView tv;
            RelativeLayout contentLayout;
        }
    }

    private class CityListAdapter extends
            HolderListAdapter<CityListAdapter.ViewHolder, CityModel> {

        public CityListAdapter(List<CityModel> list) {
            super(list, ViewHolder.class);
        }

        @Override
        protected void onInitItemView(View view, ViewHolder holder) {
            super.onInitItemView(view, holder);
        }

        @Override
        protected void onFillItemView(int position, View view,
                                      ViewHolder holder, CityModel data) {
            holder.tv.setText(data.getName());
        }

        @Override
        protected View onCreateItemView(ViewGroup parent) {
            return ((BaseActivity) getContentlLayout().getContext())
                    .getLayoutInflater().inflate(R.layout.item_selector,
                            parent, false);
        }

        @Override
        protected void onItemViewClick(View view, int viewId, CityModel data) {
            dismiss();
            mCurCity = data.getName();
            onCitySelected(mCurProvince, mCurCity);

        }

        class ViewHolder implements NoProguard {
            TextView tv;
        }
    }
}