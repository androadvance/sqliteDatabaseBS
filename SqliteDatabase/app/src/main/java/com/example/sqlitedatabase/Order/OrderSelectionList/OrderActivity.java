package com.example.sqlitedatabase.Order.OrderSelectionList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.AddShop.InsertNewShop;
import com.example.sqlitedatabase.Helper.SQLiteLogin;
import com.example.sqlitedatabase.Login.LoginActivity;
import com.example.sqlitedatabase.Login.SwitchBuyer;
import com.example.sqlitedatabase.MainActivity;
import com.example.sqlitedatabase.MyAccount.MyAccount;
import com.example.sqlitedatabase.MyCart.CartItemActivity;
import com.example.sqlitedatabase.Notification.CategoryWiseNotification;
import com.example.sqlitedatabase.Order.FilterOption.FilterItems;
import com.example.sqlitedatabase.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

public class OrderActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    Context context;
    ImageBadgeView img_Cart;
    ListView lv_listview;
    private List<OrderList> orderLists;
    private OrderListAdapter orderListAdapter;
    JSONArray jsbundle;
    public JSONArray jsonArray = null;
    String str_imgSrc = "";
    public int listposition;
    LinearLayout linearsort, linearFilter;
    String str_buyercode = "", str_retailercode = "",str_retailername = "";
    Integer pendingmsgCount = 0;
    String FilterApply;
    SQLiteLogin db;
    SearchView searchview;
    public List<OrderList> orderLists_temp;
    ImageView imageView,img_view;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    static final float END_SCALE = 0.7f;
    ConstraintLayout constraint;
    String catGroup,catName,StyleNo,itemname,PcsPerBox,size,StyleItemName;
    Dialog dialogbox;
    TextView dialogmessage;
    Button btn_no,btn_yes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        linearsort = findViewById(R.id.linearsort);
        linearFilter = findViewById(R.id.linearFilter);
        img_Cart = findViewById(R.id.img_Cart);
        lv_listview = findViewById(R.id.lv_listview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchview = findViewById(R.id.searchview);
        imageView = findViewById(R.id.imageview);
        img_view = findViewById(R.id.imgid);
        constraint = findViewById(R.id.constraint);

        context = getApplicationContext();

        orderLists = new ArrayList<>();

        Intent intent = getIntent();

        str_buyercode = intent.getStringExtra("buyercode");
        str_retailercode = intent.getStringExtra("retailercode");
        str_retailername = intent.getStringExtra("retailername");

        FilterApply = intent.getStringExtra("FilterApply");

        db = new SQLiteLogin(OrderActivity.this);

        search(searchview);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        TextView tv_username = (TextView) headerView.findViewById(R.id.username);
        TextView tv_email = (TextView) headerView.findViewById(R.id.email);
        TextView tv_buyername = (TextView) headerView.findViewById(R.id.buyername);
        TextView tv_shopname = (TextView) headerView.findViewById(R.id.shopname);
        tv_username.setText(MainActivity.Name);
        tv_email.setText(SwitchBuyer.Username);
        tv_buyername.setText(MainActivity.BuyerName);
        tv_shopname.setText(str_retailername);

        dialogbox = new Dialog(this);

        if (FilterApply == null) {
            db.deleteFilter();
            getProductListItem("", "", "", "", "");
        } else if (FilterApply.equalsIgnoreCase("false")) {
            getProductListItem("", "", "", "", "");
        } else if (FilterApply.equalsIgnoreCase("True")) {
            Cursor c1;
            c1 = db.fetchListitems();
            String group = "",catagory="",stylenumber="",str_itemname="",pcsperbox="",Size="";
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    do {
                        catGroup = c1.getString(c1.getColumnIndex("Groups"));
                        catName = c1.getString(c1.getColumnIndex("Category"));
                        StyleItemName = c1.getString(c1.getColumnIndex("Styleno"));
                        if (StyleItemName != null){
                            String[] Style = StyleItemName.split("_");
                            StyleNo = Style[0];
                            itemname = Style[1];
                        }
                        PcsPerBox = c1.getString(c1.getColumnIndex("PcsPerBox"));
                        size = c1.getString(c1.getColumnIndex("Size"));

                        if(catGroup!=null) {
                            group=group+","+catGroup;
                        }

                        if (catName!=null){
                            catagory=catagory+","+catName;
                        }

                        if (StyleNo!=null){
                            stylenumber=stylenumber+","+StyleNo;
                        }

                        if (itemname!=null){
                            str_itemname=str_itemname+","+itemname;
                        }

                        if (PcsPerBox!=null){
                            pcsperbox=pcsperbox+","+PcsPerBox;
                        }

                        if (size!=null){
                            Size=Size+","+size;
                        }

                    } while (c1.moveToNext());

                    if (!group.isEmpty()){
                        group = group.substring(1);
                    }
                    if (!catagory.isEmpty()){
                        catagory = catagory.substring(1);
                    }
                    if (!stylenumber.isEmpty()){
                        stylenumber = stylenumber.substring(1);
                    }
                    if (!str_itemname.isEmpty()){
                        str_itemname = str_itemname.substring(1);
                    }
                    if (!pcsperbox.isEmpty()){
                        pcsperbox = pcsperbox.substring(1);
                    }
                    if (!Size.isEmpty()){
                        Size = Size.substring(1);
                    }

                    try {
                        getProductFilterListItem(group, catagory, stylenumber, str_itemname, pcsperbox,Size,"");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        navigationView();

        img_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, CartItemActivity.class);
                startActivity(intent);
            }
        });

        linearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, FilterItems.class);
                startActivity(intent);
            }
        });

        linearsort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(OrderActivity.this, R.style.BottomSheetDialogTheme);

                View bottomsheetview = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.sorting_bottomsheet, (LinearLayout) findViewById(R.id.bottomsheetcontainer));

                RadioGroup radiogroup = bottomsheetview.findViewById(R.id.radiogroup);

                radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {

                        int radioButtonID = radiogroup.getCheckedRadioButtonId();
                        View radioButton = radiogroup.findViewById(radioButtonID);
                        int radiobuttonposition = radiogroup.indexOfChild(radioButton);

                        RadioButton rb = (RadioButton) radiogroup.getChildAt(radiobuttonposition);
                        String selectedtext = rb.getText().toString();

                        try {
                            switch (selectedtext) {
                                case "StyleNo  --  Ascending to Descending":
                                    orderLists.clear();
                                    getProductFilterListItem("", "", "", "", "", "", "stylenoasc");
                                    break;
                                case "StyleNo  --  Descending to Ascending":
                                    orderLists.clear();
                                    getProductFilterListItem("", "", "", "", "", "", "stylenodesc");
                                    break;
                                case "PCS Per Box  --  Ascending to Descending":
                                    orderLists.clear();
                                    getProductFilterListItem("", "", "", "", "", "", "pcsperboxasc");
                                    break;
                                case "PCS Per Box  --  Descending to Ascending":
                                    orderLists.clear();
                                    getProductFilterListItem("", "", "", "", "", "", "pcsperboxdesc");
                                    break;
                                case "Popularity":
                                    orderLists.clear();
                                    getProductFilterListItem("", "", "", "", "", "", "");
                                    break;
                                case "Newest First":
                                    orderLists.clear();
                                    getProductFilterListItem("", "", "", "", "", "", "");
                                    break;
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(bottomsheetview);
                bottomSheetDialog.show();
            }
        });

        img_Cart.setBadgeValue(0)
                .setBadgeOvalAfterFirst(true)
                .setBadgeTextSize(10)
                .setMaxBadgeValue(999)
                .setBadgeBackground(getResources().getDrawable(R.drawable.rectangle_rounded))
                .setBadgePosition(BadgePosition.TOP_RIGHT)
                .setBadgeTextStyle(Typeface.NORMAL)
                .setShowCounter(true)
                .setBadgePadding(4);

        s_getAllcartcount(MainActivity.BuyerCode);


        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                if (orderLists_temp != null) {

                    Intent intent = new Intent(OrderActivity.this, OrderListItemDetailView.class);

                    int pos = parent.getPositionForView(view);

                    String styleno = orderLists_temp.get(pos).getStyleno();
                    String itemname = orderLists_temp.get(pos).getItemname();
                    String catName = orderLists_temp.get(pos).getCategoryname();
                    String catGroup = orderLists_temp.get(pos).getCategorygroup();
                    String imgSrc = orderLists_temp.get(pos).getImage();
                    String sizes = orderLists_temp.get(pos).getSizes();
                    String pcsperbundle = orderLists_temp.get(pos).getPcsperbundle();
                    String pcsperbox = orderLists_temp.get(pos).getPcsperbox();

                    String[] ppb = pcsperbox.split(" ");

                    String str_pcsperbox = ppb[2];

                    str_imgSrc = imgSrc.substring(1);

                    intent.putExtra("styleno", styleno);
                    intent.putExtra("itemname", itemname);
                    intent.putExtra("catName", catName);
                    intent.putExtra("catGroup", catGroup);
                    intent.putExtra("imgSrc", str_imgSrc);
                    intent.putExtra("sizes", sizes);
                    intent.putExtra("pcsperbundle", pcsperbundle);
                    intent.putExtra("pcsperbox", str_pcsperbox);
                    intent.putExtra("retailercode", str_retailercode);

                    startActivity(intent);

                } else {

                    Intent intent = new Intent(OrderActivity.this, OrderListItemDetailView.class);
                    listposition = position;

                    JSONObject jsonobj = null;
                    try {

                        jsonobj = new JSONObject(jsonArray.get(listposition).toString());

                        String styleno = jsonobj.getString("styleno");
                        String itemname = jsonobj.getString("itemname");
                        String catName = jsonobj.getString("catName");
                        String catGroup = jsonobj.getString("catGroup");
                        String imgSrc = jsonobj.getString("ImgSrc");
                        String sizes = jsonobj.getString("sizes");
                        String pcsperbundle = jsonobj.getString("pcsperbundle");
                        String pcsperbox = jsonobj.getString("pcsperbox");

                        String[] ppb = pcsperbox.split(" ");

                        String str_pcsperbox = ppb[0];

                        str_imgSrc = imgSrc.substring(1);

                        intent.putExtra("styleno", styleno);
                        intent.putExtra("itemname", itemname);
                        intent.putExtra("catName", catName);
                        intent.putExtra("catGroup", catGroup);
                        intent.putExtra("imgSrc", str_imgSrc);
                        intent.putExtra("sizes", sizes);
                        intent.putExtra("pcsperbundle", pcsperbundle);
                        intent.putExtra("pcsperbox", str_pcsperbox);
                        intent.putExtra("buyercode", str_buyercode);
                        intent.putExtra("retailercode", str_retailercode);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            }
        });
    }


    public void getProductListItem(String catgroup, String catcode, String styleno, String itemname, String pcppebox) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry?group=" + catgroup + "&catcode=" + catcode + "&styleno=" + styleno + "&itemname=" + itemname + "&pcsperbox=" + pcppebox;
        final SweetAlertDialog alert = (new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            orderLists.clear();
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsonArray = jsonObject.getJSONArray("s_s_productsearch");

                                if (jsonArray.length() == 0) {
                                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Product not found").show();
                                } else {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                                        String styleno = jsonobj.getString("styleno");
                                        String itemname = jsonobj.getString("itemname");
                                        String catName = jsonobj.getString("catName");
                                        String catGroup = jsonobj.getString("catGroup");
                                        String imgSrc = jsonobj.getString("ImgSrc");
                                        String pcsperbox = jsonobj.getString("pcsperbox");
                                        String size = jsonobj.getString("sizes");
                                        String pcsperbundle = jsonobj.getString("pcsperbundle");

                                        String str_pcsperbox = "PcsPerBox - " + pcsperbox;

                                        str_imgSrc = imgSrc.substring(1);

                                        orderLists.add(new OrderList(str_imgSrc, styleno, catName, catGroup, itemname, str_pcsperbox, size, pcsperbundle));

                                    }

                                    orderListAdapter = new OrderListAdapter(orderLists, getApplicationContext());
                                    lv_listview.setAdapter(orderListAdapter);
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
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
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
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void getProductFilterListItem(String catgroup, String catcode, String styleno, String itemname, String pcsperbox,String Size,String sorting) throws UnsupportedEncodingException {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/" +
                        "s_getFilterListItem?catgroup="
                        + URLEncoder.encode(catgroup,"UTF-8")
                        + "&catname=" +  URLEncoder.encode(catcode,"UTF-8")
                        + "&styleno=" +  URLEncoder.encode(styleno,"UTF-8")
                        + "&itemname=" +  URLEncoder.encode(itemname,"UTF-8")
                        + "&pcsperbox=" + URLEncoder.encode(pcsperbox,"UTF-8")
                        + "&size="+ URLEncoder.encode(Size,"UTF-8")
                        + "&sorting="+sorting;


        final SweetAlertDialog alert = (new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();

                        orderLists.clear();
                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsonArray = jsonObject.getJSONArray("s_s_productandroidsearch");

                                if (jsonArray.length() == 0) {
                                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Product not found").show();
                                } else {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                                        String styleno = jsonobj.getString("styleno");
                                        String itemname = jsonobj.getString("itemname");
                                        String catName = jsonobj.getString("catName");
                                        String catGroup = jsonobj.getString("catGroup");
                                        String imgSrc = jsonobj.getString("ImgSrc");
                                        String pcsperbox = jsonobj.getString("pcsperbox1");
                                        String size = jsonobj.getString("sizes");
                                        String pcsperbundle = jsonobj.getString("pcsperbundle");

                                        String str_pcsperbox = "PcsPerBox - " + pcsperbox;

                                        str_imgSrc = imgSrc.substring(1);

                                        orderLists.add(new OrderList(str_imgSrc, styleno, catName, catGroup, itemname, str_pcsperbox, size, pcsperbundle));

                                    }

                                    orderListAdapter = new OrderListAdapter(orderLists, getApplicationContext());
                                    lv_listview.setAdapter(orderListAdapter);
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
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
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
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void s_getAllcartcount(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/s_getAllcartcount?buyercode=" + buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.PROGRESS_TYPE));
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
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
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
                    new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(OrderActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //String d=newText.toUpperCase();
                if(!newText.isEmpty()) {
                    orderLists_temp = new ArrayList<>();
                    //orderLists_temp.clear();
                    for (OrderList o : orderLists) {
                        if (o.getStyleno().toLowerCase().contains(newText)
                                || o.getItemname().toLowerCase().contains(newText)
                                || o.getCategoryname().toLowerCase().contains(newText)
                                || o.getCategorygroup().toLowerCase().contains(newText)
                                || o.getPcsperbox().toLowerCase().contains(newText)) {
                            orderLists_temp.add(o);
                        }
                    }
                    if (orderLists_temp.size() > 0) {
                        orderListAdapter = new OrderListAdapter(orderLists_temp, getApplicationContext());
                        lv_listview.setAdapter(orderListAdapter);
                        lv_listview.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                    } else {
                        lv_listview.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setBackgroundResource(R.drawable.notfound);
                    }
                } else {
                    orderListAdapter = new OrderListAdapter(orderLists, getApplicationContext());
                    lv_listview.setAdapter(orderListAdapter);
                    lv_listview.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        s_getAllcartcount(MainActivity.BuyerCode);
        /*Cursor c1;
        c1 = db.fetchListitems();
        if (c1.getCount() == 0){
            orderLists.clear();
            getProductListItem("", "", "", "", "");
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void navigationView(){

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawer();

    }

    public void animateNavigationDrawer(){

        drawerLayout.setScrimColor(getResources().getColor(R.color.grey_100));

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                final float diffscalledOffset = slideOffset * (1 - END_SCALE);
                final float offsetscale = 1 - diffscalledOffset;
                constraint.setScaleX(offsetscale);
                constraint.setScaleY(offsetscale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetdiff = constraint.getWidth() * diffscalledOffset/2;
                final float xTranslation = xOffset - xOffsetdiff;
                constraint.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home){
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_mynotification){
            Intent intent = new Intent(OrderActivity.this, CategoryWiseNotification.class);
            startActivity(intent);
        } else if (id == R.id.nav_mycart){
            Intent intent = new Intent(OrderActivity.this, CartItemActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_myaccount){
            Intent intent = new Intent(OrderActivity.this, MyAccount.class);
            startActivity(intent);
        } else if (id == R.id.nav_addshop){
            Intent intent = new Intent(OrderActivity.this, InsertNewShop.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout){
            ShowAlertDailogbox();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ShowAlertDailogbox(){

        dialogbox.setContentView(R.layout.logout_dialogbox);
        dialogmessage = findViewById(R.id.textid);
        btn_yes = dialogbox.findViewById(R.id.btn_yes);
        btn_no = dialogbox.findViewById(R.id.btn_no);
        dialogbox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = db.deleteLogin();
                if (res.equalsIgnoreCase("ok")){
                    Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialogbox.show();
    }
}

