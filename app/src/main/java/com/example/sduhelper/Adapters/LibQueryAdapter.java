package com.example.sduhelper.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sduhelper.Items.LibQueryItem;
import com.example.sduhelper.R;

import java.util.List;

/**
 * This is sduer
 * Created by qidi on 2017/8/24.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class LibQueryAdapter extends ArrayAdapter {

    private List<LibQueryItem> mList;
    private Context context;
    private int resourceId;

    public LibQueryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.mList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_query,null);
        }
        TextView tv = (TextView)convertView.findViewById(R.id.library_query_name);
        tv.setText(((LibQueryItem)getItem(position)).getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View details = LayoutInflater.from(context).inflate(R.layout.library_query_details,null);
                TextView name = (TextView)details.findViewById(R.id.lib_details_name);
                TextView author = (TextView)details.findViewById(R.id.lib_details_author);
                TextView press = (TextView)details.findViewById(R.id.lib_details_press);
                TextView code = (TextView)details.findViewById(R.id.lib_details_code);
                ListView locationsList = (ListView)details.findViewById(R.id.lib_details_list);
                TextView none = (TextView)details.findViewById(R.id.lib_details_none);
                LibQueryItem item = (LibQueryItem)getItem(position);
                name.setText(item.getName());
                author.setText(item.getAuthor());
                press.setText(item.getPress());
                code.setText(item.getCode());
                List<String> locations = item.getLocations();
                ArrayAdapter adapter = new ArrayAdapter(context,R.layout.item_query_details_locations ,locations);
                locationsList.setAdapter(adapter);
                if(locations.size() == 0){
                    locationsList.setVisibility(View.GONE);
                    none.setVisibility(View.VISIBLE);
                }
                new AlertDialog.Builder(context)
                        .setView(details)
                        .setTitle("详情")
                        .show();
            }
        });
        return convertView;
    }
}
