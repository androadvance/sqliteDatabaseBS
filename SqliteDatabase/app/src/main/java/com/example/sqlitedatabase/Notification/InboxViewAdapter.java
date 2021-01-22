package com.example.sqlitedatabase.Notification;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.sqlitedatabase.R;
import java.util.List;

public class InboxViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<InboxViewList> arraylist;

    public InboxViewAdapter(Context mContext, List<InboxViewList> arraylist) {
        this.mContext = mContext;
        this.arraylist = arraylist;
    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return  arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.message_view_listitems, null);
        TextView text1 = v.findViewById(R.id.dateid);
        TextView text2 = v.findViewById(R.id.msgcontent);
        TextView text3 = v.findViewById(R.id.timeid);

        final InboxViewList listItem = arraylist.get(position);

        text1.setText(arraylist.get(position).getDate());
        text2.setText(arraylist.get(position).getMsgcontent());
        text3.setText(arraylist.get(position).getTime());

        return v;
    }
}
