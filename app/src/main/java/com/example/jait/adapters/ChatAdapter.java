package com.example.jait.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jait.R;
import com.example.jait.models.Message;
import com.example.jait.utils.SCUtils;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    private ArrayList<Message> data;
    private Context mContext;

    public ChatAdapter(Context context, ArrayList<Message> data) {
        this.data = data;
        this.mContext = context;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main, null);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Message message = data.get(i);
        String formatted_date = SCUtils.formatted_date(message.getTimestamp());
        if (message.isNotification()) {
            myViewHolder.textView_message.setText(Html.fromHtml("<small><i><font color=\"#FFBB33\">" + " " + message.getMessage() + "</font></i></small>"));
        }else {
            myViewHolder.textView_message.setText(
                    Html.fromHtml("<font color=\"#403835\">&#x3C;" + message.getUsername() + "&#x3E;</font>" + " " + message.getMessage() + " <font color=\"#999999\">"));
            myViewHolder.textView_timestamp.setText(formatted_date);
        }
    }

    @Override public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView_message;
        protected TextView textView_timestamp;
        public MyViewHolder(View view) {
            super(view);
            this.textView_message = (TextView) view.findViewById(R.id.textView_message);
            this.textView_timestamp = (TextView) view.findViewById(R.id.text_timestamp);
        }
    }
}

