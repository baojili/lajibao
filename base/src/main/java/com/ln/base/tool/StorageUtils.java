package com.ln.base.tool;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ln.base.config.Config;

import java.io.File;
import java.io.IOException;

/**
 * Provides application storage paths
 */
public final class StorageUtils {
    private StorageUtils() {
    }

    /**
     * Returns application cache directory. Cache directory will be created on
     * SD card <i>("/Android/data/[app_package_name]/cache")</i> if card is
     * mounted. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * Returns application cache directory. Cache directory will be created on
     * SD card <i>("/Android/data/[app_package_name]/files")</i> if card is
     * mounted. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getFilesDirectory(Context context) {
        File appFilesDir = null;
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            appFilesDir = getExternalFilesDir(context);
        }
        if (appFilesDir == null) {
            appFilesDir = context.getFilesDir();
        }
        return appFilesDir;
    }

    /**
     * Returns custom application cache directory . Cache directory will be
     * created on SD card
     * <i>("/Android/data/[app_package_name]/cache/customDir")</i> if card is
     * mounted. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getCustomCacheDirectory(Context context,
                                               String customDir) {
        File cacheDir = getCacheDirectory(context);
        File customCacheDir = new File(cacheDir, customDir);
        if (!customCacheDir.exists()) {
            if (!customCacheDir.mkdirs()) {
                customCacheDir = cacheDir;
            }
        }
        return customCacheDir;
    }

    /**
     * Returns custom application files directory . Cache directory will be
     * created on SD card
     * <i>("/Android/data/[app_package_name]/files/customDir")</i> if card is
     * mounted. Else - Android defines files directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getCustomFilesDirectory(Context context,
                                               String customDir) {
        File filesDir = getFilesDirectory(context);
        File customFilesDir = new File(filesDir, customDir);
        if (!customFilesDir.exists()) {
            if (!customFilesDir.mkdirs()) {
                customFilesDir = filesDir;
            }
        }
        return customFilesDir;
    }

    /**
     * Returns specified application cache directory. Cache directory will be
     * created on SD card by defined path if card is mounted. Else - Android
     * defines cache directory on device's file system.
     *
     * @param context    Application context
     * @param ownDirPath own directory path
     * @return Cache {@link File directory}
     */
    public static File getOwnDirectory(Context context, String ownDirPath) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),
                    ownDirPath);
        }
        if (appCacheDir == null
                || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        return getExternalDir(context, "cache");
    }

    private static File getExternalFilesDir(Context context) {
        return getExternalDir(context, "files");
    }

    private static File getExternalDir(Context context, String customDir) {
        File dataDir = new File(new File(
                Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCustomDir = new File(
                new File(dataDir, context.getPackageName()), customDir);
        if (!appCustomDir.exists()) {
            if (!appCustomDir.mkdirs()) {
                Log.w("StorageUtils",
                        "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCustomDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.w("StorageUtils",
                        "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCustomDir;
    }

    /**
     * 获取sd卡上的目录，不随app卸载而删除，目前目录定为sd卡下面[app_package_name]
     */
    public static File getPermanentDir(Context context, String customDir) {
        File permanentDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), customDir);
        if (!permanentDir.exists()) {
            if (!permanentDir.mkdirs()) {
                return null;
            }
        }
        return permanentDir;
    }

    /**
     * 获取图片保存目录，目前定为[app_package_name]/image
     */
    public static File getImageSavePath(Context context, String fileName) {
        File imageDir = getPermanentDir(context, Config.APP_IMAGE_SAVE_DIR);
        if (imageDir != null) {
            return new File(imageDir, fileName);
        }
        return null;
    }
}
