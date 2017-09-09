package com.example.sduhelper.Adapters;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sduhelper.Items.BookBorrowedItem;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * This is sduer
 * Created by qidi on 2017/7/18.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class BookBorrowedAdapter extends RecyclerView.Adapter<BookBorrowedAdapter.ViewHolder> {


    private  List<BookBorrowedItem> mList;
    private View viewInViewHolder;


    static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView bookName;
        TextView startTime;
        TextView endTime;
        Button renew;
        View itemView;
        public ViewHolder(View v){
            super(v);
            this.bookName = (TextView)v.findViewById(R.id.library_borrowed_bookname);
            this.startTime = (TextView)v.findViewById(R.id.library_borrowed_start);
            this.endTime = (TextView)v.findViewById(R.id.library_borrowed_end);
            this.renew = (Button)v.findViewById(R.id.library_renew);
            this.itemView = v;
        }
    }

    private final int RENEW_SUCCEED = 0x123;
    private final int RENEW_FAILED = 0x124;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == RENEW_SUCCEED){
                String bookName = (String)msg.obj;
                SmartToast.make(viewInViewHolder.getContext(), bookName+"续借成功啦！重新进入图书馆功能可以看到新的应还日期！");
            } else if(msg.what == RENEW_FAILED){
                String feedBack = (String)msg.obj;
                if(feedBack.equals("")||feedBack == null){
                    SmartToast.make(viewInViewHolder.getContext(),"续借失败！请检查网络！");
                } else {
                    SmartToast.make(viewInViewHolder.getContext(),feedBack);
                }
            }
        }
    };

    public BookBorrowedAdapter(List<BookBorrowedItem> bookBorrowedList){mList = bookBorrowedList;}

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        viewInViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_borrowed,parent,false);
        final ViewHolder holder = new ViewHolder(viewInViewHolder);
        holder.renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final BookBorrowedItem bookBorrowedItem = mList.get(position);
                String url = ApiUtil.getApi(viewInViewHolder.getContext(),"api_lib_renew");
                String token = SharedPreferenceUtil.get(viewInViewHolder.getContext(),"userInfo","token");
                Map<String,String> map = new HashMap<>();
                map.put("id", SharedPreferenceUtil.get(viewInViewHolder.getContext(),"userInfo","id"));
                map.put("bookId",bookBorrowedItem.getId());
                map.put("verifyId",bookBorrowedItem.getCheckCode());
                NetWorkUtil.post(url, map, token, new Callback() {
                    Message msg;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        msg = new Message();
                        msg.what = RENEW_FAILED;
                        msg.obj = "";
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        msg = new Message();
                        String s = response.body().string();
                        Log.d("@bba", "onResponse: "+s);
                        try {
                            JSONObject o = new JSONObject(s);
                            if(o.getInt("code") == 0){
                                msg.what = RENEW_SUCCEED;
                                msg.obj = bookBorrowedItem.getBookName();
                            } else {
                                msg.what = RENEW_FAILED;
                                msg.obj = o.getString("msg");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        } finally {
                            handler.sendMessage(msg);
                        }

                    }
                });
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookBorrowedItem bookBorrowedItem = mList.get(position);
        holder.bookName.setText(bookBorrowedItem.getBookName());
        holder.startTime.setText("借出时间 " + bookBorrowedItem.getBorrowedTime());
        holder.endTime.setText("应还时间 " + bookBorrowedItem.getReturnTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
