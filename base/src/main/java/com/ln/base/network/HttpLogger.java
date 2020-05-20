package com.ln.base.network;

import com.ln.base.tool.JsonUtils;
import com.orhanobut.logger.Logger;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    ThreadLocal<StringBuffer> xMessage = new ThreadLocal<>();

    @Override
    public void log(String message) {
        // 请求或者响应开始
        if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
            xMessage.set(new StringBuffer());
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtils.toJsonViewStr(message);
        }
        xMessage.get().append(message.concat("\n"));
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            Logger.d(xMessage.get().toString());
            xMessage.remove();
        }
    }
}