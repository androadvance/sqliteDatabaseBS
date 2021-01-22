package com.example.sqlitedatabase.Order.OrderSelectionList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.sqlitedatabase.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends BaseAdapter {

    private List<OrderList> orderLists;
    private Context context;
    private ImageView img_photo;

    public OrderListAdapter(List<OrderList> orderLists, Context context) {
        this.orderLists = orderLists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderLists.size();
    }

    @Override
    public Object getItem(int position) {
        return orderLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.order_selection_listitem, null);

        img_photo = v.findViewById(R.id.img_photo);
        TextView tv_itemname = v.findViewById(R.id.tv_itemname);
        TextView tv_styleno = v.findViewById(R.id.tv_styleno);
        TextView tv_catname = v.findViewById(R.id.tv_catname);
        TextView tv_catgroup = v.findViewById(R.id.tv_catgroup);
        TextView tv_img = v.findViewById(R.id.tv_img);
        TextView tv_pcsperbox = v.findViewById(R.id.tv_pcsperbox);
        TextView tv_size = v.findViewById(R.id.tv_size);
        TextView tv_pcsperbundle = v.findViewById(R.id.tv_pcsperbundle);

        final OrderList ListItem = orderLists.get(position);

        tv_itemname.setText(orderLists.get(position).getItemname());
        tv_styleno.setText(orderLists.get(position).getStyleno());
        tv_catname.setText(orderLists.get(position).getCategoryname());
        tv_catgroup.setText(orderLists.get(position).getCategorygroup());
        tv_img.setText(orderLists.get(position).getImage());
        tv_pcsperbox.setText(orderLists.get(position).getPcsperbox());
        tv_size.setText(orderLists.get(position).getSizes());
        tv_pcsperbundle.setText(orderLists.get(position).getPcsperbundle());

        Picasso.get().load("http://bennyhillsindia.com/"+orderLists.get(position).getImage())
                .resize(250, 250)
                .into(img_photo);
        return v;
    }
}
