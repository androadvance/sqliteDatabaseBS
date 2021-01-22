package com.example.sqlitedatabase.Order.OrderSelectionList;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlitedatabase.MainActivity;
import com.example.sqlitedatabase.MyCart.CartItemActivity;
import com.example.sqlitedatabase.NonScrollListView;
import com.example.sqlitedatabase.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ru.nikartm.support.ImageBadgeView;

public class OrderListItemDetailView extends AppCompatActivity {

    ImageView img_photo;
    ImageBadgeView img_Cart;
    TextView tv_styleno, tv_pcsperbox, tv_pcsperbundle, tv_size;
    Button btn_gotocart, btn_addcart;
    NonScrollListView lv_listview;
    String str_styleno, str_itemname, str_catname, str_catgroup, str_imgpath, str_size, str_pcsperbundle, str_pcsperbox, str_buyercode, str_retailercode;
    public static List<SizeList> sizeLists;
    public static SizeAdapter sizeAdapter;
    JSONArray jsbundle;
    public Context context;
    TextView tv_sizetitle, tv_qtytitle;
    String s_size, s_qty;
    public boolean loopproceed = true;
    Integer pendingmsgCount = 0;
    private String fullScreenInd;
    int total;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_item_detail_view);

        context = getApplicationContext();

        img_photo = findViewById(R.id.img_photo);
        tv_styleno = findViewById(R.id.tv_styleno);
        tv_pcsperbox = findViewById(R.id.tv_pcsperbox);
        tv_pcsperbundle = findViewById(R.id.tv_pcsperbundle);
        btn_gotocart = findViewById(R.id.btn_gotocart);
        btn_addcart = findViewById(R.id.btn_addcart);
        lv_listview = findViewById(R.id.lv_listview);
        tv_size = findViewById(R.id.tv_size);
        tv_sizetitle = findViewById(R.id.tv_sizetitle);
        tv_qtytitle = findViewById(R.id.tv_qtytitle);
        img_Cart = findViewById(R.id.img_Cart);

        Intent intent = getIntent();

        str_styleno = intent.getStringExtra("styleno");
        str_itemname = intent.getStringExtra("itemname");
        str_catname = intent.getStringExtra("catName");
        str_catgroup = intent.getStringExtra("catGroup");
        str_imgpath = intent.getStringExtra("imgSrc");
        str_size = intent.getStringExtra("sizes");
        str_pcsperbundle = intent.getStringExtra("pcsperbundle");
        str_pcsperbox = intent.getStringExtra("pcsperbox");
        str_retailercode = intent.getStringExtra("retailercode");

        sizeLists = new ArrayList<>();

        SpannableString content = new SpannableString("Size");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_sizetitle.setText(content);

        SpannableString content1 = new SpannableString("Bundle Qty");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        tv_qtytitle.setText(content1);

        s_getAllcartcount(MainActivity.BuyerCode);

        Picasso.get().load("https://bennyhillsindia.com/" + str_imgpath)
                .into(img_photo);

        tv_styleno.setText(str_styleno + " _ " + str_itemname + " _ " + str_pcsperbox + " PCS");
        tv_pcsperbundle.setText(str_pcsperbundle + " - PCB");

        getSizeListItem(str_styleno);

        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri myUri = Uri.parse("https://bennyhillsindia.com" + str_imgpath);
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setDataAndType(myUri, "image/png");
                startActivity(intent1);
            }
        });

        fullScreenInd = getIntent().getStringExtra("fullScreenIndicator");

        if ("y".equals(fullScreenInd)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Objects.requireNonNull(getSupportActionBar()).hide();

            img_photo.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            img_photo.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            img_photo.setAdjustViewBounds(false);
            img_photo.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        btn_gotocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pendingmsgCount == 0) {
                    Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(OrderListItemDetailView.this, CartItemActivity.class);
                    startActivity(intent1);
                }
            }
        });

        img_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pendingmsgCount == 0) {
                    Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(OrderListItemDetailView.this, CartItemActivity.class);
                    startActivity(intent1);
                }
            }
        });

        btn_addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (str_styleno.equalsIgnoreCase("BH/01215C")){
                    Toast.makeText(context, "Price not available.... Please Contact BillyHills Team", Toast.LENGTH_SHORT).show();
                } else {
                    int p = 0;
                    int q;
                    for (int i = 0; i < sizeLists.size(); i++) {

                        SizeList item = sizeLists.get(i);

                        if ((CharSequence) item.qty == "") {
                            q = 0;
                        } else {
                            q = 1;
                        }

                        if (q > 0) {
                            p = p + 1;
                        }
                    }
                    if (p == 0) {
                        Toast.makeText(context, "Qty should not empty", Toast.LENGTH_SHORT).show();
                    } else {
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(OrderListItemDetailView.this, R.style.BottomSheetDialogTheme);

                        View bottomsheetview = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.bottomdialog_layout, (LinearLayout) findViewById(R.id.bottomsheetcontainer));

                        TextView tv_productdetail = bottomsheetview.findViewById(R.id.tv_productdetail);
                        TextView tv_pcsperbundle = bottomsheetview.findViewById(R.id.tv_pcsperbundle);
                        ImageView img_product = bottomsheetview.findViewById(R.id.img_product);

                        tv_productdetail.setText(str_styleno + " _ " + str_itemname);
                        tv_pcsperbundle.setText(str_pcsperbundle + "Pcs Per Bundle");

                        Picasso.get().load("http://bennyhillsindia.com/" + str_imgpath)
                                .into(img_product);

                        bottomsheetview.findViewById(R.id.btn_addorder).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                for (int i = 0; i < sizeLists.size(); i++) {

                                    SizeList item = sizeLists.get(i);

                                    s_qty = item.qty;
                                    s_size = item.size;

                                    if (s_qty != null && !s_qty.isEmpty()) {
                                        try {
                                            int qty = Integer.parseInt(s_qty);
                                            int pcb = Integer.parseInt(str_pcsperbundle);

                                            total = qty * pcb;


                                            getAddOrderDetail(MainActivity.BuyerCode, str_styleno, s_size, String.valueOf(total), str_pcsperbox, str_retailercode);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                bottomSheetDialog.dismiss();
                            }
                        });

                        bottomsheetview.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetDialog.dismiss();
                            }
                        });
                        bottomSheetDialog.setContentView(bottomsheetview);
                        bottomSheetDialog.show();
                    }
                }
            }
        });
    }

    public void getSizeListItem(String styleno) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry?styleno=" + styleno;
        final SweetAlertDialog alert = (new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsbundle = jsonObject.getJSONArray("s_GetSizeList");

                                if (jsbundle.length() == 0) {
                                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("size not found").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        String size = jsonobj.getString("size");
                                        sizeLists.add(new SizeList(size, ""));
                                    }

                                    sizeAdapter = new SizeAdapter(context, sizeLists);
                                    lv_listview.setAdapter(sizeAdapter);
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
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("s_GetSizeList");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void getAddOrderDetail(String buyercode, String styleno, String size, String qty, String pcsperbox, String retailercode) throws UnsupportedEncodingException {

        if (retailercode == null) {
            retailercode = "1";
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "localhost/OrderEntry/AddOrderDetail?buyercode=" + buyercode +
                "&styleno=" + URLEncoder.encode(styleno, "UTF-8") +
                "&size=" + URLEncoder.encode(size, "UTF-8") +
                "&qty=" + URLEncoder.encode(qty, "UTF-8") +
                "&pcsperbox=" + URLEncoder.encode(pcsperbox, "UTF-8") +
                "&retailercode=" + URLEncoder.encode(retailercode, "UTF-8");
        final SweetAlertDialog alert = (new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            if (loopproceed) {
                                if (response.contains("ok")) {

                                    Intent intent = new Intent(OrderListItemDetailView.this, OrderActivity.class);
                                    Toast.makeText(context, "Item add to cart successfully", Toast.LENGTH_SHORT).show();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                                loopproceed = false;
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
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("s_GetSizeList");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void s_getAllcartcount(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/s_getAllcartcount?buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsbundle = jsonObject.getJSONArray("s_getcartCount");

                                if (jsbundle.length() == 0) {

                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        pendingmsgCount = Integer.parseInt(jsonobj.getString("count"));
                                        img_Cart.setBadgeValue(pendingmsgCount);
                                    }
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
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("s_s_productsearch");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(OrderListItemDetailView.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        s_getAllcartcount(MainActivity.BuyerCode);
    }
}