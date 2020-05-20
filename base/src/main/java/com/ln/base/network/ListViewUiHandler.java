package com.ln.base.network;

public interface ListViewUiHandler extends RequestUiHandler {
    void onRspSuccess(PageRspInterface<?> rsp, int pageNum, int pageSize);
}
