package com.example.sduhelper.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;

import com.example.sduhelper.Activities.CurriculumActivity;
import com.example.sduhelper.Activities.ExamActivity;
import com.example.sduhelper.Activities.LibraryActivity;
import com.example.sduhelper.Activities.SchoolBusActivity;
import com.example.sduhelper.Activities.SchoolCardActivity;
import com.example.sduhelper.Activities.SchoolScheduleActivity;
import com.example.sduhelper.Activities.ScoreActivity;
import com.example.sduhelper.Activities.StudyRoomActivity;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.Information;
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

/**
 * This is UITest2
 * Created by qidi on 2017/7/16.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class FunctionsFragment extends Fragment implements View.OnClickListener{

    private CardView schoolCard;
    private CardView curriculum;
    private CardView studyRoom;
    private CardView schoolBus;
    private CardView exam;
    private CardView score;
    private CardView library;
    private CardView schoolSchedule;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_functions,container,false);

        schoolCard = (CardView)v.findViewById(R.id.func_schoolcard);
        curriculum = (CardView)v.findViewById(R.id.func_curriculum);
        studyRoom = (CardView)v.findViewById(R.id.func_studyroom);
        schoolBus = (CardView)v.findViewById(R.id.func_schoolbus);
        exam = (CardView)v.findViewById(R.id.func_exam);
        score = (CardView)v.findViewById(R.id.func_score);
        library = (CardView)v.findViewById(R.id.func_library);
        schoolSchedule = (CardView)v.findViewById(R.id.func_schoolschedule);

        schoolCard.setOnClickListener(this);
        curriculum.setOnClickListener(this);
        studyRoom.setOnClickListener(this);
        schoolBus.setOnClickListener(this);
        exam.setOnClickListener(this);
        score.setOnClickListener(this);
        library.setOnClickListener(this);
        schoolSchedule.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.func_schoolcard:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), SchoolCardActivity.class);
                startActivity(intent);
                break;
            case R.id.func_curriculum:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), CurriculumActivity.class);
                startActivity(intent);
                break;
            case R.id.func_studyroom:
                intent = new Intent(v.getContext(), StudyRoomActivity.class);
                startActivity(intent);
                //测试图书馆的绑定功能的代码
//                String url = ApiUtil.getApi(getContext(),"api_lib_bind");
//                Map<String,String> map = new HashMap<>();
//                map.put("id", SharedPreferenceUtil.get(getContext(),"userInfo","id"));
//                map.put("cardNo","111");
//                map.put("pass","222");
//                String token = SharedPreferenceUtil.get(getContext(),"userInfo","token");
//                NetWorkUtil.post(url, map, token, new Callback() {
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        Log.d("@bindLib", "onResponse: "+response.body().string());
//                    }
//                });
                break;
            case R.id.func_schoolbus:
                intent = new Intent(v.getContext(), SchoolBusActivity.class);
                startActivity(intent);
                break;
            case R.id.func_exam:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), ExamActivity.class);
                startActivity(intent);
                break;
            case R.id.func_score:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), ScoreActivity.class);
                startActivity(intent);
                break;
            case R.id.func_library:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), LibraryActivity.class);
                startActivity(intent);
                break;
            case R.id.func_schoolschedule:
                intent = new Intent(v.getContext(), SchoolScheduleActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
