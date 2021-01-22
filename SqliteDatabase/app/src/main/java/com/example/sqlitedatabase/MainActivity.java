package com.example.sqlitedatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.AddShop.InsertNewShop;
import com.example.sqlitedatabase.Helper.SQLiteLogin;
import com.example.sqlitedatabase.Helper.WebService;
import com.example.sqlitedatabase.Login.LoginActivity;
import com.example.sqlitedatabase.Login.SwitchBuyer;
import com.example.sqlitedatabase.MyAccount.MyAccount;
import com.example.sqlitedatabase.MyCart.CartItemActivity;
import com.example.sqlitedatabase.Notification.CategoryWiseNotification;
import com.example.sqlitedatabase.Order.OrderSelectionList.OrderActivity;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeVie
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String BuyerName= "",BuyerCode = "",str_username = "",str_userid = "";
    private static final String NAMESPACE = "http://tempuri.org/";
    CardView cv_order, cv_notification,cv_shop;
    Integer pendingmsgCount = 0;
    ImageBadgeView imageBadgeView;
    String str_buyercode,str_retailercode,str_retailername;
    Spinner spr_selectshop;
    JSONArray jsonArray;
    ArrayList<String> arrayList1;
    ArrayAdapter<String> adapter1;
    Context context;
    public int listposition;
    TextView tv_buyername;
    public static String Name = "";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView imageView;
    static final float END_SCALE = 0.7f;
    LinearLayout linearLayout;
    SQLiteLogin db;

    Dialog dialogbox;
    TextView dialogmessage;
    Button btn_no,btn_yes;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        spr_selectshop = findViewById(R.id.spr_selectshop);
        cv_order = findViewById(R.id.cv_order);
        cv_notification = findViewById(R.id.cv_notification);
        cv_shop = findViewById(R.id.cv_shop);
        imageBadgeView = findViewById(R.id.ibv_icon2);
        tv_buyername = findViewById(R.id.tv_buyername);
        linearLayout = findViewById(R.id.linearLayout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        imageView = findViewById(R.id.imgid);

        dialogbox = new Dialog(this);

        navigationView();

        db = new SQLiteLogin(MainActivity.this);

        imageBadgeView.setBadgeValue(0)
                .setBadgeOvalAfterFirst(true)
                .setBadgeTextSize(16)
                .setMaxBadgeValue(999)
                .setBadgeBackground(getResources().getDrawable(R.drawable.rectangle_rounded))
                .setBadgePosition(BadgePosition.TOP_RIGHT)
                .setBadgeTextStyle(Typeface.NORMAL)
                .setShowCounter(true)
                .setBadgePadding(4);

        arrayList1 = new ArrayList<String>();

        Intent intent = getIntent();

        str_userid = intent.getStringExtra("userid");
        str_username = intent.getStringExtra("appuser");
        BuyerName = intent.getStringExtra("buyername");
        BuyerCode = intent.getStringExtra("buyercode");
        Name = intent.getStringExtra("name");

        Cursor c1;
        c1 = db.TempCount();
        String Count = c1.getString(c1.getColumnIndex("Count"));

        if (Count.equalsIgnoreCase("0")){
            db.saveTempTable(str_userid,str_username,BuyerName,BuyerCode,Name);
        } else {
            Cursor c2;
            c2 = db.getTempData();
            str_userid = c2.getString(c2.getColumnIndex("userid"));
            str_username = c2.getString(c2.getColumnIndex("username"));
            BuyerName = c2.getString(c2.getColumnIndex("BuyerName"));
            BuyerCode = c2.getString(c2.getColumnIndex("BuyerCode"));
            Name = c2.getString(c2.getColumnIndex("Name"));
        }

        tv_buyername.setText(BuyerName);

        getShopList(BuyerCode);

        firebasetoken(str_userid);
        (new getNotificationCount()).execute(str_userid);

        View headerView = navigationView.getHeaderView(0);
        TextView tv_username = (TextView) headerView.findViewById(R.id.username);
        TextView tv_email = (TextView) headerView.findViewById(R.id.email);
        TextView tv_buyername = (TextView) headerView.findViewById(R.id.buyername);
        tv_username.setText(Name);
        tv_email.setText(SwitchBuyer.Username);
        tv_buyername.setText(BuyerName);


        spr_selectshop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                listposition = position;

                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(jsonArray.get(listposition).toString());

                    str_retailercode = jsonobj.getString("retailercode");
                    str_retailername = jsonobj.getString("retailername");
                }
                catch (Exception er){
                    er.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order = new Intent(MainActivity.this, OrderActivity.class);
                order.putExtra("appuser",str_username);
                order.putExtra("buyercode",str_buyercode);
                order.putExtra("retailercode",str_retailercode);
                order.putExtra("retailername",str_retailername);
                startActivity(order);
            }
        });

        cv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notification = new Intent(MainActivity.this, CategoryWiseNotification.class);
                notification.putExtra("userid", str_userid);
                startActivity(notification);
            }
        });

        cv_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shop = new Intent(MainActivity.this, InsertNewShop.class);
                startActivity(shop);
            }
        });
    }


    public  void firebasetoken(String userid){
        FirebaseInstanceId.getInstance().getInstanceId()

                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FireBase", "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    (new firebasetoken_update()).execute (userid,token);
                });
    }

    public static class firebasetoken_update extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String NAMESPACE = "http://tempuri.org/";
            String[] paras = {"userid", "firebasetoken"};
            String[] values = {params[0], params[1]};
            String methodname = "U_FirebaseToken";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public class getNotificationCount extends AsyncTask<String, String, String> {

        SweetAlertDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading!..");
            p.setCancelable(false);
            p.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userid"};
            String[] values = {params[0]};
            String methodname = "S_GetNotifications_PendingsCount";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                    pendingmsgCount = Integer.parseInt(jsonobj.getString("count"));
                    imageBadgeView.setBadgeValue(pendingmsgCount);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getShopList(String buyercode) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetBuyerRetailer?buyercode="+ buyercode;
        final SweetAlertDialog alert = (new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response !=null){

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsonArray = jsonObject.getJSONArray("m_BuyerRetailerMaster");

                                if (jsonArray.length() == 0){
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("shop not found").show();
                                } else {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonobj=new JSONObject(jsonArray.get(i).toString());
                                        String retailername=jsonobj.getString("retailername");
                                        arrayList1.add(retailername);
                                    }
                                }

                            } catch (Exception er){
                                er.printStackTrace();
                            }

                            adapter1=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList1);
                            spr_selectshop.setAdapter(adapter1);


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
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            }else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsonArray = jsonObject.getJSONArray("m_BuyerRetailerMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void navigationView(){

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        imageView.setOnClickListener(new View.OnClickListener() {
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
                linearLayout.setScaleX(offsetscale);
                linearLayout.setScaleY(offsetscale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetdiff = linearLayout.getWidth() * diffscalledOffset/2;
                final float xTranslation = xOffset - xOffsetdiff;
                linearLayout.setTranslationX(xTranslation);
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
            finish();
            startActivity(getIntent());
        } else if (id == R.id.nav_mynotification){
            Intent intent = new Intent(MainActivity.this,CategoryWiseNotification.class);
            startActivity(intent);
        } else if (id == R.id.nav_mycart){
            Intent intent = new Intent(MainActivity.this, CartItemActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_myaccount){
            Intent intent = new Intent(MainActivity.this, MyAccount.class);
            startActivity(intent);
        } else if (id == R.id.nav_addshop){
            Intent intent = new Intent(MainActivity.this, InsertNewShop.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout){
            ShowAlertDailogbox();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new getNotificationCount()).execute(str_userid);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
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