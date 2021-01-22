package com.example.sqlitedatabase.MyCart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlitedatabase.MainActivity;
import com.example.sqlitedatabase.Order.OrderSelectionList.OrderList;
import com.example.sqlitedatabase.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<CartList> cartLists;
    public CartItemActivity activity;
    JSONArray jsbundle;

    public CartAdapter(Context context, List<CartList> cartLists, Activity activity) {
        this.context = context;
        this.cartLists = cartLists;
        this.activity = (CartItemActivity)activity;
    }

    @Override
    public int getCount() {
        return cartLists.size();
    }

    @Override
    public Object getItem(int position) {
        return cartLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder")
        View v = View.inflate(context, R.layout.cart_list_items, null);

        TextView tv_productdesc = v.findViewById(R.id.tv_styleno);
        TextView tv_itemname = v.findViewById(R.id.tv_itemname);
        TextView tv_amount = v.findViewById(R.id.tv_amount);
        TextView tv_qty = v.findViewById(R.id.tv_qty);
        TextView tv_categoryname = v.findViewById(R.id.tv_categoryname);
        TextView tv_categorygroup = v.findViewById(R.id.tv_categorygroup);
        TextView tv_pcsperbox = v.findViewById(R.id.tv_pcsperbox);
        TextView tv_pcsperbundle = v.findViewById(R.id.tv_pcsperbundle);
        TextView tv_symbol = v.findViewById(R.id.tv_symbol);
        ImageView img_photo = v.findViewById(R.id.img_photo);
        Button btn_remove = v.findViewById(R.id.btn_remove);


        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CartList ListItem = cartLists.get(position);

                String styleno = ListItem.getStyleno();
                String pcsperbox = ListItem.getPcsperbox();

                String[] str_syleno = styleno.split("_");
                String s_styleno = str_syleno[0];

                String[] str_pcsperbox = pcsperbox.split("-");
                String s_pcsperbox = str_pcsperbox[1];

                String[] str_removespace = s_pcsperbox.split(" ");
                String s_ppb = str_removespace[1];

                d_removeProduct(s_styleno,s_ppb, MainActivity.BuyerCode);
                activity.GetAllCartProduct(MainActivity.BuyerCode);
                activity.s_getAllcartcount(MainActivity.BuyerCode);

            }
        });

        final CartList ListItem = cartLists.get(position);

        tv_productdesc.setText(cartLists.get(position).getStyleno());
        tv_itemname.setText(cartLists.get(position).getItemname());
        tv_amount.setText(cartLists.get(position).getAmount());
        tv_qty.setText(cartLists.get(position).getQty());
        tv_categoryname.setText(cartLists.get(position).getCategoryname());
        tv_categorygroup.setText(cartLists.get(position).getCategorygroup());
        tv_pcsperbox.setText(cartLists.get(position).getPcsperbox());
        tv_pcsperbundle.setText(cartLists.get(position).getPcsperbundle());
        tv_symbol.setText(cartLists.get(position).getRupee());
        btn_remove.setText(cartLists.get(position).getRemove());
      //  CartItemActivity.cartAdapter.notifyDataSetChanged();

        Picasso.get().load("http://bennyhillsindia.com/" + cartLists.get(position).getImage())
                .resize(250, 250)
                .into(img_photo);

        return v;

    }


    public void d_removeProduct(String styleno, String pcsperbox, String buyercode) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/d_removeCartProduct?styleno=" + styleno + "&pcsperbox=" + pcsperbox + "&buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    alert.dismissWithAnimation();
                    if (response != null) {
                        try {
                            jsbundle = new JSONArray(response);
                            if (response.contains("success")){
                                Toast.makeText(context, "Product item removed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            NetworkResponse response = error.networkResponse;

            if (error.networkResponse.statusCode == 500) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("s_getallCartProductDetail");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }
}
