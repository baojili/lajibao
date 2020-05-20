package com.ln.base.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.model.ContactInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AndroidUtils {

    private static final String IDENTIFY_TYPE_ID = "id";
    private static final String IDENTIFY_TYPE_ATTR = "attr";
    private static final String IDENTIFY_TYPE_DRAWABLE = "drawable";

    //获取Drawable资源文件绝对路径
    public static Uri getResourcesUri(Context context, @DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return Uri.parse(uriPath);
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        // Uri分为三个部分   域名://主机名/路径/id
        // content://media/extenral/images/media/17766
        // content://com.android.providers.media.documents/document/image:2706
        // file://com.xxxx.xxxxx ---- 7.0 有限制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        final String scheme = uri.getScheme();
        String filePath = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (scheme == null)
                filePath = uri.getPath();
            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                filePath = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                filePath = getDataColumn(context, uri, null, null);
            }
        }
        return filePath;
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // 如果是document类型的 uri, 则通过document id来进行处理
                String documentId = DocumentsContract.getDocumentId(uri);
                if (isMediaDocument(uri)) { // MediaProvider
                    final String[] divide = documentId.split(":");
                    final String type = divide[0];
                    Uri mediaUri = null;
                    if ("image".equals(type)) {
                        mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    } else {
                        return null;
                    }
                    String selection = BaseColumns._ID + "=?";
                    String[] selectionArgs = {divide[1]};
                    filePath = getDataColumn(context, mediaUri, selection, selectionArgs);
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                    filePath = getDataColumn(context, contentUri, null, null);
                } else if (isExternalStorageDocument(uri)) {
                    String[] split = documentId.split(":");
                    if (split.length >= 2) {
                        String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        }
                    }
                }
            } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
                // 如果是 content 类型的 Uri
                filePath = getDataColumn(context, uri, null, null);
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                // 如果是 file 类型的 Uri,直接获取图片对应的路径
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    //获取设备屏幕分辨率
    public static DisplayMetrics getDeviceResolution(Context context) {
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        //int width = metrics.widthPixels;//获取到的是px，像素，绝对像素，需要转化为dpi
        //int height = metrics.heightPixels;
        return metrics;
    }

    //获取设备屏幕分辨率
    public static Point getDeviceResolution2(Context context) {
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point mRealSize = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWindowManager.getDefaultDisplay().getRealSize(mRealSize);
        }
        return mRealSize;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getDeviceStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setClipboardContent(Context context, String content) {
        final ClipboardManager clipboardManager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
        } else {
            clipboardManager.setText(content);
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    /**
     * get App versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context, int defaultCode) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return defaultCode;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manger.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isWifiAvailable(Context context) {
        try {
            ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return manger.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static void hideSoftKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
        imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
    }

    public static int getDialogTheme(Context context, int attr) {
        Theme theme = context.getTheme();
        if (theme != null) {
            TypedValue outValue = new TypedValue();
            theme.resolveAttribute(attr, outValue, true);
            return outValue.resourceId;
        }
        return 0;
    }

    public static boolean openPhoneDial(Context context, String phoneNumber) {
        try {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean openPhoneCall(Context context, String phoneNumber) {
        try {
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean openWebPage(Context context, String pageUrl) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Object obj = appInfo.metaData.get(name);
            if (obj != null) {
                return obj.toString();
            }
        } catch (NameNotFoundException e) {
        }
        return null;
    }

    public static String getDeviceId(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId != null) {
            deviceId = deviceId.trim().toLowerCase(Locale.getDefault());
        }
        if (deviceId == null || deviceId.equals("null") || deviceId.matches("[0]*")) {
            return "";
        }
        return deviceId;
    }

    public static String getOperator(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        if (imsi == null) {
            return null;
        } else if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
            return "mobile";
        } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
            return "unicom";
        } else if (imsi.startsWith("46003")) {
            return "telecom";
        } else {
            return null;
        }
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return "Android_" + Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        String model = android.os.Build.MODEL;
        model = model.replace(" ", "_");
        return model;
    }

    public static boolean isAppExist(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static void installApp(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void gotoDeviceDesktop(Context context) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(i);
    }

    public static void openApp(Context context, ComponentName componentName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        context.startActivity(intent);
    }

    public static void relaunchWithExitStatus(Context context, Class<?> relaunchActivityCls, int status) {
        try {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 100,
                    PendingIntent.getActivity(context, 0, new Intent(context, relaunchActivityCls),
                            PendingIntent.FLAG_ONE_SHOT));
            System.exit(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getIdentify(Context context, String name) {
        return getIdentify(context.getResources(), name, context.getPackageName());
    }

    public static int getIdentify(Resources resources, String name, String packageName) {
        return resources.getIdentifier(name, IDENTIFY_TYPE_ID, packageName);
    }

    public static int getAttrIdentify(Context context, String name) {
        return getAttrIdentify(context.getResources(), name, context.getPackageName());
    }

    public static int getAttrIdentify(Resources resources, String name, String packageName) {
        return resources.getIdentifier(name, IDENTIFY_TYPE_ATTR, packageName);
    }

    public static int getDrawableIdentify(Context context, String name) {
        return getDrawableIdentify(context.getResources(), name, context.getPackageName());
    }

    public static int getDrawableIdentify(Resources resources, String name, String packageName) {
        return resources.getIdentifier(name, IDENTIFY_TYPE_DRAWABLE, packageName);
    }

    public static Intent getCropImageIntent(File originalFile, File newFile) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(originalFile), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        /**将裁剪框区域的图片裁剪后进行缩放，到指定尺寸，scaleUpIfNeeded是针对小图进行放大的参数*/
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    public static Intent getCropImageIntent(Bitmap photo) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.putExtra("data", photo);
        intent.putExtra("crop", "true");

        /**将裁剪框区域的图片裁剪后进行缩放，到指定尺寸，scaleUpIfNeeded是针对小图进行放大的参数*/
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        return intent;
    }

    public static byte[] getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                return packageinfo.signatures[0].toByteArray();
            }
        }
        return null;
    }

    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    @SuppressWarnings("deprecation")
    public static boolean isTopActivity(Activity context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getRunningTasks(1) == null || am.getRunningTasks(1).size() == 0) {
            return false;
        }
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName().contains(context.getClass().getSimpleName());
    }

    @SuppressWarnings("deprecation")
    public static boolean isTopActivity(Context context, String name) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getRunningTasks(1) == null || am.getRunningTasks(1).size() == 0) {
            return false;
        }
        String cn = am.getRunningTasks(1).get(0).topActivity.getClassName();
        return cn.equals(name);
    }

    // This is added to fix soft navigationBar's overlapping to content above
    // LOLLIPOP
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasMenuKey = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        }
        // boolean hasMenuKey = KeyCharacterMap
        // .deviceHasKey(KeyEvent.KEYCODE_MENU);
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        // boolean hasHomeKey = KeyCharacterMap
        // .deviceHasKey(KeyEvent.KEYCODE_HOME);
        // && Build.VERSION.SDK_INT >=
        // 21//Build.VERSION_CODES.LOLLIPOP
        return !hasMenuKey && !hasBackKey;
    }

    /**
     * 把内容复制到剪切板, 高低版本的情况下ClipboardManager对象并不一样，考虑到兼容性的问题这里做个封装
     *
     * @param text    复制到剪切板的内容
     * @param context Context
     */
    public static void copyToClipboard(final String text, @NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // api level < 11
            @SuppressWarnings("deprecation")
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    context.getApplicationContext().getSystemService(
                            Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setText(text);
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).getToastDialog().showToast(R.string.copy_done);
                }
            }
        } else {
            // api level >= 11
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    context.getApplicationContext().getSystemService(
                            Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).getToastDialog().showToast(R.string.copy_done);
                }
            }
        }
    }

    public static ContactInfo getContact(BaseActivity activity, Intent data) {
        if (data == null) {
            return null;
        }
        if (data.getData() == null) {
            activity.getToastDialog().showToast(activity.getString(R.string.fail_read_contacts));
            return null;
        }
        Cursor cursor = activity.getContentResolver().query(data.getData(), null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            activity.getToastDialog().showToast(activity.getString(R.string.fail_read_contacts));
            return null;
        }
        String name = cursor.getString(cursor.getColumnIndex(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME));
        String phoneNumber = "";
        if ("1".equals(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))) {
            Cursor phones = activity.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)), null, null);
            if (phones == null || !phones.moveToFirst()) {
                activity.getToastDialog().showToast(activity.getString(R.string.fail_read_contacts));
                return null;
            }
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phones.close();
        }
        cursor.close();
        ContactInfo info = new ContactInfo();
        info.setName(name);
        info.setPhone(phoneNumber.replace(" ", ""));
        return info;
    }

    public static boolean isValidList(List data) {
        return data != null && data.size() > 0;
    }

    /**
     * 获取设备唯一标识符
     *
     * @param context
     * @return
     */
    public static String getDeviceUnicodeId(Context context) {
        //读取保存的在sd卡中的唯一标识符
        String deviceId = readDeviceID(context);
        //用于生成最终的唯一标识符
        StringBuffer s = new StringBuffer();
        //判断是否已经生成过,
        if (deviceId != null && !"".equals(deviceId.trim())) {
            return deviceId;
        }
        try {
            //获取IMES(也就是常说的DeviceId)
            deviceId = getIMIEStatus(context);
            s.append(deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //获取设备的MACAddress地址 去掉中间相隔的冒号
            deviceId = getLocalMac().replace(":", "");
            s.append(deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }

        //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
        if (s == null || s.length() <= 0) {
            UUID uuid = UUID.randomUUID();
            deviceId = uuid.toString().replace("-", "");
            s.append(deviceId);
        }
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
        String md5 = StringUtils.getMD5(s.toString(), false);
        if (s.length() > 0) {
            //持久化操作, 进行保存到SD卡中
            saveDeviceID(md5, context);
        }
        return md5;
    }


    /**
     * 读取固定的文件中的内容,这里就是读取sd卡中保存的设备唯一标识符
     *
     * @param context
     * @return
     */
    public static String readDeviceID(Context context) {
        File file = getDeviceDir(context);
        if (!file.exists()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            Reader in = new BufferedReader(isr);
            int i;
            while ((i = in.read()) > -1) {
                buffer.append((char) i);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取设备的DeviceId(IMES) 这里需要相应的权限<br/>
     * 需要 READ_PHONE_STATE 权限
     *
     * @param context
     * @return
     */
    private static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String deviceId = tm.getDeviceId();
        return deviceId;
    }


    /**
     * 获取设备MAC 地址 由于 6.0 以后 WifiManager 得到的 MacAddress得到都是 相同的没有意义的内容
     * 所以采用以下方法获取Mac地址
     *
     * @return
     */
    private static String getLocalMac() {
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "";
            }
            byte[] addr = networkInterface.getHardwareAddress();


            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
        return macAddress;
    }

    /**
     * 保存 内容到 SD卡中,  这里保存的就是 设备唯一标识符
     *
     * @param str
     * @param context
     */
    private static boolean saveDeviceID(String str, Context context) {
        File file = getDeviceDir(context);
        if (!file.exists()) {
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 统一处理设备唯一标识 保存的文件的地址
     *
     * @param context
     * @return
     */
    private static File getDeviceDir(Context context) {
        File deviceFile = StorageUtils.getCustomFilesDirectory(context, "device");
        if (deviceFile == null) return null;
        File DEVICEfile = new File(deviceFile, ".DEVICE");
        return DEVICEfile;
    }

}