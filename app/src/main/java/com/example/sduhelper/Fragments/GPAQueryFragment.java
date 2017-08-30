package com.example.sduhelper.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GPAQueryFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "GPAQueryFragment";

    private TextView totalGPA;
    private ListView listView;
    private ImageView help;
    private View view;

    private List<String> mList;
    private String total;

    private final int LOAD_FAILED = 0x111;
    private final int LOAD_SUCCEED = 0x112;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == LOAD_SUCCEED){
                totalGPA.setText(total);
                ArrayAdapter adapter = new ArrayAdapter(view.getContext(),android.R.layout.simple_list_item_1,mList);
                listView.setAdapter(adapter);
            } else if(msg.what == LOAD_FAILED){
                SmartToast.make(getContext(),"获取绩点失败！");
            }
        }
    };
    public GPAQueryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gpaquery, container, false);

        totalGPA = (TextView)view.findViewById(R.id.score_total_gpa);
        listView = (ListView)view.findViewById(R.id.score_gpa_list);
        help = (ImageView)view.findViewById(R.id.score_gpa_help);

        help.setOnClickListener(this);
        loadData();

        return view;
    }

    //加载各学期成绩，和总绩点！
    private void loadData(){
        mList = new LinkedList<>();
        String url = ApiUtil.getApi(getContext(),"api_academic_credits")+SharedPreferenceUtil.get(getContext(),"userInfo","id");
        String token = SharedPreferenceUtil.get(getContext(),"userInfo","token");
        NetWorkUtil.get(url, token, new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                String s = response.body().string();

                try {
                    JSONObject j = new JSONObject(s);
                    if(j.getInt("code") == 0) {
                        msg.what = LOAD_SUCCEED;
                        JSONObject obj = j.getJSONObject("obj");
                        total = obj.getString("总绩点");
                        Iterator<String> iterator = obj.keys();
                        while(iterator.hasNext()){
                            String date = iterator.next();
                            String cre = obj.getString(date);
                            mList.add(date+"   :   "+cre);
                        }
                        mList.remove(mList.size()-1);
                    } else {
                        msg.what = LOAD_FAILED;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(msg);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle("说明")
                .setMessage("1.关于格式：\n" +
                        "直接举个例子吧：2017-2018-1代表2017到2018学年第一学期\n" +
                        "2.关于绩点计算：" +
                        "绩点根据山大的官方绩点计算方法计算，每当一科出成绩的时候就会把这一科" +
                        "算进总绩点里，所以在出成绩的那段时间总绩点可能会不断变化。建议等某学" +
                        "期的成绩出全了再查看该学期的绩点。" )
                .setCancelable(false)
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //啥也不用写
                    }
                })
                .show();
    }
}
