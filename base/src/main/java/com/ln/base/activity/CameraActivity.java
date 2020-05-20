package com.ln.base.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.base.R;
import com.ln.base.tool.BitmapUtils;
import com.ln.base.tool.CameraUtil;
import com.ln.base.tool.Md5Encrypt;
import com.ln.base.tool.StorageUtils;
import com.ln.base.view.CameraLayerView;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private int mCameraId = 0;

    //屏幕宽高
    private int screenWidth;
    private boolean isview = false;
    private ImageView img_camera;
    private int picHeight;
    private RelativeLayout homecamera_bottom_relative;
    private TextView tvCameraLight, tvFinish;
    private CameraLayerView cameraLayerView;
    private TextView tv_title;
    private byte[] picData;
    private LinearLayout llTitle;
    private TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        img_camera = findViewById(R.id.img_camera);
        img_camera.setOnClickListener(this);
        tvCameraLight = findViewById(R.id.tv_camera_light);
        tvCameraLight.setOnClickListener(this);
        tvFinish = findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(this);
        cameraLayerView = findViewById(R.id.camera_layer_view);
        cameraLayerView.setIsFront(getIntent().getBooleanExtra("isFront", false));
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getIntent().getBooleanExtra("isFront", false) ? getString(R.string.camera_front_title) : getString(R.string.camera_back_title));
        homecamera_bottom_relative = findViewById(R.id.homecamera_bottom_relative);
        llTitle = findViewById(R.id.ll_title);
        tvSave = findViewById(R.id.tv_save);
        tvSave.setOnClickListener(this);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_camera) {
            if (isview) {
                captrue();
                isview = false;
            }

        } else if (i == R.id.tv_finish) {
            if (((TextView) v).getText().equals(getString(R.string.cancel))) {
                finish();
            } else {
                picData = null;
                isview = true;
                mCamera.startPreview();
                showPicView();
            }

        } else if (i == R.id.tv_save) {
            savePic();

        } else if (i == R.id.tv_camera_light) {
            if (v.getTag() != null && (v.getTag()).equals("1")) {
                CameraUtil.getInstance().turnLightOn(mCamera);
                tvCameraLight.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_camera_light_off), null, null, null);
                tvCameraLight.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.spacing_smaller));
                tvCameraLight.setText(getString(R.string.camera_light_close));
                tvCameraLight.setTag("0");
            } else {
                CameraUtil.getInstance().turnLightOff(mCamera);
                tvCameraLight.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_camera_light_on), null, null, null);
                tvCameraLight.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.spacing_smaller));
                tvCameraLight.setText(getString(R.string.camera_light_open));
                tvCameraLight.setTag("1");
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mCamera == null) {
                getToastDialog().showToast(R.string.camera_open_error);
                finish();
                return;
            }
            if (mHolder != null) {
                startPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {

        }
        return camera;
    }

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
            camera.startPreview();
            isview = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void captrue() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                isview = false;
                mCamera.stopPreview();
                picData = data;

                showSureView();
            }
        });
    }

    private void showSureView() {
        tvCameraLight.setVisibility(View.INVISIBLE);
        tvFinish.setText(getString(R.string.camera_again));
        img_camera.setVisibility(View.INVISIBLE);
        cameraLayerView.setVisibility(View.INVISIBLE);
        llTitle.setVisibility(View.INVISIBLE);
        tvSave.setVisibility(View.VISIBLE);
    }

    private void showPicView() {
        tvCameraLight.setVisibility(View.VISIBLE);
        tvFinish.setText(getString(R.string.cancel));
        img_camera.setVisibility(View.VISIBLE);
        cameraLayerView.setVisibility(View.VISIBLE);
        llTitle.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.GONE);
    }

    private void savePic() {
        if (picData == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(picData, 0, picData.length);
        Bitmap saveBitmap = CameraUtil.getInstance().setTakePicktrueOrientation(mCameraId, bitmap);

        File saveImg = StorageUtils.getImageSavePath(CameraActivity.this, Md5Encrypt.md5(System.currentTimeMillis() + "") + ".jpg");
        if (!saveImg.exists()) {
            BitmapUtils.saveBitmap(saveBitmap, saveImg);
        }

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        if (!saveBitmap.isRecycled()) {
            saveBitmap.recycle();
        }

        picData = null;
        Intent intent = new Intent();
        intent.putExtra("imageUrl", saveImg.getAbsolutePath());
        intent.putExtra("width", screenWidth);
        intent.putExtra("height", picHeight);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropSizeForWidth(parameters.getSupportedPreviewSizes(), screenWidth);
        if (previewSize.width < previewSize.height) {
            parameters.setPreviewSize(previewSize.height, previewSize.width);
        } else {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        Camera.Size pictrueSize = CameraUtil.getInstance().getPropSizeForWidth(parameters.getSupportedPictureSizes(), screenWidth);
        if (previewSize.width < previewSize.height) {
            parameters.setPictureSize(pictrueSize.height, pictrueSize.width);
        } else {
            parameters.setPictureSize(pictrueSize.width, pictrueSize.height);
        }

        camera.setParameters(parameters);

    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

}
