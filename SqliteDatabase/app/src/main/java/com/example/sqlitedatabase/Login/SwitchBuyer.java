package com.example.sqlitedatabase.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.sqlitedatabase.AboutApp.AboutActivity;
import com.example.sqlitedatabase.Helper.SQLiteLogin;
import com.example.sqlitedatabase.MainActivity;
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
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SwitchBuyer extends AppCompatActivity {

    Spinner spr_selectbuyer;
    ImageView img_next;
    Toolbar toolbar;
    JSONArray jsbundle;
    Context context;
    public static String Username = "",Name = "";
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapters;
    String buyercode = "",BuyerName = "";
    public int listposition;
    ImageView img_about;
    SQLiteLogin db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_buyer);

        spr_selectbuyer = findViewById(R.id.spr_selectbuyer);
        img_next = findViewById(R.id.img_next);
        toolbar = findViewById(R.id.toolbar);
        img_about = findViewById(R.id.img_about);

        context = getApplicationContext();

        db = new SQLiteLogin(SwitchBuyer.this);

        arrayList = new ArrayList<String>();

        Intent intent = getIntent();
        Username = intent.getStringExtra("appuser");
        Name = intent.getStringExtra("name");

        getBuyerList(Username);

        img_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SwitchBuyer.this, AboutActivity.class);
                startActivity(intent1);
            }
        });

        spr_selectbuyer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                listposition = position;

                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(jsbundle.get(listposition).toString());
                    BuyerName = jsonobj.getString("BuyerName");
                    buyercode = jsonobj.getString("buyercode");
                }
                catch (Exception er){
                    er.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String res = db.deleteTempData();
                if (res.equalsIgnoreCase("ok")){
                    Intent intent = new Intent(SwitchBuyer.this, MainActivity.class);
                    intent.putExtra("buyername",BuyerName);
                    intent.putExtra("buyercode",buyercode);
                    intent.putExtra("name",Name);
                    startActivity(intent);
                }
            }
        });
    }


    public void getBuyerList(String email) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry?email="+ email;
        final SweetAlertDialog alert = (new SweetAlertDialog(SwitchBuyer.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsbundle = jsonObject.getJSONArray("s_GetBuyer");

                                if (jsbundle.length() == 0){
                                    new SweetAlertDialog(SwitchBuyer.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Buyer not assign").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj=new JSONObject(jsbundle.get(i).toString());
                                        String BuyerName=jsonobj.getString("BuyerName");
                                        arrayList.add(BuyerName);
                                    }
                                }

                            } catch (Exception er){
                                er.printStackTrace();
                            }

                            adapters=new ArrayAdapter<String>(SwitchBuyer.this,android.R.layout.simple_list_item_1,arrayList);
                            spr_selectbuyer.setAdapter(adapters);

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
                            new SweetAlertDialog(SwitchBuyer.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                        }
                    }else if (error.networkResponse.statusCode == 502) {
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            new SweetAlertDialog(SwitchBuyer.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                        }
                    } else
                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    alert.dismissWithAnimation();
                });
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}