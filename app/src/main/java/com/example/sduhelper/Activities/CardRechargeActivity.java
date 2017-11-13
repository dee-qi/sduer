package com.example.sduhelper.Activities;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CardRechargeActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioButton recharge20;
    private RadioButton recharge50;
    private RadioButton recharge100;
    private RadioButton recharge200;
    private RadioButton rechargeOther;
    private EditText inputNum;
    private Button confirm;
    private final int UNCHECKED = 0x212;
    private int checkedRadioButton = UNCHECKED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_recharge);
        initToolbar("转账充值");

        recharge20 = (RadioButton)findViewById(R.id.card_recharge_20);
        recharge50 = (RadioButton)findViewById(R.id.card_recharge_50);
        recharge100 = (RadioButton)findViewById(R.id.card_recharge_100);
        recharge200 = (RadioButton)findViewById(R.id.card_recharge_200);
        rechargeOther = (RadioButton)findViewById(R.id.card_recharge_other);
        inputNum = (EditText)findViewById(R.id.card_recharge_input);
        confirm = (Button)findViewById(R.id.card_recharge_confirm);

        recharge20.setOnClickListener(this);
        recharge50.setOnClickListener(this);
        recharge100.setOnClickListener(this);
        recharge200.setOnClickListener(this);
        rechargeOther.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.card_recharge_confirm){
            if(checkedRadioButton == UNCHECKED){
                SmartToast.make(CardRechargeActivity.this,"请至少选择一个金额！");
            } else {
                String rechargeNum;
                switch (checkedRadioButton) {
                    case R.id.card_recharge_20:
                        rechargeNum = "20";
                        break;
                    case R.id.card_recharge_50:
                        rechargeNum = "50";
                        break;
                    case R.id.card_recharge_100:
                        rechargeNum = "100";
                        break;
                    case R.id.card_recharge_200:
                        rechargeNum = "200";
                        break;
                    case R.id.card_recharge_other:
                        rechargeNum = inputNum.getText().toString();
                        break;
                    default:
                        rechargeNum = "";
                }
                recharge(rechargeNum);
            }

        } else {
            unCheckedAll();
            switch (v.getId()){
                case R.id.card_recharge_20:
                    inputNum.setText("");
                    inputNum.setEnabled(false);
                    recharge20.setChecked(true);
                    checkedRadioButton = R.id.card_recharge_20;
                    break;
                case R.id.card_recharge_50:
                    inputNum.setText("");
                    inputNum.setEnabled(false);
                    recharge50.setChecked(true);
                    checkedRadioButton = R.id.card_recharge_50;
                    break;
                case R.id.card_recharge_100:
                    inputNum.setText("");
                    inputNum.setEnabled(false);
                    recharge100.setChecked(true);
                    checkedRadioButton = R.id.card_recharge_100;
                    break;
                case R.id.card_recharge_200:
                    inputNum.setText("");
                    inputNum.setEnabled(false);
                    recharge200.setChecked(true);
                    checkedRadioButton = R.id.card_recharge_200;
                    break;
                case R.id.card_recharge_other:
                    inputNum.setEnabled(true);
                    rechargeOther.setChecked(true);
                    checkedRadioButton = R.id.card_recharge_other;
                    break;
            }
        }
    }

    private ProgressDialog dia;
    private final int RECHARGE_FAILED = 0x889;
    private final int RECHARGE_SUCCEED = 0x888;
    private final int RECHARGE_AMOUNT_ERR = 0x123;
    private final int RECHARGE_BALANCE_ERR = 0x124;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dia.dismiss();
            if(msg.what == RECHARGE_SUCCEED){
                SmartToast.make(CardRechargeActivity.this,"充值成功！请重新进入校园卡服务查询过渡余额！");
            } else if(msg.what == RECHARGE_FAILED){
                new AlertDialog.Builder(CardRechargeActivity.this)
                        .setTitle("充值失败")
                        .setMessage("请检查网络或者在问题反馈中联系我们。")
                        .setNegativeButton("我知道了",null)
                        .show();
            } else if(msg.what == RECHARGE_BALANCE_ERR){
                new AlertDialog.Builder(CardRechargeActivity.this)
                        .setTitle("余额不足")
                        .setMessage("请确保与校园卡绑定的银行卡内有足够余额。")
                        .setNegativeButton("我知道了",null)
                        .show();
            } else if(msg.what == RECHARGE_AMOUNT_ERR){
                new AlertDialog.Builder(CardRechargeActivity.this)
                        .setTitle("金额错误")
                        .setMessage("充值金额必须大于0且不大于300。")
                        .setNegativeButton("我知道了",null)
                        .show();
            }
        }
    };
    private void recharge(String number){
        dia = new ProgressDialog(this);
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setMessage("正在充值");
        dia.setIndeterminate(true);
        dia.setCancelable(false);
        dia.show();
        String url = ApiUtil.getApi(CardRechargeActivity.this, "api_school_card_transfer");
        String token = SharedPreferenceUtil.get(CardRechargeActivity.this, "userInfo", "token");
        Map<String, String> map = new HashMap<>();
        map.put("amount", number);
        NetWorkUtil.post(url, map, token, new Callback() {
            Message msg;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = RECHARGE_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                String s = response.body().string();
                Log.d("@transfer", "onResponse: "+s);

                try{
                    JSONObject obj = new JSONObject(s);
                    String str = obj.getString("msg");
                    if(str.contains("成功")){
                        msg.what = RECHARGE_SUCCEED;
                    } else if(str.contains("限制")){
                        msg.what = RECHARGE_AMOUNT_ERR;
                    } else if(str.contains("余额")){
                        msg.what = RECHARGE_BALANCE_ERR;
                    } else msg.what = RECHARGE_FAILED;
                } catch (JSONException e){
                    Log.d("@tra", "onResponse: "+e.getMessage());
                    msg.what = RECHARGE_FAILED;
                } finally {
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void unCheckedAll(){
        recharge20.setChecked(false);
        recharge50.setChecked(false);
        recharge100.setChecked(false);
        recharge200.setChecked(false);
        rechargeOther.setChecked(false);
    }

    private void initToolbar(String title){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_36dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
