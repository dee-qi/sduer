package com.example.sduhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;
import com.example.sduhelper.wxapi.WXEntryActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    public final static int WX_BOUND_SUCCEED = 231;
    public final static int WX_BOUND_FAILED = -21;

    //微信登录有关
    IWXAPI api= WXAPIFactory.createWXAPI(this,ApiUtil.getApi(LoginActivity.this,"APP_ID"));
    public static int login_state = 0;//微信登录结果
    private ProgressDialog wx_progressDialog;

    private EditText loginStunum;
    private EditText loginPwd;
    private TextView tip;
    private Button login;
    boolean infoCompleted = false;
    private ProgressDialog login_progressDialog;

    private final int WX_LOGIN_SUCCEED = 0x123;
    private final int WX_LOGIN_FAILED = 0x124;
    private final int GET_INFO_SUCCEED = 0x125;
    private final int GET_INFO_FAILED = 0x126;
    private final int BIND_ACADEMIC_SUCCEED = 0x127;
    private final int BIND_ACADEMIC_FAILED = 0x128;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case WX_LOGIN_FAILED:
                    Log.d(TAG, "handleMessage: WX_LOGIN_FAILED");
                    login_progressDialog.dismiss();
                    SmartToast.make(LoginActivity.this,"获取微信端信息失败！");
                    break;
                case WX_LOGIN_SUCCEED:
                    Log.d(TAG, "handleMessage: WX_LOGIN_SUCCEED");
                    getUserInfo();
                    break;
                case BIND_ACADEMIC_FAILED:
                    Log.d(TAG, "handleMessage: BIND_ACA_FAILED");
                    login_progressDialog.dismiss();
                    SmartToast.make(LoginActivity.this,"绑定教务系统失败！");
                    break;
                case BIND_ACADEMIC_SUCCEED:
                    Log.d(TAG, "handleMessage: BIND_ACA_SUCC");
                    SharedPreferenceUtil.save(LoginActivity.this,"userInfo","isAcademicBound","true");
                    getUserInfo();
                    break;
                case GET_INFO_FAILED:
                    Log.d(TAG, "handleMessage: GET_INFO_FAILED");
                    login_progressDialog.dismiss();
                    SmartToast.make(LoginActivity.this,"获取用户信息失败！");
                    break;
                case GET_INFO_SUCCEED:
                    Log.d(TAG, "handleMessage: GET_INFO_SUCCEED");
                    login_progressDialog.dismiss();
                    Intent startIntent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(startIntent);
                    finishActivity();
            }
        }
    };

    private void finishActivity(){
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //是否存有用户信息
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        if(!sp.getString("stuNum","").equals("")){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        Information.isOnTrial = false;

        api.registerApp(ApiUtil.getApi(LoginActivity.this,"APP_ID"));

        //初始化EditText
        loginStunum = (EditText)findViewById(R.id.login_stunum);
        loginPwd = (EditText)findViewById(R.id.login_pwd);

        tip = (TextView)findViewById(R.id.login_tip);

        //初始化Button
        login = (Button)findViewById(R.id.login);


        if(SharedPreferenceUtil.get(this,"userInfo","wx_code").equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("需要微信授权才能继续使用！")
                    .setMessage("由于技术原因，本应用大部分功能必须绑定微信后才能正常使用。" +
                            "你可以先点击“随便看看”进行试用，试用版本仅“校车查询”、“资讯”、“校历”可正常使用，" +
                            "在试用结束后你仍然可以进行微信绑定以体验完整功能。")
                    .setCancelable(false)
                    .setPositiveButton("微信绑定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final SendAuth.Req req = new SendAuth.Req();
                            req.scope = "snsapi_userinfo";
                            req.state = "ddddddd";
                            api.sendReq(req);
                            wx_progressDialog = new ProgressDialog(LoginActivity.this);
                            wx_progressDialog.setMessage("正在唤起微信");
                            wx_progressDialog.setCancelable(false);
                            wx_progressDialog.show();
                        }
                    })
                    .setNegativeButton("随便看看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Information.isOnTrial = true;
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    })
                    .show();
        }


        //两个EditText都有文字时可以登录，按钮背景色改为sduRed
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!loginStunum.getText().toString().equals("")
                        && !loginPwd.getText().toString().equals("")){
                    login.setBackgroundColor(getResources().getColor(R.color.themeColor));
                    infoCompleted = true;
                } else {
                    login.setBackgroundColor(getResources().getColor(R.color.button_default));
                    infoCompleted = false;
                }
            }
        };
        loginStunum.addTextChangedListener(watcher);
        loginPwd.addTextChangedListener(watcher);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!NetWorkUtil.isNetWorkAvailable(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "无网络！请打开网络连接！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!infoCompleted){
                    Toast.makeText(LoginActivity.this, "请将登录信息填写完整！", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(loginStunum.getText().toString(),loginPwd.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(login_state == WX_BOUND_FAILED) {
            if(wx_progressDialog != null) {
                wx_progressDialog.dismiss();
            }
            new AlertDialog.Builder(this)
                    .setTitle("需要微信授权才能继续使用！")
                    .setMessage("微信绑定被拒绝了哎= =！")
                    .setCancelable(false)
                    .setPositiveButton("微信绑定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final SendAuth.Req req = new SendAuth.Req();
                            req.scope = "snsapi_userinfo";
                            req.state = "ddddddd";
                            api.sendReq(req);
                        }
                    })
                    .setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishALl();
                        }
                    })
                    .show();
        } else if(Information.isOnTrial){
            if(wx_progressDialog != null) {
                wx_progressDialog.dismiss();
            }
            new AlertDialog.Builder(this)
                    .setTitle("感觉如何？")
                    .setMessage("欢迎进行微信绑定以使用正式功能！")
                    .setCancelable(false)
                    .setPositiveButton("微信绑定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final SendAuth.Req req = new SendAuth.Req();
                            req.scope = "snsapi_userinfo";
                            req.state = "ddddddd";
                            api.sendReq(req);
                        }
                    })
                    .setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishALl();
                        }
                    })
                    .show();
        } else if(login_state == WX_BOUND_SUCCEED){
            if(wx_progressDialog != null) {
                wx_progressDialog.dismiss();
            }
            tip.setText("微信绑定成功啦~\n请输入学号密码进行登录\n一个微信号只能对应一个学号哟");
        }
    }




    private void login(String num, String pwd){
        //在这里写登陆逻辑
        login_progressDialog = new ProgressDialog(LoginActivity.this);
        login_progressDialog.setMessage("正在登录");
        login_progressDialog.setCancelable(false);
        login_progressDialog.show();
        String code = SharedPreferenceUtil.get(this,"userInfo","wx_code");
        String url = ApiUtil.getApi(LoginActivity.this,"api_oauth_app")+code+"/"+(code.hashCode()+2);
        Log.d(TAG, "login: "+url);
        NetWorkUtil.get(url, new Callback() {
            Message msg ;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = WX_LOGIN_FAILED;
                handler.sendMessage(msg);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "onResponse: loginResp:"+responseData);
                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONObject obj = json.getJSONObject("obj");
                    Information.api_token = obj.getString("token");
                    boolean lib = obj.getJSONObject("user").getBoolean("library");
                    boolean aca = obj.getJSONObject("user").getBoolean("academic");
                    boolean card = obj.getJSONObject("user").getBoolean("card");
                    int id = obj.getJSONObject("user").getInt("id");
                    SharedPreferenceUtil.save(LoginActivity.this,"userInfo","token",Information.api_token);
                    SharedPreferenceUtil.save(LoginActivity.this,"userInfo","isLibraryBound",""+lib);
                    SharedPreferenceUtil.save(LoginActivity.this,"userInfo","isAcademicBound",""+aca);
                    SharedPreferenceUtil.save(LoginActivity.this,"userInfo","isCardBound",""+card);
                    SharedPreferenceUtil.save(LoginActivity.this,"userInfo","id",""+id);
                } catch (JSONException e){
                    e.printStackTrace();
                }
                msg = new Message();
                msg.what = WX_LOGIN_SUCCEED;
                handler.sendMessage(msg);
            }
        });

    }

    private void bindAcademic(){
        String url = ApiUtil.getApi(LoginActivity.this,"api_academic_bind");
//                +"?id="+SharedPreferenceUtil.get(LoginActivity.this,"userInfo","id")+"&"
//                +"stuNo="+loginStunum.getText().toString()+"&"
//                +"password="+loginPwd.getText().toString();
        Log.d(TAG, "bindAcademic: url:"+url);
        OkHttpClient client = new OkHttpClient();
        Map map = new HashMap();
        map.put("id",SharedPreferenceUtil.get(LoginActivity.this,"userInfo","id"));
        map.put("stuNo",loginStunum.getText().toString());
        map.put("password",loginPwd.getText().toString());
        NetWorkUtil.post(url, map, SharedPreferenceUtil.get(LoginActivity.this, "userInfo", "token"), new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = BIND_ACADEMIC_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                String s = response.body().string();
                Log.d(TAG, "onResponse: bindAca:"+s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getInt("code") == 0) {
                        msg.what = BIND_ACADEMIC_SUCCEED;
                    } else {
                        msg.what = BIND_ACADEMIC_FAILED;
                    }
                } catch (JSONException e){
                    msg.what = BIND_ACADEMIC_FAILED;
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(msg);
                }

            }
        });
    }

    private void getUserInfo(){
        if(SharedPreferenceUtil.get(LoginActivity.this,"userInfo","isAcademicBound").equals("false")){
            bindAcademic();
        }
        String url = ApiUtil.getApi(LoginActivity.this,"api_academic_info")+SharedPreferenceUtil.get(LoginActivity.this,"userInfo","id");
        Log.d(TAG, "getUserInfo: url:"+url);
        NetWorkUtil.get(url, SharedPreferenceUtil.get(LoginActivity.this, "userInfo", "token"), new Callback() {
            Message msg ;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = GET_INFO_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                String s = response.body().string();
                Log.d(TAG, "onResponse: GetInfo:"+s);
                try {
                    if ((new JSONObject(s)).getInt("code") == 0) {
                        msg.what = GET_INFO_SUCCEED;
                        JSONArray array = new JSONObject(s).getJSONArray("obj");

                        JSONObject obj = new JSONObject(array.getString(0));
                        String name = obj.getString("name");
                        String stuNum = obj.getString("stuNum");
                        String major = obj.getString("major");
                        String college = obj.getString("college");
                        Log.d(TAG, "onResponse: userInfo:"+name+stuNum+major+college);
                        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("stuNum",stuNum);
                        editor.putString("name",name);
                        editor.putString("major",major);
                        editor.putString("college",college);
                        editor.commit();
                    } else{
                        msg.what = GET_INFO_FAILED;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    msg.what = GET_INFO_FAILED;
                } finally {
                    handler.sendMessage(msg);
                }
            }
        });
    }
}
