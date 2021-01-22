package com.example.sqlitedatabase.Helper;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.example.sqlitedatabase.Login.LoginActivity;
import com.example.sqlitedatabase.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductMasterSync extends AppCompatActivity {

    SQLiteLogin sqLiteLogin;
    JSONArray jsonArray;
    Context context;
    String total;
    JSONObject jsonObject;
    SweetAlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_master_sync);

        context = getApplicationContext();
        sqLiteLogin = new SQLiteLogin(ProductMasterSync.this);
        s_getCatMasterCount();
    }

    public void s_getCatMasterCount() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http:";

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsonArray = jsonObject.getJSONArray("s_getCatMasterCount");

                                if (jsonArray.length() == 0) {
                                    new SweetAlertDialog(ProductMasterSync.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("shop not found").show();
                                } else {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                                        total = jsonobj.getString("total");
                                    }
                                    Cursor c1;
                                    c1 = sqLiteLogin.getCatMasterCount();
                                    String Count = c1.getString(c1.getColumnIndex("Count"));

                                    if (Count.equals(total)) {
                                        Intent intent = new Intent(ProductMasterSync.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        sqLiteLogin.deleteAllCatMaster();
                                        s_InsertData();

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
                    new SweetAlertDialog(ProductMasterSync.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    public class s_InsertData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                jsonObject = new JSONObject(strings[0]);

                jsonArray = jsonObject.getJSONArray("s_getCatProd");
                sqLiteLogin = new SQLiteLogin(getApplicationContext());
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                    String StyleItemName = jsonobj.getString("StyleItemName");
                    String pcsperbox = jsonobj.getString("pcsperbox");
                    String size = jsonobj.getString("size");
                    String catGroup = jsonobj.getString("catGroup");
                    String catName = jsonobj.getString("catName");
                    String Sizeindex = jsonobj.getString("sizeIndex");
                    sqLiteLogin.insertAllProductMaster(StyleItemName, size, pcsperbox, catGroup, catName,Sizeindex);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alert.dismissWithAnimation();
            Intent intent = new Intent(ProductMasterSync.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void s_InsertData() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http:";
        alert = (new SweetAlertDialog(ProductMasterSync.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading Category Master Please Wait ....");
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        /*progressDialog = new ProgressDialog(ProductMasterSync.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);*/

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        (new s_InsertData()).execute(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }
}