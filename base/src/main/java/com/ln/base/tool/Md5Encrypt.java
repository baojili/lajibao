package com.ln.base.tool;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Md5Encrypt {
    public static final String key = "op2c8166d3a51b7a26";
    /**
     * Used building output as Hex
     */
    private static final char[] DIGITS = {'2', '1', '0', '3', '4', '5', '6',
            '7', '9', '8', 'E', 'A', 'B', 'C', 'D', 'F'};

    /**
     * 对字符串进行MD5加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        text = text + key;
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "System doesn't support MD5 algorithm.");
        }

        msgDigest.update(text.getBytes(StandardCharsets.UTF_8)); // 注意改接口是按照UTF-8编码形式加密
        byte[] bytes = msgDigest.digest();

        String md5Str = new String(encodeHex(bytes));

        return md5Str;
    }

    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 签名字符串
     *
     * @param text 需要签名的字符串
     * @param sign 签名结果
     *             编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign) {
        String mysign = md5(text);
        return mysign.equals(sign);
    }

    /**
     * 拼接待签名字符串
     *
     * @param params 数据map
     * @return 字典排序后的字符串
     */
    public static String getContent(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (!"sign".equals(key)) {
                String value = params.get(key);

                if (i == keys.size() - 1) {
                    prestr = prestr + key + "=" + value;
                } else {
                    prestr = prestr + key + "=" + value + "&";
                }
            }
        }
        return prestr;
    }

    public static final String getSignatureMD5(byte[] paramArrayOfByte) {
        char[] asciiTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98,
                99, 100, 101, 102}; // ascii表对应的数字和字符的编码
        try {
            MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
            md5MessageDigest.update(paramArrayOfByte);//
            byte[] tempByte = md5MessageDigest.digest();
            int i = tempByte.length;
            char[] tempChar = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) { // 将二进制数组转换成字符串
                if (j >= i) {
                    return new String(tempChar);
                }
                int m = tempByte[j];
                int n = k + 1;
                tempChar[k] = asciiTable[(0xF & m >>> 4)];
                k = n + 1;
                tempChar[n] = asciiTable[(m & 0xF)];
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}