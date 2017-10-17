package com.example.sduhelper.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sduhelper.R;

import java.util.LinkedList;
import java.util.List;

/**
 * This is sduer
 * Created by qidi on 2017/10/14.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class RoomResultAdapter extends RecyclerView.Adapter<RoomResultAdapter.ViewHolder> {

    List<String> dataList;

    public RoomResultAdapter(List<String> list){dataList = list;}

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView roomName;
        private TextView status1;
        private TextView status2;
        private TextView status3;
        private TextView status4;
        private TextView status5;
        private TextView status6;
        List<TextView> statusList;

        public ViewHolder(View itemView) {
            super(itemView);
            statusList = new LinkedList<>();
            roomName = (TextView)itemView.findViewById(R.id.item_studyroom_result_room);
            status1 = (TextView)itemView.findViewById(R.id.item_studyroom_result_1);
            status2 = (TextView)itemView.findViewById(R.id.item_studyroom_result_2);
            status3 = (TextView)itemView.findViewById(R.id.item_studyroom_result_3);
            status4 = (TextView)itemView.findViewById(R.id.item_studyroom_result_4);
            status5 = (TextView)itemView.findViewById(R.id.item_studyroom_result_5);
            status6 = (TextView)itemView.findViewById(R.id.item_studyroom_result_6);
            statusList.add(status1);
            statusList.add(status2);
            statusList.add(status3);
            statusList.add(status4);
            statusList.add(status5);
            statusList.add(status6);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_studyroom_result,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = dataList.get(position);
        String statusInfo = data.substring(0,6);
        String roomName = data.substring(6);
        Log.d("@room", "onBindViewHolder: "+statusInfo);
        holder.roomName.setText(roomName);
        for(int i=0; i<statusInfo.length(); i++){
            if(statusInfo.charAt(i) == '0'){
                holder.statusList.get(i).setText("闲");
                holder.statusList.get(i).setTextColor(holder.roomName.getResources().getColor(R.color.text_default));
            } else if(statusInfo.charAt(i) == '1'){
                holder.statusList.get(i).setText("课");
                holder.statusList.get(i).setTextColor(holder.roomName.getResources().getColor(R.color.themeColor));
            } else {
                holder.statusList.get(i).setText("借");
                holder.statusList.get(i).setTextColor(holder.roomName.getResources().getColor(R.color.themeColor));
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
