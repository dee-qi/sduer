package com.example.sduhelper.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.sduhelper.LoginActivity;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_AUTH_DENIED;
import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_OK;
import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_USER_CANCEL;

/**
 * This is sduer
 * Created by qidi on 2017/7/26.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
    private static final String TAG = "WXEntryActivity";

    private IWXAPI api = WXAPIFactory.createWXAPI(this, ApiUtil.getApi(WXEntryActivity.this,"APP_ID"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode){
            case ERR_OK:
                LoginActivity.login_state = LoginActivity.WX_BOUND_SUCCEED;
                if(SharedPreferenceUtil.get(this,"userInfo","wx_code").equals("")){
                    SendAuth.Resp resp = (SendAuth.Resp)baseResp;
                    SharedPreferenceUtil.save(this,"userInfo","wx_code",resp.code);
                    Log.d("wx_code", "onResp: wx_code is "+resp.code);
                    Log.d("wx_code", "onResp: wx_code hash is "+resp.code.hashCode()+2);
                }
                SmartToast.make(this,"微信授权成功");
                Information.isOnTrial = false;
                finish();
                break;
            case ERR_AUTH_DENIED:
                LoginActivity.login_state = LoginActivity.WX_BOUND_FAILED;
                SmartToast.make(this,"微信授权被拒绝！");
                finish();
                break;
            case ERR_USER_CANCEL:
                LoginActivity.login_state = LoginActivity.WX_BOUND_FAILED;
                SmartToast.make(this,"微信授权被取消！");
                finish();
                break;
            default:
                LoginActivity.login_state = LoginActivity.WX_BOUND_FAILED;
                SmartToast.make(this,"发送返回！");
                Log.d(TAG, "onResp: "+baseResp.errStr);
                finish();
                break;
        }
        Log.d(TAG, "onResp: "+baseResp.errStr);
    }
}
