package com.example.sam.anoumyouschat.adapter;

import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sam.anoumyouschat.ChatMain;
import com.example.sam.anoumyouschat.message.Message;
import com.example.sam.anoumyouschat.R;

import java.util.List;

/**
 * Created by Sam on 6/1/2017.
 */

public class MessageListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messagesItems;


    public MessageListAdapter(Context context, List<Message> messageList){
        this.context = context;
        this.messagesItems = messageList;
    }



    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Message getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        Message m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (messagesItems.get(position).isSelf()) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);

        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }
        if (messagesItems.get(position).getMessage().equals("leave")){
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_center,
                    null);
            TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

            txtMsg.setText("Đã thoát");
            lblFrom.setText(m.getFromName());
            return convertView;
        } if (messagesItems.get(position).getType().equals("image")) {
            if (messagesItems.get(position).isSelf())
            convertView = mInflater.inflate(R.layout.list_image_message_right,
                    null);

            if (!messagesItems.get(position).isSelf())
                convertView = mInflater.inflate(R.layout.list_image_message_left,
                        null);

            TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
            ImageView imgRight = (ImageView) convertView.findViewById(R.id.imgRight);

            imgRight.setImageBitmap(m.getBitmap());
            lblFrom.setText(m.getFromName());

            return convertView;
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        txtMsg.setText(m.getMessage());
        lblFrom.setText(m.getFromName());


        return convertView;
    }
}
