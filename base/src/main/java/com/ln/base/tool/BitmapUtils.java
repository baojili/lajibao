package com.ln.base.tool;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {

    public static final String WIDTH_PLACEHOLDER = "{WIDTH}";
    public static final String HEIGHT_PALACEHOLDER = "{HEIGHT}";

    /**
     * 定缩略图的宽最少为<Width>，高最少为<Height>，进行等比缩放，不裁剪。如果只指定 w 参数或只指定 h 参数，代表长宽限定为同样的值
     */
    private static String THUMB_SUFFIX = "?imageView2/0/w/" + WIDTH_PLACEHOLDER + "/h/" + HEIGHT_PALACEHOLDER;


    public static Bitmap downloadBitmap(String bitmapUrl) {
        InputStream is = null;
        try {
            URL url = new URL(bitmapUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                return BitmapFactory.decodeStream(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void downloadBitmap(String bitmapUrl, File imageFile) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(bitmapUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                fos = new FileOutputStream(imageFile);
                is = conn.getInputStream();
                byte[] buffer = new byte[4 * 1024];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
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

    public static void compressBitmap(String filePath, int imageWidth, int imageHeight, int quality) {
        compressBitmap(new File(filePath), imageWidth, imageHeight, quality);
    }

    public static void compressBitmap(File photoFile, int imageWidth, int imageHeight) {
        compressBitmap(photoFile, imageWidth, imageHeight, 100);
    }

    public static void compressBitmap(File photoFile, int imageWidth, int imageHeight, int quality) {
        if (photoFile == null) {
            return;
        }
        Bitmap bitmap = getBitmap(photoFile, imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(photoFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                fos.flush();
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param imageFileStream 图片绝对路径
     * @return 图片的旋转角度
     */

    private static int getBitmapDegree(InputStream imageFileStream) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(imageFileStream);
            } else {
                return 0;
            }
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    // public static void compressBitmap(File photoFile, int ratioX, int ratioY)
    // {
    // Bitmap bitmap = getBitmap(photoFile, PHOTO_WIDTH);
    // if (bitmap != null) {
    // int width = bitmap.getWidth();
    // int height = bitmap.getHeight();
    // if (ratioX > 0 && ratioY > 0 && (width / ratioX != height / ratioY)) {
    // int finalWidth = width;
    // int finalHeight = height;
    // if (width / ratioX > height / ratioY) {
    // finalWidth = height / ratioY * ratioX;
    // } else {
    // finalHeight = width / ratioX * ratioY;
    // }
    // Bitmap temp = Bitmap.createBitmap(bitmap, (width - finalWidth) / 2,
    // (height - finalHeight) / 2, finalWidth, finalHeight);
    // bitmap.recycle();
    // bitmap = temp;
    // }
    // FileOutputStream fos = null;
    // try {
    // fos = new FileOutputStream(photoFile);
    // bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
    // fos.flush();
    // bitmap.recycle();
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // try {
    // fos.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // }

    public static Bitmap getBitmap(byte[] data) {
        return getBitmap(data, 0, 0, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap getBitmap(byte[] data, int maxWidth, int maxHeight, Config decodeConfig) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPurgeable = true;
        decodeOptions.inInputShareable = true;
        decodeOptions.inPreferredConfig = decodeConfig;
        Bitmap bitmap = null;
        if (maxWidth == 0 && maxHeight == 0) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
            // decodeOptions.inPreferQualityOverSpeed =
            // PREFER_QUALITY_OVER_SPEED;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmap(String imagePath) {
        return getBitmap(new File(imagePath), 0, 0, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap getBitmap(String imagePath, int maxWidth, int maxHeight) {
        return getBitmap(new File(imagePath), maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap getBitmap(File imageFile) {
        return getBitmap(imageFile, 0, 0, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap getBitmap(File imageFile, int maxWidth, int maxHeight, Config decodeConfig) {
        if (!imageFile.exists()) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPurgeable = true;
        decodeOptions.inInputShareable = true;
        decodeOptions.inPreferredConfig = decodeConfig;
        InputStream is = null;
        try {
            if (maxWidth == 0 && maxHeight == 0) {
                is = new FileInputStream(imageFile);
                bitmap = BitmapFactory.decodeStream(is, null, decodeOptions);
            } else {
                decodeOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageFile.getPath(), decodeOptions);
                int actualWidth = decodeOptions.outWidth;
                int actualHeight = decodeOptions.outHeight;
                if (actualWidth > actualHeight && maxWidth < maxHeight) {
                    int temp = maxWidth;
                    maxWidth = maxHeight;
                    maxHeight = temp;
                }
                // Then compute the dimensions we would ideally like to
                // decode to.
                int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
                int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);
                // Decode to the nearest power of two scaling factor.
                decodeOptions.inJustDecodeBounds = false;
                decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
                Log.i("(actualWidth,actualHeight)=(" + actualWidth + "," + actualHeight
                        + ") -> (desiredWidth,desiredHeight)=(" + desiredWidth + "," + desiredHeight
                        + ")\nbestRatio = " + decodeOptions.inSampleSize);
                is = new FileInputStream(imageFile);
                Bitmap tempBitmap = BitmapFactory.decodeStream(is, null, decodeOptions);
                // If necessary, scale down to the maximal acceptable size.
                if (tempBitmap != null
                        && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
                    bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                    tempBitmap.recycle();
                } else {
                    bitmap = tempBitmap;
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int degree = getBitmapDegree(imageFile.getPath());
        if (bitmap != null && degree > 0) {
            bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        return bitmap;
    }

    public static Bitmap getBitmap(InputStream imageFileStream, int maxWidth, int maxHeight, Config decodeConfig) {
        if (imageFileStream == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPurgeable = true;
        decodeOptions.inInputShareable = true;
        decodeOptions.inPreferredConfig = decodeConfig;
        try {
            if (maxWidth == 0 && maxHeight == 0) {
                bitmap = BitmapFactory.decodeStream(imageFileStream, null, decodeOptions);
            } else {
                decodeOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(imageFileStream, null, decodeOptions);
                int actualWidth = decodeOptions.outWidth;
                int actualHeight = decodeOptions.outHeight;
                if (actualWidth > actualHeight && maxWidth < maxHeight) {
                    int temp = maxWidth;
                    maxWidth = maxHeight;
                    maxHeight = temp;
                }
                // Then compute the dimensions we would ideally like to
                // decode to.
                int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
                int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);
                // Decode to the nearest power of two scaling factor.
                decodeOptions.inJustDecodeBounds = false;
                decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
                Log.i("(actualWidth,actualHeight)=(" + actualWidth + "," + actualHeight
                        + ") -> (desiredWidth,desiredHeight)=(" + desiredWidth + "," + desiredHeight
                        + ")\nbestRatio = " + decodeOptions.inSampleSize);
                Bitmap tempBitmap = BitmapFactory.decodeStream(imageFileStream, null, decodeOptions);
                // If necessary, scale down to the maximal acceptable size.
                if (tempBitmap != null
                        && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
                    bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                    tempBitmap.recycle();
                } else {
                    bitmap = tempBitmap;
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageFileStream != null) {
                try {
                    imageFileStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int degree = getBitmapDegree(imageFileStream);
        if (bitmap != null && degree > 0) {
            bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        return bitmap;
    }

    public static Bitmap getBitmap(Resources resources, @RawRes int resId, int maxWidth, int maxHeight, Config decodeConfig) {
        if (resources == null) {
            return null;
        }
        return getBitmap(resources.openRawResource(resId), maxWidth, maxHeight, decodeConfig);
    }

    public static BitmapFactory.Options getBitmapOptions(String imageFilePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, options);
//		int outHeight=options.outHeight;
//		int outWidth= options.outWidth;
        return options;
    }

    public static BitmapFactory.Options getBitmapOptions(Resources resources, @DrawableRes int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, opts);
//		int width=opts.outWidth;
//		int height=opts.outHeight;
        return opts;
    }

    public static byte[] getBitmapByte(String imageFile, int quality) {
        return getBitmapByte(new File(imageFile), quality);
    }

    /**
     * 把bitmap转换成byte
     *
     * @param imageFile
     * @return
     */
    public static byte[] getBitmapByte(File imageFile, int quality) {
        // 先转成bitmap再得到byte[]，byte[]大小会是原来三倍左右
        // Bitmap bitmap = getBitmap(imageFile);
        // return getBitmapByte(bitmap, quality);
        byte[] buffer = null;
        InputStream fin = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fin = new FileInputStream(imageFile);
            final byte[] tmp = new byte[4096];
            int len = 0;
            while ((len = fin.read(tmp)) != -1) {
                bos.write(tmp, 0, len);
            }
            bos.close();
            buffer = bos.toByteArray();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    public static byte[] getBitmapByte(Bitmap bitmap, int quality) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        // 没用的图片，告诉系统回收
        bitmap.recycle();
        bitmap = null;
        return b;
    }

    public static void saveBitmap(Bitmap bitmap, String filePath) {
        saveBitmap(bitmap, new File(filePath));
    }

    public static void saveBitmap(Bitmap bitmap, File imageFile) {
        if (bitmap != null) {
            FileOutputStream fos = null;
            try {
                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }
                fos = new FileOutputStream(imageFile);
                bitmap.compress(CompressFormat.JPEG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Bitmap addBorder(Bitmap bitmap, float borderRadius) {
        return addBorder(bitmap, borderRadius, 0, 0);
    }

    public static Bitmap addBorder(Bitmap bitmap, float borderRadius, float borderWidth, int borderColor) {
        if (bitmap != null) {
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Bitmap borderBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(borderBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            final RectF rectF = new RectF(rect);
            canvas.drawRoundRect(rectF, borderRadius, borderRadius, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            bitmap.recycle();
            if (borderWidth > 0) {
                paint.setXfermode(null);
                paint.setColor(borderColor);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(borderWidth);
                rectF.left = borderWidth / 2;
                rectF.top = rectF.left;
                rectF.right = rect.width() - rectF.left;
                rectF.bottom = rect.height() - rectF.top;
                canvas.drawRoundRect(rectF, borderRadius, borderRadius, paint);
            }
            return borderBitmap;
        }
        return null;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for
     *                        max width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to
     *                        maintain aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    private static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}