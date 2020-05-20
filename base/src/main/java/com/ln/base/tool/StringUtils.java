package com.ln.base.tool;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final char UNDERLINE = '_';

    public static String removeBlanks(String content) {
        return content.trim().replaceAll("\n( *\n)+", "\n");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isValid(String str) {
        return str != null && !isEmpty(str.trim());
    }

    public static String hideMobile(String mobile) {
        if (!isValid(mobile)) {
            return "";
        }
        if (mobile.length() == 11) {
            return mobile.substring(0, 3) + "****" + mobile.substring(7);
        }
        return mobile;
    }

    public static String hideIDCode(String idCode) {
        if (!isValid(idCode)) {
            return "";
        }
        if (idCode.length() > 10) {
            return idCode.substring(0, 6) + "****" + idCode.substring(idCode.length() - 4);
        }
        return idCode;
    }

    public static String insertBlank(String content, int subLength) {
        if (!isValid(content)) {
            return "";
        }
        content = content.replaceAll(" ", "");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < content.length()) {
            sb.append(content.charAt(i));
            i++;
            if (i % subLength == 0 && i < content.length()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String camelToUnderline(String param) {
        if (isValid(param) && param.matches(".*[A-Z].*")) {
            final int len = param.length();
            final StringBuilder sb = new StringBuilder();
            char c;
            for (int i = 0; i < len; i++) {
                c = param.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(UNDERLINE);
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return param;
    }

    public static String underlineToCamel(String param) {
        if (isValid(param) && param.matches(".*_.*")) {
            final int len = param.length();
            final StringBuilder sb = new StringBuilder();
            char c;
            for (int i = 0; i < len; i++) {
                c = param.charAt(i);
                if (c == UNDERLINE) {
                    if (++i < len) {
                        sb.append(Character.toUpperCase(param.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return param;
    }

    /**
     * 验证联系电话
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        if (!isValid(phone)) {
            return false;
        }
        return phone.length() == 11 && phone.startsWith("1");
    }

    public static boolean checkIp(String ipAddress) {
        if (!isValid(ipAddress)) return false;
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /**
     * 正则验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        if (!isValid(email)) {
            return false;
        }
        return email
                .matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    /**
     * 判断数字
     *
     * @param num
     * @return
     */
    public static boolean checkNum(String num) {
        if (!isValid(num)) {
            return false;
        }
        return num.matches("^[0-9_]+$");
    }

    /**
     * 判断Mac地址
     *
     * @param macAddr
     * @return
     */
    public static boolean checkMacAddr(String macAddr) {
        if (!isValid(macAddr)) {
            return false;
        }
        return macAddr
                .matches("^[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}$");
    }

    /**
     * 判断血压计识别码
     *
     * @param bpmCode
     * @return
     */
    public static boolean checkBpmCode(String bpmCode) {
        if (!isValid(bpmCode)) {
            return false;
        }
        if (!bpmCode.contains("/") && bpmCode
                .matches("^[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}$")) {
            return true;
        } else
            return bpmCode.contains("/") && bpmCode.substring(0, bpmCode.indexOf("/")).matches("^[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}$");
    }

    /**
     * 获取血压计识别码里的mac地址
     *
     * @param bpmCode
     * @return
     */
    public static String getMacAddrFromBpmCode(String bpmCode) {
        if (!checkBpmCode(bpmCode) || !BluetoothAdapter.checkBluetoothAddress(bpmCode)) {
            return "";
        }
        if (!bpmCode.contains("/")) {
            return bpmCode;
        } else {
            return bpmCode.substring(0, bpmCode.indexOf("/"));
        }
    }

    /**
     * 判断姓名
     *
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        // if
        // (name.matches("^[\u4E00-\u9FA5A-Za-z0-9_\\.\\-\\,\\.\\/\\;\\'\\*\\~\\!\\！\\@\\#\\$\\%\\^\\&\\(\\)\\（\\）\\、\\，\\。\\？\\?\\：\\“\\<\\>\\[\\]\\{\\}\\`\\·\\/\\-\\+]+$"))
        return isValid(name) && name.matches("^[\u4E00-\u9FA5A-Za-z0-9_\\p{P}]+$");
    }

    @SuppressLint("DefaultLocale")
    public static String tranNum2Readable(int num) {
        if (num < 10000) {
            return num + "";
        }

        if (num < 100000) {
            return String.format("%.1f万", (float) (num) / 10000);
        }
        return Math.round((float) (num) / 10000) + "万";
    }

    public static SpannableStringBuilder stringFormatForMoney(Context context, String money, int textSize) {
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(money);
        //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
        //size  为0 即采用原始的正常的 size大小
        spanBuilder.setSpan(new TextAppearanceSpan(null, 0, context.getResources().getDimensionPixelOffset(textSize), null, null), 0, money.split("\\.")[0].length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spanBuilder;
    }

    public static int countBit(String content) {
        if (!isValid(content)) {
            return 0;
        }
        char[] chars = content.toCharArray();
        int count = 0;
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        for (int i = 0; i < chars.length; i++) {
            if (pattern.matcher(chars[i] + "").matches()) {
                count += 2;
            } else {
                count++;
            }
        }
        return count;
    }

    /***
     * 格式化金额数据
     *
     * @param price 金额
     * @return
     */
    public static String formatPrice(float price) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(price);
    }

    /**
     * 检查是否符合十六进制表示格式
     */
    public static boolean isHexString(String hexString) {
        if (!isValid(hexString)) {
            return false;
        }
        return hexString
                .matches("^([0-9a-fA-F]{2})+$");
    }

    public static boolean isEqual(String leftStr, String rightStr) {
        if (TextUtils.isEmpty(leftStr)) {
            return TextUtils.isEmpty(rightStr);
        }
        return leftStr.equals(rightStr);
    }

    public static int toInt(String intString) {
        int result = 0;
        try {
            result = Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static long toLong(String longStr) {
        long result = 0;
        try {
            result = Long.parseLong(longStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatPhone(String phone) {
        if (checkPhone(phone)) {
            return phone.substring(0, 3) + " " + phone.substring(3, 7) + " " + phone.substring(7, 11);
        } else {
            return phone;
        }
    }

    public static boolean isEnglishAlphabet(String str) {

        Pattern p = Pattern.compile("^[A-Za-z]+$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static String bytesToHex(byte[] bytes, boolean upperCase) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        if (upperCase) {
            return md5str.toString().toUpperCase();
        }
        return md5str.toString().toLowerCase();
    }

    public static String byteToHex(byte digital, boolean upperCase) {
        StringBuffer md5str = new StringBuffer();
        if (digital < 0) {
            digital += 256;
        }
        if (digital < 16) {
            md5str.append("0");
        }
        md5str.append(Integer.toHexString(digital));
        if (upperCase) {
            return md5str.toString().toUpperCase();
        }
        return md5str.toString().toLowerCase();
    }

    /**
     * 对挺特定的 内容进行 md5 加密
     *
     * @param message   加密明文
     * @param upperCase 加密以后的字符串是是大写还是小写  true 大写  false 小写
     * @return
     */
    public static String getMD5(String message, boolean upperCase) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] input = message.getBytes();

            byte[] buff = md.digest(input);

            md5str = bytesToHex(buff, upperCase);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }
}