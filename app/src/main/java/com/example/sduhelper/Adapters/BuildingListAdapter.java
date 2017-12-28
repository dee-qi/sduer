package com.example.sduhelper.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sduhelper.Activities.StudyRoomResultActivity;
import com.example.sduhelper.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is sduer
 * Created by qidi on 2017/10/13.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class BuildingListAdapter extends RecyclerView.Adapter<BuildingListAdapter.ViewHolder>{
    private String campus = "";
    private String[] buildings = null;

    public BuildingListAdapter(String campus,String[] buildings){
        this.campus = campus;
        this.buildings = buildings;
    }

    public void setCampus(String campus,String[] buildings){
        this.campus = campus;
        this.buildings = buildings;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView buildingName;
        View item;
        public ViewHolder(View itemView) {
            super(itemView);
            buildingName = (TextView)itemView.findViewById(R.id.item_studyroom_name);
            this.item = itemView;
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_studyroom_building,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.buildingName.setText(buildings[position]);
        Log.d("@holder", "onBindViewHolder: "+buildings[position]);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.item.getContext(), StudyRoomResultActivity.class);
                intent.putExtra("campus",campus);
                intent.putExtra("building",buildings[position]);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildings.length;
    }
}
