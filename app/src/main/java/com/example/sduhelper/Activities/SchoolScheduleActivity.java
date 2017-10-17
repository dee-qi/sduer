package com.example.sduhelper.Activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SchoolScheduleActivity extends BaseActivity {
    private static final String TAG = "SchoolScheduleActivity";
    private String[] name = new String[]{"教工上班","老腊肉注册","小鲜肉报到","新生军训","老肉上课","小鲜肉上课",
            "校庆日","考试周","寒假"};
    private String[] time = new String[]{"2017/09/07","2017/09/08","2017/09/09","从2017/09/10到2017/09/30",
            "2017/09/11","2017/10/09","2017/10/15","从2018/01/15到2018/01/28","从2018/01/29到2018/03/04"};

    private ListView listView;
    private CalendarView calendarView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_schedule);

        initToolbar("校历");

        calendarView = (CalendarView)findViewById(R.id.sch_schedule_calender);
        scrollView = (ScrollView)findViewById(R.id.sch_schedule_scrollView);

        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.item_sch_schedule
        ,new String[]{"name","time"},new int[]{R.id.sch_schedule_name,R.id.sch_schedule_time});
        listView = (ListView)findViewById(R.id.sch_schedule_list);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnItems(listView);
        scrollView.smoothScrollTo(0,0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String time = ((TextView)view.findViewById(R.id.sch_schedule_time)).
                        getText().toString();
                if(time.contains("从")){
                    time = time.substring(1,11);
                }
                String[] parts = time.split("/");

                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                Log.d(TAG, "onItemClick: month is"+month);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DATE,day);

                calendarView.setDate(calendar.getTimeInMillis());
                scrollView.smoothScrollTo(0,0);
            }
        });
    }

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new LinkedList<>();
        for(int i=0; i<name.length; i++){
            Map<String, Object> item = new HashMap<>();
            item.put("name", name[i]);
            item.put("time", time[i]);
            list.add(item);
        }
        return list;
    }

    private void setListViewHeightBasedOnItems(ListView listView){
        ListAdapter adapter = listView.getAdapter();
        if(adapter != null){
            int total = adapter.getCount();
            View v = adapter.getView(0,null,listView);
            v.measure(0,View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = total*v.getMeasuredHeight();
            listView.setLayoutParams(params);
        }
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
