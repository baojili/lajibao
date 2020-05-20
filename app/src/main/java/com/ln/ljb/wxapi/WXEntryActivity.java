package com.ln.ljb.wxapi;

import android.content.Intent;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ln.ljb.activity.BaseActivity;
import com.ln.ljb.constant.Config;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    public static final String WE_CHAT_CODE = "wechat_code";
    public static final String WE_CHAT_OK_RSP = "wechat_ok_rsp";
    public static final String WE_CHAT_ERROR_RSP = "wechat_error_rsp";
    public static final String WE_CHAT_CANCEL_RSP = "wechat_cancel_rsp";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Config.WE_CHAT_APP_ID, true);
        api.registerApp(Config.WE_CHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
        //TODO
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX == resp.getType()) {
            //handleShareResp(resp);
        } else if (ConstantsAPI.COMMAND_SENDAUTH == resp.getType()) {
            handleLoginResp(resp);
        }
    }

    private void handleShareResp(BaseResp resp) {
        /*int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string._share_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string._share_canceled;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string._send_deny;
                break;
            default:
                result = R.string._send_error_unknown;
                break;
        }
        getToastDialog().showToast(result);
        finish();*/
    }

    private void handleLoginResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Intent intent = new Intent(WE_CHAT_OK_RSP);
                Bundle bundle = new Bundle();
                SendAuth.Resp sendAuthResp = (SendAuth.Resp) resp;
                bundle.putString(WE_CHAT_CODE, sendAuthResp.code);
                intent.putExtras(bundle);
                LocalBroadcastManager.getInstance(WXEntryActivity.this).sendBroadcast(intent);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Intent cancelIntent = new Intent(WE_CHAT_CANCEL_RSP);
                LocalBroadcastManager.getInstance(WXEntryActivity.this).sendBroadcast(cancelIntent);
                break;
            default:
                Intent errorIntent = new Intent(WE_CHAT_ERROR_RSP);
                LocalBroadcastManager.getInstance(WXEntryActivity.this).sendBroadcast(errorIntent);
                break;
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}