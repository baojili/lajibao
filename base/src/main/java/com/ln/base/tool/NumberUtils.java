package com.ln.base.tool;

public class NumberUtils {
    //byte 数组与 int 的相互转换
    public static int byteArrayToInt(byte[] b) {
        int result;
        if (b.length != 3 && b.length != 4)
            throw new IllegalArgumentException("this b.length must be 3 or 4");
        if (b.length == 4)
            result = b[3] & 0xFF |
                    (b[2] & 0xFF) << 8 |
                    (b[1] & 0xFF) << 16 |
                    (b[0] & 0xFF) << 24;
        else result = (byte) 0 & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
        return result;
    }

    public static int byteArrayToIntLE(byte[] b) {
        int result;
        if (b.length != 3 && b.length != 4)
            throw new IllegalArgumentException("this b.length must be 3 or 4");
        if (b.length == 4)
            result = b[0] & 0xFF << 24 |
                    (b[1] & 0xFF) << 16 |
                    (b[2] & 0xFF) << 8 |
                    (b[3] & 0xFF);
        else result = (byte) 0 & 0xFF << 24 |
                (b[0] & 0xFF) << 16 |
                (b[1] & 0xFF) << 8 |
                (b[2] & 0xFF);
        return result;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToByteArrayLE(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * @param number 短整型
     * @return 两位的字节数组
     * @功能 短整型与字节的转换
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    /**
     * @param b 两位的字节数组
     * @return 短整型
     * @功能 字节的转换与短整型
     */
    public static short byteToShort(byte[] b) {
        if (b.length != 2)
            throw new IllegalArgumentException("this b.length must be 2");
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }
}
