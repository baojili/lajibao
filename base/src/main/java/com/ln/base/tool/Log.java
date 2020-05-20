package com.ln.base.tool;

import androidx.annotation.Nullable;

public class Log {

    private static boolean isDisable = false;
    private static boolean isDebug = false;
    private static String globalTag = "ln";
    private static int priority = android.util.Log.INFO;

    public static void disableLog(boolean isDisable) {
        Log.isDisable = isDisable;
    }

    /**
     * @param globalTag Used to identify the source of a log message.
     * @param priority  The message level you would like logged. the value MUST BE {@link android.util.Log#VERBOSE} , {@link android.util.Log#DEBUG} , {@link android.util.Log#INFO} , {@link android.util.Log#ERROR} , {@link android.util.Log#WARN} or {@link android.util.Log#ASSERT}
     * @param isDebug   true,will be log more detail info.
     **/
    public static void isLoggable(@Nullable String globalTag, int priority, boolean isDebug) {
        Log.globalTag = globalTag;
        Log.priority = priority;
        Log.isDebug = isDebug;
    }


    public static void v(String msg) {
        log(android.util.Log.VERBOSE, msg, null);
    }

    public static void v(String msg, Throwable tr) {
        log(android.util.Log.VERBOSE, msg, tr);
    }

    public static void d(String msg) {
        log(android.util.Log.DEBUG, msg, null);
    }

    public static void d(String msg, Throwable tr) {
        log(android.util.Log.DEBUG, msg, tr);
    }

    public static void i(String msg) {
        log(android.util.Log.INFO, msg, null);
    }

    public static void i(String msg, Throwable tr) {
        log(android.util.Log.INFO, msg, tr);
    }

    public static void e(String msg) {
        log(android.util.Log.ERROR, msg, null);
    }

    public static void e(String msg, Throwable tr) {
        log(android.util.Log.ERROR, msg, tr);
    }

    public static void w(String msg) {
        log(android.util.Log.WARN, msg, null);
    }

    public static void w(String msg, Throwable tr) {
        log(android.util.Log.WARN, msg, tr);
    }

    public static void wtf(String msg) {
        log(android.util.Log.ASSERT, msg, null);
    }

    public static void wtf(String msg, Throwable tr) {
        log(android.util.Log.ASSERT, msg, tr);
    }

    /**
     * The default level of any tag is set to INFO(Android system).so VERBOSE and DEBUG will be log more detail messages than other
     */
    private static void log(int priority, String msg, Throwable tr) {
        if (isDisable || priority < Log.priority || !StringUtils.isValid(msg)) return;
        switch (priority) {
            case android.util.Log.VERBOSE:
                logDetail(android.util.Log.VERBOSE, msg, tr);
                break;
            case android.util.Log.DEBUG:
                logDetail(android.util.Log.DEBUG, msg, tr);
                break;
            case android.util.Log.INFO:
                if (isDebug) {
                    logDetail(android.util.Log.INFO, msg, tr);
                } else {
                    android.util.Log.i(globalTag, msg, tr);
                }
                break;
            case android.util.Log.WARN:
                if (isDebug) {
                    logDetail(android.util.Log.WARN, msg, tr);
                } else {
                    android.util.Log.w(globalTag, msg, tr);
                }
                break;
            case android.util.Log.ERROR:
                if (isDebug) {
                    logDetail(android.util.Log.ERROR, msg, tr);
                } else {
                    android.util.Log.e(globalTag, msg, tr);
                }
                break;
            case android.util.Log.ASSERT:
                if (isDebug) {
                    logDetail(android.util.Log.ASSERT, msg, tr);
                } else {
                    android.util.Log.wtf(globalTag, msg, tr);
                }
                break;
            default:
                break;
        }
    }

    private static void logDetail(int priority, String msg, Throwable tr) {
        if (!StringUtils.isValid(msg)) return;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        if (stacksLen > 4) {
            StringBuffer tag = new StringBuffer(globalTag);
            tag.append("-").append(stacks[3].getFileName().split("\\.")[0]);
            StringBuffer extendMsg = new StringBuffer(msg);
            extendMsg.append(" LINE:").append(stacks[3].getLineNumber());
            switch (priority) {
                case android.util.Log.VERBOSE:
                    android.util.Log.v(tag.toString(), extendMsg.toString(), tr);
                    break;
                case android.util.Log.DEBUG:
                    android.util.Log.d(tag.toString(), extendMsg.toString(), tr);
                    break;
                case android.util.Log.INFO:
                    android.util.Log.i(tag.toString(), extendMsg.toString(), tr);
                    break;
                case android.util.Log.WARN:
                    android.util.Log.w(tag.toString(), extendMsg.toString(), tr);
                    break;
                case android.util.Log.ERROR:
                    android.util.Log.e(tag.toString(), extendMsg.toString(), tr);
                    break;
                case android.util.Log.ASSERT:
                    android.util.Log.wtf(tag.toString(), extendMsg.toString(), tr);
                    break;
                default:
                    break;
            }
        } else {
            switch (priority) {
                case android.util.Log.VERBOSE:
                    android.util.Log.v(globalTag, msg, tr);
                    break;
                case android.util.Log.DEBUG:
                    android.util.Log.d(globalTag, msg, tr);
                    break;
                case android.util.Log.INFO:
                    android.util.Log.i(globalTag, msg, tr);
                    break;
                case android.util.Log.WARN:
                    android.util.Log.w(globalTag, msg, tr);
                    break;
                case android.util.Log.ERROR:
                    android.util.Log.e(globalTag, msg, tr);
                    break;
                case android.util.Log.ASSERT:
                    android.util.Log.wtf(globalTag, msg, tr);
                    break;
                default:
                    break;
            }
        }
    }
}