package com.ln.base.network;

import com.ln.base.config.Config;
import com.ln.base.model.JsonEntity;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.Md5Encrypt;

import java.util.HashMap;
import java.util.Map;

public class Base extends JsonEntity {
    //"签名，签名秘钥由服务器事先给到终端"
    private String sign;
    //"时间戳"
    private Long timestamp;
    //操作系统
    private String operation;
    //客户端版本
    private String version;

    public Base() {
        operation = Config.OS_TYPE;
        version = AndroidUtils.getSystemVersion();

        Long timestampValue = System.currentTimeMillis();
        Map<String, String> temp = new HashMap<>();
        temp.put("timestamp", String.valueOf(timestampValue));
        String signValue = Md5Encrypt.md5(Md5Encrypt.getContent(temp));
        setTimestamp(timestampValue);
        setSign(signValue);
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
