package com.example.sqlitedatabase.MyCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.MainActivity;
import com.example.sqlitedatabase.NonScrollListView;
import com.example.sqlitedatabase.Order.OrderSelectionList.OrderActivity;
import com.example.sqlitedatabase.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

public class CartItemActivity extends AppCompatActivity {

    NonScrollListView lv_listview;
    Button btn_placeorder;
    TextView tv_totalamount, tv_buyername, tv_date, tv_deliverydate;
    Spinner spr_billingAddress, spr_deliveryAddress;
    EditText eT_porefno;
    ArrayList<String> billadd_arrayList;
    ArrayAdapter<String> billadd_adapters;
    ArrayList<String> shipadd_arrayList;
    ArrayAdapter<String> shipadd_adapters;
    public Context context;
    JSONArray jsbundle;
    DatePickerDialog datePickerDialog;
    public static List<CartList> cartLists;
    public static CartAdapter cartAdapter;
    public int listposition;
    public int spinnershiplistposition;
    public int spinnerbillinglistposition;
    String styleno = "";
    ConstraintLayout rootlayout;
    Integer pendingmsgCount = 0;
    ImageBadgeView img_Cart;
    int total;
    String productdesc, pcsPerBox, PCSPerBundle, Qty, productamount;
    EditText eT_remarks;
    public boolean loopproceed = true;
    String BillAddrsId = "", ShipAddrId = "";
    JSONArray jsonArray_shipadd, jsonArray_billadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_item);

        context = getApplicationContext();

        lv_listview = findViewById(R.id.lv_listview);
        btn_placeorder = findViewById(R.id.btn_placeorder);
        tv_totalamount = findViewById(R.id.tv_totalamount);
        tv_buyername = findViewById(R.id.tv_buyername);
        tv_date = findViewById(R.id.tv_date);
        tv_deliverydate = findViewById(R.id.tv_deliverydate);
        spr_billingAddress = findViewById(R.id.spr_billingAddress);
        spr_deliveryAddress = findViewById(R.id.spr_deliveryAddress);
        eT_porefno = findViewById(R.id.eT_porefno);
        rootlayout = findViewById(R.id.rootlayout);
        img_Cart = findViewById(R.id.img_Cart);
        eT_remarks = findViewById(R.id.eT_remarks);

        // current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        tv_date.setText(currentDateandTime);

        billadd_arrayList = new ArrayList<String>();

        shipadd_arrayList = new ArrayList<>();

        cartLists = new ArrayList<>();

        GetBillingAddress(MainActivity.BuyerCode);

        GetShippingAddress(MainActivity.BuyerCode);

        GetAllCartProduct(MainActivity.BuyerCode);

        s_getAllcartcount(MainActivity.BuyerCode);

        img_Cart.setBadgeValue(0)
                .setBadgeOvalAfterFirst(true)
                .setBadgeTextSize(10)
                .setMaxBadgeValue(999)
                .setBadgeBackground(getResources().getDrawable(R.drawable.rectangle_rounded))
                .setBadgePosition(BadgePosition.TOP_RIGHT)
                .setBadgeTextStyle(Typeface.NORMAL)
                .setShowCounter(true)
                .setBadgePadding(4);


        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tv_deliverydate.getText().toString().equalsIgnoreCase("Select date")) {
                    Toast.makeText(context, "Please select delivery date", Toast.LENGTH_SHORT).show();
                } else {

                    String deliverydate = parseDateToddMMyyyy(tv_deliverydate.getText().toString());
                    String date = parseDateToddMMyyyy(tv_date.getText().toString());
                    String s_deliveryadd = spr_deliveryAddress.getSelectedItem().toString().replace(",", " ");

                    i_PlaceOrder(eT_porefno.getText().toString(), MainActivity.BuyerCode, date,
                            date, deliverydate, MainActivity.Name, eT_remarks.getText().toString(),
                            BillAddrsId, ShipAddrId, s_deliveryadd, MainActivity.BuyerName);
                }
            }
        });

        spr_billingAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                spinnerbillinglistposition = position;

                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(jsonArray_billadd.get(spinnerbillinglistposition).toString());

                    BillAddrsId = jsonobj.getString("BillAddrsId");

                } catch (Exception er) {
                    er.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spr_deliveryAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                spinnershiplistposition = position;

                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(jsonArray_shipadd.get(spinnershiplistposition).toString());

                    ShipAddrId = jsonobj.getString("ShipAddrId");

                } catch (Exception er) {
                    er.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                listposition = position;

                Intent intent = new Intent(CartItemActivity.this, CartItemUpdate.class);
                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(jsbundle.get(listposition).toString());

                    String styleno = jsonobj.getString("styleno");
                    String PCSPerBox = jsonobj.getString("PCSPerBox");
                    String catGroup = jsonobj.getString("catGroup");
                    String catName = jsonobj.getString("catName");
                    String itemname = jsonobj.getString("itemname");
                    String qty = jsonobj.getString("qty");
                    String amount = jsonobj.getString("amount");
                    String ImgSrc = jsonobj.getString("ImgSrc");
                    String pcsperbundle = jsonobj.getString("pcsperbundle");

                    String str_imgSrc = ImgSrc.substring(1);

                    intent.putExtra("styleno", styleno);
                    intent.putExtra("PCSPerBox", PCSPerBox);
                    intent.putExtra("ImgSrc", str_imgSrc);
                    intent.putExtra("catGroup", catGroup);
                    intent.putExtra("catName", catName);
                    intent.putExtra("itemname", itemname);
                    intent.putExtra("qty", qty);
                    intent.putExtra("amount", amount);
                    intent.putExtra("pcsperbundle", pcsperbundle);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });


        tv_deliverydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(CartItemActivity.this,
                        (view1, year, monthOfYear, dayOfMonth) -> tv_deliverydate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 10000);
                datePickerDialog.show();
            }
        });
    }

    public void GetBillingAddress(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetBillingAddress?buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsonArray_billadd = jsonObject.getJSONArray("m_BuyerBillingMaster");

                                if (jsonArray_billadd.length() == 0) {
                                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Billing address not found").show();
                                } else {

                                    for (int i = 0; i < jsonArray_billadd.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsonArray_billadd.get(i).toString());
                                        String BillAddress = jsonobj.getString("BillAddress");

                                        /*if (!billadd_arrayList.contains("Select BillingAddress")){
                                            billadd_arrayList.add("Select BillingAddress");
                                        }*/
                                        billadd_arrayList.add(BillAddress);
                                    }
                                    billadd_adapters = new ArrayAdapter<String>(CartItemActivity.this, android.R.layout.simple_spinner_item, billadd_arrayList);
                                    spr_billingAddress.setAdapter(billadd_adapters);
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
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else
                new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void GetShippingAddress(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetShippingAddress?buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsonArray_shipadd = jsonObject.getJSONArray("m_BuyerDeliveryMaster");

                                if (jsonArray_shipadd.length() == 0) {
                                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Delivery address not found").show();
                                } else {

                                    for (int i = 0; i < jsonArray_shipadd.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsonArray_shipadd.get(i).toString());
                                        String shipAddress = jsonobj.getString("shipAddress");

                                        /*if (!shipadd_arrayList.contains("Select ShippingAddress")){
                                            shipadd_arrayList.add("Select ShippingAddress");
                                        }*/
                                        shipadd_arrayList.add(shipAddress);
                                    }
                                    shipadd_adapters = new ArrayAdapter<String>(CartItemActivity.this, android.R.layout.simple_spinner_item, shipadd_arrayList);
                                    spr_deliveryAddress.setAdapter(shipadd_adapters);
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
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("m_BuyerDeliveryMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void GetAllCartProduct(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/s_getAllCartProductDetail?buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    alert.dismissWithAnimation();
                    if (response != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            jsbundle = jsonObject.getJSONArray("s_getallCartProductDetail");

                            if (jsbundle.length() == 0) {
                                rootlayout.setVisibility(View.INVISIBLE);
                                new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Warning...")
                                        .setContentText("Cart item is Empty")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                Intent intent = new Intent(CartItemActivity.this, OrderActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                sweetAlertDialog.dismissWithAnimation();
                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .show();

                            } else {

                                cartLists.clear();

                                for (int i = 0; i < jsbundle.length(); i++) {
                                    JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                    styleno = jsonobj.getString("styleno");
                                    String PCSPerBox = jsonobj.getString("PCSPerBox");
                                    String catGroup = jsonobj.getString("catGroup");
                                    String catName = jsonobj.getString("catName");
                                    String itemname = jsonobj.getString("itemname");
                                    String qty = jsonobj.getString("qty");
                                    String amount = jsonobj.getString("amount");
                                    String ImgSrc = jsonobj.getString("ImgSrc");
                                    String pcsperbundle = jsonobj.getString("pcsperbundle");

                                    String str_imgSrc = ImgSrc.substring(1);

                                    productdesc = styleno + "_" + itemname + "_" + catGroup + "_" + catName;
                                    pcsPerBox = "Pack Type - " + PCSPerBox;
                                    PCSPerBundle = "Total PackPer Bundle - " + pcsperbundle;
                                    Qty = "Total Qty in Bundle - " + qty;
                                    cartLists.add(new CartList(str_imgSrc, productdesc, "", "", "", pcsPerBox, Qty, amount, PCSPerBundle, "Remove", getResources().getString(R.string.u20b9_734)));

                                }

                                cartAdapter = new CartAdapter(getApplicationContext(), cartLists, CartItemActivity.this);
                                lv_listview.setAdapter(cartAdapter);
                                cartAdapter.notifyDataSetChanged();
                                total = 0;
                                for (int i = 0; i < cartLists.size(); i++) {
                                    CartList cartList = cartLists.get(i);
                                    String amt = cartList.getAmount();
                                    total = total + Integer.parseInt(amt);
                                }
                                productamount = getResources().getString(R.string.u20b9_734) + " " + total;
                                tv_totalamount.setText(productamount);

                            }

                        } catch (Exception er) {
                            er.printStackTrace();
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
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
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
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void s_getAllcartcount(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/s_getAllcartcount?buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.PROGRESS_TYPE));
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
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
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
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void i_PlaceOrder(String poreqno, String buyercode, String podate, String porecdate, String deliverydate, String audituser, String remarks, String billaddid, String shipaddid, String shippingaddress, String BuyerName) {

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            String url = "localhost/OrderEntry/i_placeorder?poreqno=" + poreqno +
                    "&buyercode=" + buyercode +
                    "&podate=" + podate +
                    "&porecdate=" + porecdate +
                    "&deliverydate=" + deliverydate +
                    "&audituser=" + audituser +
                    "&remarks=" + remarks +
                    "&billaddid=" + billaddid +
                    "&shipaddid=" + shipaddid +
                    "&shippingaddress=" + URLEncoder.encode(shippingaddress, "UTF-8") +
                    "&buyername=" + BuyerName;

            final SweetAlertDialog alert = (new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.PROGRESS_TYPE));
            alert.setContentText("Loading");
            alert.show();

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            if (loopproceed) {
                                if (response.contains("success")) {
                                    Intent intent = new Intent(CartItemActivity.this, ContinueShopping.class);
                                    Toast.makeText(context, "Order Placed successfully", Toast.LENGTH_SHORT).show();
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
                        new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                    }
                } else
                    new SweetAlertDialog(CartItemActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                alert.dismissWithAnimation();
            });
            queue.add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetAllCartProduct(MainActivity.BuyerCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String parseDateToddMMyyyy(String dateformat) {
        String inputPattern = "dd/MM/yyyy";
        String outputPattern = "yyyy-MM-dd";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateformat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }
}