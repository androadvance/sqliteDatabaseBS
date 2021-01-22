package com.example.sqlitedatabase.Notification;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sqlitedatabase.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<CategoryList> arraylist;

    public CategoryAdapter(Context mContext, List<CategoryList> arraylist) {
        this.mContext = mContext;
        this.arraylist = arraylist;
    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.category_wise_listitems, null);

        TextView text1 = v.findViewById(R.id.categoryid);
        TextView text2 = v.findViewById(R.id.msgcontentid);
        TextView text3 = v.findViewById(R.id.timeid);
        TextView text4 = v.findViewById(R.id.messagecountid);
        LinearLayout linearLayout = v.findViewById(R.id.countcircleid);


        final CategoryList listItem = arraylist.get(position);

        text1.setText(arraylist.get(position).getCategory());
        text2.setText(arraylist.get(position).getMessage());
        text3.setText(arraylist.get(position).getTime());
        text4.setText(arraylist.get(position).getMessagecount());


        if (arraylist.get(position).getIsread().contains("false")){

            text1.setTypeface(text1.getTypeface(), Typeface.BOLD);
            text1.setTextColor(Color.parseColor("#000000"));
            text2.setTypeface(text2.getTypeface(), Typeface.BOLD);
            text2.setTextColor(Color.parseColor("#000000"));
            text3.setTypeface(text3.getTypeface(), Typeface.BOLD);
            text3.setTextColor(Color.parseColor("#000000"));
        }

        if (arraylist.get(position).getMessagecount().contains("0")){

            text4.setVisibility(View.GONE);

        } else {

            text4.setVisibility(View.VISIBLE);
            linearLayout.setBackgroundResource(R.drawable.setcircle);
        }

        return v;
    }
}
