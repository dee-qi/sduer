package com.example.sduhelper.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sduhelper.Items.ItemCurriculum;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.Information;

import java.util.Calendar;
import java.util.List;


/**
 * This is sduer
 * Created by qidi on 2017/7/24.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class CurriculumAdapter extends BaseAdapter {
    private static final String TAG = "@curri";
    private Context context;
    private List<ItemCurriculum> classList;
    TextView weekCount;

    public CurriculumAdapter(Context context, List<ItemCurriculum> classList,TextView weekCount){
        this.classList = classList;
        this.context = context;
        this.weekCount = weekCount;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    @Override
    public int getCount() {
        return classList.size();
    }

    @Override
    public ItemCurriculum getItem(int position) {
        return classList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_curriculum,null);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.curriculum_item);
        if(getItem(position).isClass()){

            //设置课程View的文字和字颜色
            textView.setText(getItem(position).getName()+"\n"+
                    "@"+getItem(position).getTeacher()+"\n"+
                    getItem(position).getLocation());
            textView.setTextColor(Color.WHITE);

            //获取当前周数
            int weekOfTerm = Information.getCurrentWeekCount();
            Log.d(TAG, "week of term is "+weekOfTerm);
            if(weekOfTerm != -1) {
                weekCount.setText("当前是第 " + weekOfTerm + " 周");
                //不在当前周的课程为灰色，其余为5种颜色随机
                Log.d(TAG, "item "+getItem(position).getName()+" is "+getItem(position).getWeekSequence());
                if (getItem(position).getWeekSequence().charAt(weekOfTerm - 1) == '0') {
                    textView.setText("");
//                    textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_0));
                    //do nothing
                } else {
                    switch ((int) (Math.random() * 5)) {
                        case 0:
                            textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_1));
                            break;
                        case 1:
                            textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_2));
                            break;
                        case 2:
                            textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_3));
                            break;
                        case 3:
                            textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_4));
                            break;
                        case 4:
                            textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_5));
                            break;
                        default:
                            break;
                    }
                }
            } else {
                weekCount.setText("当前是假期");
                textView.setBackground(context.getResources().getDrawable(R.drawable.curriculum_bg_0));
            }
        }
        return convertView;
    }
}
