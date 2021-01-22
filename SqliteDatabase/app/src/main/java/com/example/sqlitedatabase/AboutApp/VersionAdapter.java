package com.example.sqlitedatabase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sqlitedatabase.R;

import java.util.List;

public class VersionAdapter extends BaseAdapter {

    private List<VersionList> arraylist;
    private Context context;

    public VersionAdapter(List<VersionList> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.versionlist, null);

        TextView tv_version = v.findViewById(R.id.tv_version);
        TextView tv_remarks = v.findViewById(R.id.tv_remarks);
        TextView tv_bulletpoint = v.findViewById(R.id.tv_bulletpoint);
        TextView tv_description = v.findViewById(R.id.tv_description);

        VersionList listItem = arraylist.get(position);

        tv_version.setText(arraylist.get(position).getVersion());
        tv_remarks.setText(arraylist.get(position).getRemarks());
        tv_bulletpoint.setText(arraylist.get(position).getBulletpoint());
        tv_description.setText(arraylist.get(position).getDescription());

        return v;
    }
}
