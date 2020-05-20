package com.ln.base.dialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.tool.FileUtils;
import com.ln.base.tool.StorageUtils;

import java.io.File;
import java.util.UUID;

public class PickPhotoPopupMenu extends PopupMenu {

    private BaseActivity context;
    private File photoFolder;
    private File photoFile;
    private int requestCode;

    public PickPhotoPopupMenu(BaseActivity context, int requestCode) {
        super(context, new String[]{
                context.getString(R.string.pick_from_gallery),
                context.getString(R.string.pick_from_camera)});
        this.context = context;
        this.requestCode = requestCode;
        photoFolder = StorageUtils.getCustomCacheDirectory(context, "localImg");
        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
    }

    @Override
    public void onMenuSelect(int position) {
        photoFile = new File(photoFolder, UUID.randomUUID().toString() + ".jpg");
        photoFile.delete();
        Intent intent;
        if (position == 0) {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        }
        try {
            context.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            context.getToastDialog().showToast(
                    context.getString(R.string.cannot_use_system)
                            + (position == 0 ? context
                            .getString(R.string.gallery) : context
                            .getString(R.string.camera)));
        }
    }

    public File getPhotoFileFromReault(Context context, Intent data) {
        if (!photoFile.exists() && data != null && data.getData() != null) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(data.getData(),
                    filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String pickPhotoPath = cursor.getString(columnIndex);
                    if (!FileUtils.copyFile(new File(pickPhotoPath), photoFile)) {
                        photoFile.delete();
                    }

                }
                cursor.close();
            }
        }
        return photoFile;
    }

}