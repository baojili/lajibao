package com.ln.base.tool;

/**
 * Created by JiangLei on 2016/5/18.
 */
public class Hex {

    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String encodeToHexStr(byte[] data) {
        return new String(encode(data));
    }

    public static String encodeToHexStr(byte data) {
        byte[] temp = new byte[1];
        temp[0] = data;
        return new String(encode(temp));
    }

    public static char[] encode(byte[] data) {
        return encode(data, true);
    }

    public static char[] encode(byte[] data, boolean toLowerCase) {
        return encode(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encode(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;
        for (int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }
        return out;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param hexStr 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStrToByteArray(String hexStr) {
        if (!StringUtils.isValid(hexStr))
            throw new IllegalArgumentException("this hexString must not be empty");
        int len = hexStr.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character
                    .digit(hexStr.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * 16进制表示的字符串转换为单字节
     *
     * @param hexStr 16进制表示的字符串，长度为2
     * @return byte 字节
     */
    public static byte hexStrToByte(String hexStr) {
        if (!StringUtils.isValid(hexStr))
            throw new IllegalArgumentException("this hexStr must not be empty");
        int len = hexStr.length();
        if (len != 2)
            throw new IllegalArgumentException("this hexStr.length() must be 2");
        // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
        byte b = (byte) ((Character.digit(hexStr.charAt(0), 16) << 4) + Character
                .digit(hexStr.charAt(1), 16));
        return b;
    }
}
