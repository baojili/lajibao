package com.ln.base.dialog;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.activity.CameraActivity;
import com.ln.base.config.Config;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.BitmapUtils;
import com.ln.base.tool.FileUtils;
import com.ln.base.tool.StorageUtils;

import java.io.File;
import java.util.UUID;

public class PickPhotoDialog extends Dialog {

    private BaseActivity context;
    private File photoFile;
    private Integer requestCode;
    private String title;
    private boolean isCustomCamera = false;
    private boolean isFront = false;

    private LinearLayout tvCamare, tvLocal;
    //fragment不为空时，将调用该fragment的的startActivityForResult，结果也是回调该fragment的onActivityResult
    private Fragment fragment;

    public PickPhotoDialog(BaseActivity context, Integer requestCode) {
        this(context, null, requestCode);
    }

    public PickPhotoDialog(BaseActivity context, String title) {
        this(context, title, null);
    }

    public PickPhotoDialog(BaseActivity context) {
        this(context, null, null);
    }

    public PickPhotoDialog(BaseActivity context, String title, Integer requestCode) {
        super(context);
        this.context = context;
        this.title = title;
        this.requestCode = requestCode;
        init();
    }

    private void init() {
        photoFile = StorageUtils.getCustomCacheDirectory(context, "localImg");
        photoFile = new File(photoFile, UUID.randomUUID().toString() + ".jpg");
        setTitle(title);
        setContentView(R.layout.dialog_pick_photo);

        tvCamare = findViewById(R.id.ll_camera);
        tvLocal = findViewById(R.id.ll_local);

        tvCamare.setOnClickListener(this);
        tvLocal.setOnClickListener(this);
    }

    public void show(int requestCode) {
        this.requestCode = requestCode;
        super.show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.ll_camera) {
            dismiss();
            execute(1);
        } else if (view.getId() == R.id.ll_local) {
            dismiss();
            execute(0);
        }
    }

    private void execute(int operate) {
        if (requestCode == null) return;
        photoFile.delete();

        Intent intent;
        if (operate == 0) {// from gallery
            /*intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
            intent = new Intent(Intent.ACTION_GET_CONTENT,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        } else {// from camera
            if (isCustomCamera) {
                intent = new Intent(context, CameraActivity.class);
                intent.putExtra("isFront", isFront);
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoUri = null;
                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //Log.e(context.getPackageName());
                    photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", photoFile);
                    //intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    photoUri = Uri.fromFile(photoFile);
                    //intent.setDataAndType(Uri.fromFile(photoFile), "application/vnd.android.package-archive");
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            }
        }
        try {
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                context.startActivityForResult(intent, requestCode);
            }
        } catch (ActivityNotFoundException e) {
            context.getToastDialog().showToast(
                    context.getString(R.string.cannot_use_system)
                            + (operate == 0 ? context
                            .getString(R.string.gallery) : context
                            .getString(R.string.camera)));
        }
    }


    public File getPhotoFileFromResult(BaseActivity context, Intent data) {
        return getPhotoFileFromResult(context, data, false);
    }

    public File getPhotoFileFromResult(BaseActivity context, Intent data,
                                       boolean isOrigin) {
        if (!photoFile.exists() && data != null && data.getData() != null) {
            String realPath = AndroidUtils.getRealPathFromUri(context, data.getData());
            if (realPath != null) {
                try {
                    FileUtils.copyFile(new File(realPath), photoFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //confirm the crop image is the original image
        if (photoFile.exists() && !isOrigin) {
//			BitmapUtils.compressBitmap(photoFile, Config.PHOTO_COMPRESS_WIDTH, Config.PHOTO_COMPRESS_HEIGHT);
            compressBitmap(photoFile);
            File tempFile = StorageUtils.getCustomCacheDirectory(context, "localImg");
            tempFile = new File(tempFile, photoFile.getName().split("\\.")[0] + "_tmp.jpg");
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        return photoFile;
    }

    protected void compressBitmap(File photoFile) {
        BitmapUtils.compressBitmap(photoFile, Config.PHOTO_COMPRESS_WIDTH, Config.PHOTO_COMPRESS_HEIGHT);
    }

    public File getPhotoFileFromFile(BaseActivity context, File data) {
        //confirm the crop image is the original image
        if (data.exists()) {
            compressBitmap(photoFile);
//			BitmapUtils.compressBitmap(data, Config.PHOTO_COMPRESS_WIDTH,
//					Config.PHOTO_COMPRESS_HEIGHT);
        }
        return data;
    }

    public void doCropImage(BaseActivity context, Intent data, int requestCode) {
        getPhotoFileFromResult(context, data, true);
        if (!photoFile.exists()) return;
        File tempFile = StorageUtils.getCustomCacheDirectory(context, "localImg");
        tempFile = new File(tempFile, photoFile.getName().split("\\.")[0] + "_tmp.jpg");
        FileUtils.copyFile(photoFile, tempFile);
        if (fragment != null) {
            fragment.startActivityForResult(
                    AndroidUtils.getCropImageIntent(tempFile, photoFile),
                    requestCode);
        } else {
            context.startActivityForResult(
                    AndroidUtils.getCropImageIntent(tempFile, photoFile),
                    requestCode);
        }

    }

    public boolean isCustomCamera() {
        return isCustomCamera;
    }

    public void setCustomCamera(boolean customCamera) {
        isCustomCamera = customCamera;
    }

    public boolean isFront() {
        return isFront;
    }

    public void setFront(boolean front) {
        isFront = front;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

}