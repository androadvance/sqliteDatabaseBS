package com.example.sqlitedatabase.Order.FilterOption;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.sqlitedatabase.Helper.SQLiteLogin;
import com.example.sqlitedatabase.R;
import com.example.sqlitedatabase.SplashActivity;

import java.util.List;

public class FilterAdapter extends BaseAdapter {

    private Context mContext;
    private List<FilterList> mproductlist;
    SQLiteLogin db;

    public FilterAdapter(Context mContext, List<FilterList> mproductlist) {
        this.mContext = mContext;
        this.mproductlist = mproductlist;
    }


    @Override
    public int getCount() {
        return mproductlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mproductlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder")
        View v = View.inflate(mContext, R.layout.selectfilter_listitem, null);

        CheckBox checkBox = v.findViewById(R.id.checkBox);
        final FilterList ListItem = mproductlist.get(position);
        checkBox.setText(mproductlist.get(position).getSubtype());

        db = new SQLiteLogin(mContext);

        Cursor c1;
        if (FilterItems.selectedfiltertype.equalsIgnoreCase("group")) {
            c1 = db.fetchCheckedGroup(mproductlist.get(position).getSubtype());
            if (c1.getCount() > 0) {
                checkBox.setChecked(true);
            }
        } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("category")){
            c1 = db.fetchCheckedCatName(mproductlist.get(position).getSubtype());
            if (c1.getCount() > 0) {
                checkBox.setChecked(true);
            }
        } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("PCSPerBox")){
            c1 = db.fetchCheckedPcsPerBox(mproductlist.get(position).getSubtype());
            if (c1.getCount() > 0) {
                checkBox.setChecked(true);
            }
        } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("Size")){
            c1 = db.fetchCheckedSize(mproductlist.get(position).getSubtype());
            if (c1.getCount() > 0) {
                checkBox.setChecked(true);
            }
        } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("StyleNo & ItemName")){
            c1 = db.fetchCheckedStyleno(mproductlist.get(position).getSubtype());
            if (c1.getCount() > 0) {
                checkBox.setChecked(true);
            }
        }


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean checked = ((CheckBox) view).isChecked();
                // Check which checkbox was clicked
                if (view.getId() == R.id.checkBox) {
                    if (FilterItems.selectedfiltertype.equalsIgnoreCase("group")) {
                        if (checked)
                            db.insertGroup(mproductlist.get(position).getSubtype());
                        else
                            db.deleteGroup(mproductlist.get(position).getSubtype());
                    } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("category")) {
                        if (checked)
                            db.insertCat(mproductlist.get(position).getSubtype());
                        else
                            db.deleteCat(mproductlist.get(position).getSubtype());
                    } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("pcsperbox")) {
                        if (checked)
                            db.insertPcsPerBox(mproductlist.get(position).getSubtype());
                        else
                            db.deletePcsPerBox(mproductlist.get(position).getSubtype());
                    } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("styleno & itemname")) {
                        if (checked)
                            db.insertStyleNo(mproductlist.get(position).getSubtype());
                        else
                            db.deleteStyleNo(mproductlist.get(position).getSubtype());
                    } else if (FilterItems.selectedfiltertype.equalsIgnoreCase("size")) {
                        if (checked)
                            db.insertSize(mproductlist.get(position).getSubtype());
                        else
                            db.deleteSize(mproductlist.get(position).getSubtype());
                    }
                }
            }
        });

        return v;
    }
}
