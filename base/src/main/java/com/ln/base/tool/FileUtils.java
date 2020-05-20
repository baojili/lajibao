package com.ln.base.tool;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    /**
     * File buffer stream size.
     */
    public static final int FILE_STREAM_BUFFER_SIZE = 16 * 1024;

    public static boolean copyFile(String fromPath, String toPath) {
        return copyFile(new File(fromPath), new File(toPath));
    }

    public static boolean copyFile(File from, File to) {
        if (from.exists()) {
            FileChannel inChannel = null, outChannel = null;
            try {
                inChannel = new FileInputStream(from).getChannel();
                outChannel = new FileOutputStream(to).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inChannel != null || outChannel != null) {
                    try {
                        inChannel.close();
                        outChannel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    public static void downloadFile(String fileUrl, File file) {
        File tempFile = new File(file.getAbsolutePath() + ".temp");
        if (tempFile.exists()) {
            tempFile.delete();
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
            }
        }

        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                fos = new FileOutputStream(tempFile);
                is = conn.getInputStream();
                byte[] buffer = new byte[4 * 1024];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
                tempFile.renameTo(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists())
            return false;
        if (file.isFile()) {
            return file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * Creates a pseudo-unique filename for the specified cache key.
     *
     * @param key The key to generate a file name for.
     * @return A pseudo-unique filename.
     */
    private static String getFileNameForKey(String key) {
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
        return localFilename;
    }

    /**
     * Returns a file object for the given cache key.
     */
    public static File getFileForKey(File mRootDirectory, String key) {
        if (!mRootDirectory.exists()) {
            mRootDirectory.mkdirs();
        }
        return new File(mRootDirectory, getFileNameForKey(key));
    }

    /**
     * Returns a file object for the given cache key.
     */
    public static File getFileForKey(String mRootDirectory, String key) {
        File rootFile = new File(mRootDirectory);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        return new File(mRootDirectory, getFileNameForKey(key));
    }

    public static void clearCookies(Context context) {
        CookieSyncManager cookieSyncMngr =
                CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
    }

    /**
     * String to OutputStream
     *
     * @param str    String
     * @param stream OutputStream
     * @return success or not
     */
    public static boolean stringToStream(@Nullable String str, @NonNull OutputStream stream) {
        if (str == null) {
            return false;
        }

        byte[] data;
        try {
            data = str.getBytes(StandardCharsets.UTF_8);
            stream.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(stream);
        }

        return false;
    }

    /**
     * String to file
     *
     * @param str  String
     * @param file File
     * @return success or not
     */
    public static boolean stringToFile(@Nullable String str, @NonNull File file) {
        return stringToFile(str, file, false);
    }

    /**
     * String to file
     *
     * @param str    String
     * @param file   File
     * @param append is append mode or not
     * @return success or not
     */
    public static boolean stringToFile(@Nullable String str, @NonNull File file, boolean append) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file, append);
            return stringToStream(str, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(stream);
        }
    }

    /**
     * stream to file
     *
     * @param in   Inputstream
     * @param file File
     * @return success or not
     */
    public static boolean streamToFile(@NonNull InputStream in, @NonNull File file) {
        return streamToFile(in, file, false);
    }

    /**
     * stream to file
     *
     * @param in     Inputstream
     * @param file   File
     * @param append is append mode or not
     * @return success or not
     */
    public static boolean streamToFile(@NonNull InputStream in, @NonNull File file, boolean append) {
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file, append));
            final byte[] buffer = new byte[FILE_STREAM_BUFFER_SIZE];
            int n;
            while (-1 != (n = in.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(output);
            close(in);
        }
        return false;
    }

    /**
     * 关闭，并捕获IOException
     *
     * @param closeable Closeable
     */
    public static void close(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static String getExtensionName(String filename, String defaultPrefix) {
        String prefix = null;
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                prefix = filename.substring(dot + 1);
            }
        } else {
            if (!TextUtils.isEmpty(defaultPrefix)) {
                prefix = defaultPrefix;
            }
        }
        return prefix;
    }

    /**
     * 读取filePath的文件，将文件中的数据按照行读取到String数组中
     *
     * @param filePath 文件的路径
     * @return 文件中一行一行的数据
     */
    public static String readToString(String filePath) {
        File file = new File(filePath);
        Long fileLength = file.length(); // 获取文件长度
        if (fileLength == 0) return null;
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(fileContent);// 返回文件内容,默认编码
    }

    public static String readToString2(String filePath) {
        File file = new File(filePath);
        if (file.length() == 0) return null;
        FileInputStream is = null;
        StringBuilder stringBuilder = null;
        try {
            /**
             * 文件有内容才去读文件
             */
            is = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return String.valueOf(stringBuilder);

    }

}