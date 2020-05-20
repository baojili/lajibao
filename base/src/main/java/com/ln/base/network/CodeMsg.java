package com.ln.base.network;

import com.ln.base.AppBase;
import com.ln.base.R;

/**
 * 定义统一的码信息
 */

public enum CodeMsg {
    //与服务对应的码信息
    OK(0, "ok"),
    UNAUTHORIZED(401, "未获得认证授权"),
    FORBIDDEN(403, "无权限禁止访问"),
    SERVER_ERROR(500, "服务器内部错误(500)"),
    OTHER(1000, "其他错误"),
    //仅供本地使用码信息
    NETWORK_UNAVAILABLE(-101, AppBase.getBaseInstance().getString(R.string.network_unavailable_pls_check)),
    NETWORK_ANOMALY(-102, AppBase.getBaseInstance().getString(R.string.network_anomaly_try_later)),
    PARSE_FAILURE(-103, AppBase.getBaseInstance().getString(R.string.parse_failure_try_later)),
    RESPONSE_CHECK_ANOMALY(-104, AppBase.getBaseInstance().getString(R.string.response_check_anomaly_try_later)),
    SERVER_ANOMALY(-105, AppBase.getBaseInstance().getString(R.string.server_anomaly_try_later)),
    NETWORK_UNSTABLE(-106, AppBase.getBaseInstance().getString(R.string.network_unstable_pls_check)),
    AUTH_FAILURE(-107, AppBase.getBaseInstance().getString(R.string.auth_failure)),
    REQUEST_ANOMALY(-108, AppBase.getBaseInstance().getString(R.string.request_anomaly_try_later));

    public Integer code;
    public String msg;

    CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
