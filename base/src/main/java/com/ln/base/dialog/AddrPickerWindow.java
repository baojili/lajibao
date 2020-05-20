package com.ln.base.dialog;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.model.CityModel;
import com.ln.base.model.DistrictModel;
import com.ln.base.model.ProvinceModel;
import com.ln.base.tool.Log;
import com.ln.base.tool.StringUtils;
import com.ln.base.tool.XmlParserHandler;
import com.ln.base.view.WheelPicker;
import com.ln.base.view.WheelPicker.OnSelectLineChangeListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class AddrPickerWindow extends BottomWindow implements
        OnSelectLineChangeListener {

    private List<ProvinceModel> provinceList = null;
    /**
     * 省、市、区选择器
     */
    private WheelPicker wheelPickerProvince, wheelPickerCity,
            wheelPickerDistrict;
    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    private Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    /*** key - 区 values - 邮编 */
//	private Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
    /**
     * 当前省的名称
     */
    private String mCurrentProviceName = "";
    /*** 当前市的名称 */
    private String mCurrentCityName = "";
    /*** 当前区的名称 */
    private String mCurrentDistrictName = "";
    /*** 当前区的邮政编码 */
//	private String mCurrentZipCode = "";
    /*** 当前省、市、区编号 */
    private int currentProviceId = 0, currentCityId = 0, currentDistrictId = 0;


    public AddrPickerWindow(BaseActivity context, String title) {
        super(context, title);
        initData();
        initView();
    }

    public AddrPickerWindow(BaseActivity context, String title,
                            String provice, String city, String distrect) {
        super(context, title);
        update(provice, city, distrect);
    }

    public void update(String provice, String city, String distrect) {
        mCurrentProviceName = provice;
        mCurrentCityName = city;
        mCurrentDistrictName = distrect;
        initData();
        initView();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        if (getContentLayout().getChildCount() == 1) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.dialog_pick_address, getContentLayout(), false);
            wheelPickerProvince = view
                    .findViewById(R.id.wp_province);
            wheelPickerCity = view.findViewById(R.id.wp_city);
            wheelPickerDistrict = view
                    .findViewById(R.id.wp_district);
            wheelPickerProvince.setOnSelectLineChangeListener(this);
            wheelPickerCity.setOnSelectLineChangeListener(this);
            wheelPickerDistrict.setOnSelectLineChangeListener(this);
            wheelPickerProvince.setAdapter(new WheelPicker.StringArrayAdapter(
                    mProvinceDatas));

            wheelPickerProvince.setOnTouchListener(new RestorOnTanchListener());
            wheelPickerCity.setOnTouchListener(new RestorOnTanchListener());
            wheelPickerDistrict.setOnTouchListener(new RestorOnTanchListener());
            addContent(view);
        }
        if (currentProviceId != 0) {
            wheelPickerProvince.setSelectLine(currentProviceId);
        }
        updateCities();
        updateAreas();
    }

    /**
     * 初始化数据，从本地读取城市数据
     */
    private void initData() {
        try {
            if (provinceList == null) {
                AssetManager asset = mContext.getAssets();
                InputStream input = asset.open("abd.xml");
                // 创建一个解析xml的工厂对象
                SAXParserFactory spf = SAXParserFactory.newInstance();
                // 解析xml
                SAXParser parser = spf.newSAXParser();
                XmlParserHandler handler = new XmlParserHandler();
                parser.parse(input, handler);
                input.close();
                // 获取解析出来的数据
                provinceList = handler.getDataList();
            }
            if (mCurrentProviceName == null || mCurrentCityName == null || mCurrentDistrictName == null) {
                return;
            }
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                if (StringUtils.isValid(mCurrentProviceName)
                        && mProvinceDatas[i].startsWith(mCurrentProviceName)) {
                    currentProviceId = i;
                }
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    if (StringUtils.isValid(mCurrentCityName)
                            && cityNames[j].startsWith(mCurrentCityName)) {
                        currentCityId = j;
                    }

                    List<DistrictModel> districtList = cityList.get(j)
                            .getDistrictList();
                    String[] distrinctNameArray = new String[districtList
                            .size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList
                            .size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(
                                districtList.get(k).getName());

                        if (StringUtils.isValid(mCurrentDistrictName)
                                && districtList.get(k).getName()
                                .startsWith(mCurrentDistrictName)) {
                            currentDistrictId = k;
                        }
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
//						mZipcodeDatasMap.put(districtList.get(k).getName(),
//								"");
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = wheelPickerCity.getSelectLine();
        if (pCurrent < 0 || mCitisDatasMap.get(mCurrentProviceName) == null || mCitisDatasMap.get(mCurrentProviceName).length < 1) {
            mCurrentCityName = "";
        } else {
            mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        }
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        wheelPickerDistrict
                .setAdapter(new WheelPicker.StringArrayAdapter(areas));
        if (currentDistrictId != 0) {
            wheelPickerDistrict.setSelectLine(currentDistrictId);
        } else {
            wheelPickerDistrict.setSelectLine(0);
        }
        Log.e("Areas lint:" + wheelPickerCity.getSelectLine());
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = wheelPickerProvince.getSelectLine();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        wheelPickerCity.setAdapter(new WheelPicker.StringArrayAdapter(cities));
        if (currentCityId != 0) {
            wheelPickerCity.setSelectLine(currentCityId);
        } else {
            wheelPickerCity.setSelectLine(0);
        }
        Log.e("citi lint:" + wheelPickerCity.getSelectLine());

        updateAreas();
    }

    @Override
    public void onSelectLineChange(WheelPicker wheelPicker, int selectLine) {
        if (wheelPicker == wheelPickerProvince) {
            updateCities();
        } else if (wheelPicker == wheelPickerCity) {
            updateAreas();
        } else if (wheelPicker == wheelPickerDistrict) {
            if (mDistrictDatasMap.get(mCurrentCityName) != null && mDistrictDatasMap.get(mCurrentCityName).length > 0) {
                mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[selectLine];
            } else {
                mCurrentDistrictName = "";
            }
//			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    public void onConfirm(String provice, String city, String distrect) {

    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        onConfirm(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName);
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

    /**
     * 地区选择器 触碰事件，处理手指开始滑动后默认的地址区域ID清除
     *
     * @author
     */
    private class RestorOnTanchListener implements OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (currentProviceId != 0 || currentCityId != 0
                    || currentDistrictId != 0) {
                currentProviceId = 0;
                currentCityId = 0;
                currentDistrictId = 0;
            }
            return false;
        }

    }

}