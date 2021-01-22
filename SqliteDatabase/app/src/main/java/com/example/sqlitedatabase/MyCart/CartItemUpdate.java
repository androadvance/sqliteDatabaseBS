package com.example.sqlitedatabase.MyCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.MainActivity;
import com.example.sqlitedatabase.NonScrollListView;
import com.example.sqlitedatabase.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartItemUpdate extends AppCompatActivity {

    TextView tv_itemname,tv_styleno,tv_Qty,tv_productamount,tv_pcsperbundle,tv_pcsperbox;
    Toolbar toolbar;
    ImageView img_product;
    NonScrollListView lv_listview;
    Context context;
    String str_styleno,str_pcsperbox,str_imgpath;
    Button btn_update;
    JSONArray jsonarray;
    public static List<U_CartItemList> mOrderlist;
    public U_CartItemAdapter adapter;
    String catGroup,catName,itemname,qty,amount,pcsperbundle;
    public boolean loopproceed = true;
    int total;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_item_update);

        context = getApplicationContext();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Update Order");
        setSupportActionBar(toolbar);
        tv_itemname = findViewById(R.id.tv_itemname);
        img_product = findViewById(R.id.img_product);
        lv_listview = findViewById(R.id.lv_listview);
        btn_update = findViewById(R.id.btn_update);

        tv_styleno = findViewById(R.id.tv_styleno);
        tv_pcsperbundle = findViewById(R.id.tv_PCSPerBundle);
        tv_pcsperbox = findViewById(R.id.tv_pcsperbox);
        tv_Qty = findViewById(R.id.tv_Qty);
        tv_productamount = findViewById(R.id.tv_productamount);

        mOrderlist = new ArrayList<>();

        Intent intent = getIntent();
        str_styleno = intent.getStringExtra("styleno");
        str_pcsperbox = intent.getStringExtra("PCSPerBox");
        str_imgpath = intent.getStringExtra("ImgSrc");

        catGroup = intent.getStringExtra("catGroup");
        catName = intent.getStringExtra("catName");
        itemname = intent.getStringExtra("itemname");
        qty = intent.getStringExtra("qty");
        amount = intent.getStringExtra("amount");
        pcsperbundle = intent.getStringExtra("pcsperbundle");

        tv_styleno.setText(str_styleno+"_"+itemname+"_"+catGroup+"_"+catName);
        tv_pcsperbox.setText("Pack Type - "+str_pcsperbox);
        tv_Qty.setText("Total Qty in Bundle - "+qty);
        tv_productamount.setText("Total Amount- "+amount);
        tv_pcsperbundle.setText("Total PackPer Bundle - "+pcsperbundle);

        Picasso.get().load("http://bennyhillsindia.com/" + str_imgpath)
                .into(img_product);

        String[] s_pcpperbox = str_pcsperbox.split(" ");
        String s_ppb = s_pcpperbox[0];

        s_getsizewiseRate(str_styleno,s_ppb, MainActivity.BuyerCode);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0 ; i < mOrderlist.size(); i++){

                    U_CartItemList cartList = mOrderlist.get(i);
                    String size = cartList.getSize();
                    String qty = cartList.getQty();

                    if (qty.isEmpty() || qty.equalsIgnoreCase("0")){
                        Toast.makeText(context, "qty should not empty", Toast.LENGTH_SHORT).show();
                    } else {
                        int i_qty = Integer.parseInt(qty);
                        int i_pcsperbundle = Integer.parseInt(pcsperbundle);

                        total = i_qty * i_pcsperbundle;

                        update_sizePlaceOrder(String.valueOf(total),str_styleno,size,s_ppb,MainActivity.BuyerCode);
                    }
                }
            }
        });
    }

    public void s_getsizewiseRate(String Styleno,String pcsperbox,String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/s_getsizewiseRate?styleno="+Styleno+"&pcsperbox="+pcsperbox+"&buyercode="+buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsonarray = jsonObject.getJSONArray("s_getporeqdtl");

                                if (jsonarray.length() == 0) {
                                    new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Product not found").show();
                                } else {

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsonarray.get(i).toString());
                                        String size = jsonobj.getString("size");
                                        String qty = jsonobj.getString("qty");
                                        String rate = jsonobj.getString("rate");
                                        String Amount = jsonobj.getString("Amount");

                                        mOrderlist.add(new U_CartItemList(size,qty,rate,Amount));

                                    }

                                    adapter = new U_CartItemAdapter(getApplicationContext(),mOrderlist);
                                    lv_listview.setAdapter(adapter);
                                }

                            } catch (Exception er) {
                                er.printStackTrace();
                            }


                        } else {
                            Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, error -> {
            error.printStackTrace();
            NetworkResponse response = error.networkResponse;

            if (error.networkResponse.statusCode == 500) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsonarray = jsonObject.getJSONArray("s_getporeqdtl");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void update_sizePlaceOrder(String qty,String styleno,String size,String pcsperbox,String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "localhost/OrderEntry/u_SizePlaceOrder?qty="+qty+"&styleno="+styleno+"&pcsperbox="+pcsperbox+"&size="+size+"&buyercode="+buyercode;

        final SweetAlertDialog alert = (new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    alert.dismissWithAnimation();
                    if (response != null) {

                        if (loopproceed) {
                            if (response.contains("success")) {

                                Intent intent = new Intent(CartItemUpdate.this, CartItemActivity.class);
                                Toast.makeText(context, "Qty updated successfully", Toast.LENGTH_SHORT).show();
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            loopproceed = false;
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
                    new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            }  else
                new SweetAlertDialog(CartItemUpdate.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }
}