package com.ln.base.tool;

import android.text.format.Time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;
    public static final long DIS_INTERVAL = MINUTE_IN_MILLIS * 5;
    /**
     * This constant is actually the length of 364 days, not of a year!
     */
    public static final long YEAR_IN_MILLIS = WEEK_IN_MILLIS * 52;

    private static final String[] WEEKS =
            {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private static final Time time = new Time();

    /**
     * @param when
     * @param format 时间格式
     * @return 返回format定义的格式
     */
    public static String format(Timestamp when, String format) {
        return format(when, format);
    }

    /**
     * @param when
     * @param format 时间格式
     * @return 返回format定义的格式
     */
    public static String format(long when, String format) {
        time.set(when);
        return time.format(format);
    }

    /**
     * @return 返回格式 %H:%M 默认当前时间
     */
    public static String formatTime() {
        return formatTime(System.currentTimeMillis());
    }

    /**
     * @param when
     * @return 返回格式 %H:%M
     */
    public static String formatTime(long when) {
        time.set(when);
        return time.format("%H:%M");
    }

    /**
     * @param when
     * @return 返回格式 %H:%M
     */
    public static String formatTime(Timestamp when) {
        return formatTime(when.getTime());
    }

    /**
     * @return 返回格式%H:%M:%S
     */
    public static String formatHMS(long when) {
        time.set(when);
        return time.format("%H:%M:%S");
    }

    /**
     * @return 返回格式%H:%M:%S
     */
    public static String formatHMS(Timestamp when) {
        return formatYMD(when.getTime());
    }

    /**
     * @return 返回格式 %Y/%m
     */
    public static String formatYM(long when) {
        time.set(when);
        return time.format("%Y/%m");
    }

    /**
     * @return 返回格式返 %Y/%m
     */
    public static String formatYM(Timestamp when) {
        return formatYMDT(when.getTime());
    }

    /**
     * @return 返回格式 %m/%d
     */
    public static String formatMD(long when) {
        time.set(when);
        return time.format("%m/%d");
    }

    /**
     * @return 返回格式 %m/%d
     */
    public static String formatMD(Timestamp when) {

        return formatMD(when.getTime());
    }

    /**
     * @return 返回格式 %Y/%m/%d
     */
    public static String formatMDHM(long when) {
        time.set(when);
        return time.format("%m/%d") + " " + formatTime(when);
    }


    /**
     * @return 返回格式 %Y/%m/%d
     */
    public static String formatYMD(long when) {
        time.set(when);
        return time.format("%Y/%m/%d");
    }

    /**
     * @return 返回格式 %Y-%m-%d
     */
    public static String formatYMD_(long when) {
        time.set(when);
        return time.format("%Y-%m-%d");
    }

    /**
     * @return 返回格式 %Y/%m/%d
     */
    public static String formatYMD(Timestamp when) {
        return formatYMD(when.getTime());
    }

    /**
     * @return 返回格式 %Y/%m/%d %H:%M
     */
    public static String formatYMDT(long when) {
        time.set(when);
        return time.format("%Y/%m/%d %H:%M");
    }

    /**
     * @return 返回格式 %Y/%m/%d %H:%M
     */
    public static String formatYMDT(Timestamp when) {
        return formatYMDT(when.getTime());
    }

    /**
     * @return 返回格式 %Y年%m月
     */
    public static String formatZYM(long when) {
        time.set(when);
        return time.format("%Y年%m月");
    }

    /**
     * @return 返回格式 %Y年
     */
    public static String formatZY(long when) {
        time.set(when);
        return time.format("%Y年");
    }

    /**
     * @return 返回格式 %Y年%m月
     */
    public static String formatZYM(Timestamp when) {
        return formatZYM(when.getTime());
    }

    /**
     * @return 返回格式 %Y年%m月%d日
     */
    public static String formatZYMD(long when) {
        time.set(when);
        return time.format("%Y年%m月%d日");
    }

    /**
     * @return 返回格式 %Y年%m月%d日
     */
    public static String formatZYMD(Timestamp when) {
        return formatZYMD(when.getTime());
    }

    /**
     * @return 返回格式 %d日
     */
    public static String formatZD(long when) {
        time.set(when);
        return time.format("%d日");
    }

    /**
     * @return 返回格式 %d日
     */
    public static String formatZD(Timestamp when) {
        return formatZD(when.getTime());
    }

    /**
     * @return 返回格式 %m月
     */
    public static String formatZM(long when) {
        time.set(when);
        return time.format("%m月");
    }

    /**
     * @return 返回格式 %m月
     */
    public static String formatZM(Timestamp when) {
        return formatZM(when.getTime());
    }


    /**
     * @return 返回格式 %m月%d日
     */
    public static String formatZMD(long when) {
        time.set(when);
        return time.format("%m月%d日");
    }

    /**
     * @return 返回格式 %m月%d日
     */
    public static String formatZMD(Timestamp when) {
        return formatZMD(when.getTime());
    }

    /**
     * @return 返回格式 %m月%d日 %H:%M
     */
    public static String formatZMDT(long when) {
        time.set(when);
        return time.format("%m月%d日   %H:%M");
    }

    /**
     * @return 返回格式 %m月%d日 %H:%M
     */
    public static String formatZMDT(Timestamp when) {
        return formatZMDT(when.getTime());
    }

    /**
     * @return 返回格式 MM月dd日 a HH:mm
     */
    public static String formatZMDAT(long when) {
        return new SimpleDateFormat("MM月dd日 a HH:mm ").format(new Date(when));
    }

    /**
     * @return 返回格式 MM月dd日 a HH:mm
     */
    public static String formatZMDAT(Timestamp when) {
        return new SimpleDateFormat("MM月dd日 a HH:mm ").format(new Date(when.getTime()));
    }

    /**
     * @return 返回格式%Y年 %m月%d日 %H:%M
     */
    public static String formatZYMDT(long when) {
        time.set(when);
        return time.format("%Y年%m月%d日   %H:%M");
    }

    /**
     * @return 返回格式%Y年 %m月%d日 %H:%M
     */
    public static String formatZYMDT(Timestamp when) {
        return formatZYMDT(when.getTime());
    }

    /**
     * @return 返回格式 %m月%d日 星期*
     */
    public static String formatZMDW(long when) {
        return formatZMD(when) + " " + WEEKS[time.weekDay];
    }

    /**
     * @return 返回格式 %Y年%m月%d日 星期* %H:%M
     */
    public static String formatZYMDWT(long when) {
        return formatZYMD(when) + " " + WEEKS[time.weekDay] + " " + formatTime(when);
    }

    /**
     * @return 返回格式 %Y年%m月%d日 星期*
     */
    public static String formatZYMDW(long when) {
        return formatZYMD(when) + " " + WEEKS[time.weekDay];
    }

    /**
     * @return 返回格式 将yyyy年MM月dd日 转为yyyy/MM/dd
     */
    public static String formatYMD2YMD(String YMD) {
        try {
            SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy年MM月dd日");

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            YMD = format.format(formatYMD.parse(YMD));
            return YMD;
        } catch (Exception e) {
            e.printStackTrace();
            return YMD;
        }
    }

    /**
     * @return 返回格式 将yyyy-MM-dd 转为MM-dd,去掉年份
     */
    public static String formatYMD2MD(String YMD) {
        try {
            SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat("MM-dd");
            YMD = format.format(formatYMD.parse(YMD));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return YMD;
    }

    /**
     * @return 返回格式 将yyyy-MM-dd 转为yyyy/MM/dd
     */
    public static String formatYMD2YMD(String YMD, String formatString) {
        try {
            SimpleDateFormat formatYMD = new SimpleDateFormat(formatString);

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            YMD = format.format(formatYMD.parse(YMD));
            return YMD;
        } catch (Exception e) {
            e.printStackTrace();
            return YMD;
        }
    }

    /**
     * @return 返回格式 %Y/%m/%d
     */
    public static String formatZMDHM(long when) {
        time.set(when);
        return time.format("%m/%d %H:%M");
    }

    /**
     * @return 返回格式 %m.%d
     */
    public static String formatZMDD(long when) {
        time.set(when);
        return time.format("%m.%d");
    }

    public static String formatDateSmartZD(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return "今天";
            } else if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            } else {
                return formatZD(when);
            }
        } else {
            return formatZD(when);
        }
    }

    public static String formatDateSmart(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return "今天";
            } else if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            } else {
                return formatZMD(when);
            }
        } else {
            return formatZYMD(when);
        }
    }

    public static String formatDateSmart2Chat(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return "今天";
            } else if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            } else {
                return formatZMD(when);
            }
        } else {
            return formatZMD(when);
        }
    }

    public static String formatDateSmartYMDOrMD(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            return formatMD(when);
        } else {
            return formatYMD(when);
        }
    }

    public static String formatDateSmartYMD(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return "今天";
            } else if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            } else {
                return formatZYMD(when);
            }
        } else {
            return formatZYMD(when);
        }
    }

    public static String formatDateSmart(Timestamp when) {
        return formatDateSmart(when.getTime());
    }

    public static String formatWeekNow() {
        return WEEKS[time.weekDay];
    }

    public static String formatWeek(long when) {
        time.set(when);
        return WEEKS[time.weekDay];
    }

    public static int getWeekNow() {
        return time.weekDay;
    }

    /**
     * 获取上一天或者下一天的
     *
     * @param when 今天
     * @param day  相隔几天
     * @return %Y/%m/%d 返回时间格式
     */
    public static String getUpOrNextDay(long when, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(when));
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH)
                + day);
        return formatYMD(calendar.getTimeInMillis());
    }

    /**
     * 获取上一天或者下一天的毫秒值
     *
     * @param when 今天
     * @param day  相隔几天
     * @return 毫秒值
     */
    public static long getUpOrNextDayLong(long when, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(when));
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH)
                + day);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取上一月或者下一月的显示内容
     *
     * @param when  这个月
     * @param month 相隔几月
     * @return %Y/%m 返回时间格式
     */
    public static String getUpOrNextMonth(long when, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(when));
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)) + month,
                calendar.get(Calendar.DAY_OF_MONTH));
        calendar.getTimeInMillis();
        return formatYM(calendar.getTimeInMillis());
    }

    /**
     * 获取上一月或者下一月的第一天毫秒值
     *
     * @param when  这个月
     * @param month 相隔几月
     * @return 毫秒值
     */
    public static long getUpOrNextMonthLongFirst(long when, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(when));
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)) + month,
                calendar.get(Calendar.DAY_OF_MONTH));
        calendar.getTimeInMillis();
        return calendar.getTimeInMillis();
    }

    /**
     * 获取上一周或者下一周的显示区间
     *
     * @param time 现在的时间
     * @param week 相隔几周
     * @return %Y/%m/%d-%Y/%m/%d
     */
    public static String getUpOrNextWeek(long time, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH)
                + week * 7);
        int offset;
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            offset = 6;
        } else {
            offset = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        }
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH)
                - offset);
        long monday = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)),
                calendar.get(Calendar.DAY_OF_MONTH) + 6);
        long sumday = calendar.getTimeInMillis();
        if (sumday > Calendar.getInstance().getTimeInMillis()) {
            return formatYMD(monday) + "-今天";
        } else {
            return formatYMD(monday) + "-" + formatYMD(sumday);
        }
    }

    /**
     * 获取上一周或者下一周的毫秒值区间
     *
     * @param time 现在的时间
     * @param week 相隔几周
     * @return 起始时间和结束时间
     */
    public static List<Long> getUpOrNextWeekLong(long time, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH)
                + week * 7);
        int offset;
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            offset = 6;
        } else {
            offset = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        }
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH)
                - offset);
        long monday = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)),
                calendar.get(Calendar.DAY_OF_MONTH) + 6);
        long sumday = calendar.getTimeInMillis();
        List<Long> oneWeek = new ArrayList<Long>();
        oneWeek.add(monday);
        if (sumday > Calendar.getInstance().getTimeInMillis()) {
            oneWeek.add(Calendar.getInstance().getTimeInMillis());
        } else {
            oneWeek.add(sumday);
        }

        return oneWeek;
    }

    /**
     * 获取上一月或者下一月的毫秒值区间
     *
     * @param time 现在的时间
     * @return 起始时间和结束时间
     */
    public static List<Long> getUpOrNextMonthLong(long time, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
        System.out.println(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long firstDay = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long lastDay = calendar.getTimeInMillis();
        List<Long> oneMonth = new ArrayList<Long>();
        oneMonth.add(firstDay);
        if (lastDay > Calendar.getInstance().getTimeInMillis()) {
            oneMonth.add(Calendar.getInstance().getTimeInMillis());
        } else {
            oneMonth.add(lastDay);
        }
        return oneMonth;
    }

    /**
     * 根据时间字符串返回对应的毫秒值
     *
     * @param birthday 时间字符串
     * @param format   格式
     * @return 毫秒值
     */
    public static long getLongFromString(String birthday, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(birthday);

            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 判断当前毫秒值是否属于未来
     *
     * @param when 时间毫秒值
     * @return
     */
    public static boolean isTomorrow(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            return days < 0;
        } else return calendar.get(Calendar.YEAR) > now.get(Calendar.YEAR);
    }

    public static boolean isDay(long millis) {
        time.set(millis);
        return Integer.parseInt(time.format("%H")) > 7 && Integer.parseInt(time.format("%H")) < 20;
    }

    public static String formatCommonDate(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return formatTime(when);
            } else if (days == 1) {
                return "昨天 " + formatTime(when);
            } else if (days == 2) {
                return "前天 " + formatTime(when);
            } else {
                return formatMD(when) + " " + formatTime(when);
            }
        } else {
            return formatYMD(when) + " " + formatTime(when);
        }
    }

    public static String formatCommonZDate(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return formatTime(when);
            } else if (days == 1) {
                return "昨天 " + formatTime(when);
            } else if (days == 2) {
                return "前天 " + formatTime(when);
            } else {
                return formatZMD(when) + " " + formatTime(when);
            }
        } else {
            return formatZYMD(when) + " " + formatTime(when);
        }
    }

    public static String formatCommonSimpleDate(long when) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            int days = now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            if (days == 0) {
                return formatTime(when);
            } else if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            } else {
                return formatZMD(when);
            }
        } else {
            return formatZY(when);
        }
    }

    public static String formatTime2AHM(long when) {
        return new SimpleDateFormat("a hh:mm").format(when);
    }

    public static long getHalfHour(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date newDate = null;
        try {

            String timeString = dateFormat.format(new Date(time));
            int m = Integer.parseInt(timeString.split(" ")[1].split(":")[1]);

            if (m < 30) {
                if (Math.abs(30 - m) < m) {
                    newDate = new Date(dateFormat.parse(timeString).getTime() + (30 - m) * 60 * 1000);

                } else {
                    newDate = new Date(dateFormat.parse(timeString).getTime() - m * 60 * 1000);
                }
            } else {
                if (Math.abs(60 - m) < m - 30) {
                    newDate = new Date(dateFormat.parse(timeString).getTime() + (60 - m) * 60 * 1000);
                } else {
                    newDate = new Date(dateFormat.parse(timeString).getTime() - (m - 30) * 60 * 1000);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate.getTime();
    }

    public static boolean isLongInterval(long current, long last) {
        return (current - last) > DIS_INTERVAL;
    }

}