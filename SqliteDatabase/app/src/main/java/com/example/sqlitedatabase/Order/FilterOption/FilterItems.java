package com.example.sqlitedatabase.Order.FilterOption;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.Helper.SQLiteLogin;
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
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FilterItems extends AppCompatActivity {

    Toolbar toolbar;
    ListView lv_type, lv_subtype;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapters;
    private static List<FilterList> lists;
    private static FilterAdapter filterAdapteradapter;
    Button btn_apply;
    JSONArray jsbundle;
    public Context context;
    SQLiteLogin sqLiteLogin;
    TextView tv_clearfilter;
    public  static String  selectedfiltertype="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_items);

        lv_type = findViewById(R.id.lv_type);
        lv_subtype = findViewById(R.id.lv_subtype);
        btn_apply = findViewById(R.id.btn_apply);
        tv_clearfilter = findViewById(R.id.tv_clearfilter);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        sqLiteLogin = new SQLiteLogin(FilterItems.this);

        arrayList = new ArrayList<String>();
        arrayList.add("Group");
        arrayList.add("Category");
        arrayList.add("StyleNo & ItemName");
        arrayList.add("PCSPerBox");
        arrayList.add("Size");
        adapters = new ArrayAdapter<String>(FilterItems.this, android.R.layout.simple_list_item_1, arrayList);
        lv_type.setAdapter(adapters);

        lists = new ArrayList<>();

        Cursor c1;
        c1 = sqLiteLogin.DynamicQuery("Group");
        do {
            String list = c1.getString(c1.getColumnIndex("list"));
            lists.add(new FilterList(list));
        } while (c1.moveToNext());
        filterAdapteradapter = new FilterAdapter(context, lists);
        lv_subtype.setAdapter(filterAdapteradapter);

        selectedfiltertype = "Group";
        lv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String value = lv_type.getAdapter().getItem(position).toString();
                lists.clear();
                Cursor c1;
                selectedfiltertype=value;
                c1 = sqLiteLogin.DynamicQuery(value);
                if (c1.getCount() > 0) {
                    do {
                        String list = c1.getString(c1.getColumnIndex("list"));
                        lists.add(new FilterList(list));
                    } while (c1.moveToNext());
                    filterAdapteradapter = new FilterAdapter(context, lists);
                    lv_subtype.setAdapter(filterAdapteradapter);
                }
            }
        });

        tv_clearfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteLogin db = new SQLiteLogin(FilterItems.this);
                db.deleteFilter();
                finish();
                startActivity(getIntent());
            }
        });


        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FilterItems.this, OrderActivity.class);
                Cursor c1;
                c1 = sqLiteLogin.fetchListitems();
                if (c1.getCount() == 0) {
                    intent.putExtra("FilterApply","False");
                } else {
                    intent.putExtra("FilterApply","True");
                }
                finish();
                startActivity(intent);
            }
        });
    }


    public void GetGroup() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetCategoryGroup";
        final SweetAlertDialog alert = (new SweetAlertDialog(FilterItems.this, SweetAlertDialog.PROGRESS_TYPE));
        alert.setContentText("Loading");
        alert.show();
        lists.clear();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert.dismissWithAnimation();
                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                jsbundle = jsonObject.getJSONArray("m_categoryMaster");

                                if (jsbundle.length() == 0) {
                                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Group not found").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        String catGroup = jsonobj.getString("catGroup");

                                        lists.add(new FilterList(catGroup));
                                    }

                                    filterAdapteradapter = new FilterAdapter(context, lists);
                                    lv_subtype.setAdapter(filterAdapteradapter);

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
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("m_categoryMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void GetCategory() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetCategoryName";
        final SweetAlertDialog alert = (new SweetAlertDialog(FilterItems.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsbundle = jsonObject.getJSONArray("m_categoryMaster");

                                if (jsbundle.length() == 0) {
                                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Category not found").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        String catName = jsonobj.getString("catName");
                                        lists.add(new FilterList(catName));
                                    }

                                    filterAdapteradapter = new FilterAdapter(context, lists);
                                    lv_subtype.setAdapter(filterAdapteradapter);
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
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("m_categoryMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void GetPcsperbox() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetPcsPerBox";
        final SweetAlertDialog alert = (new SweetAlertDialog(FilterItems.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsbundle = jsonObject.getJSONArray("m_productMaster");

                                if (jsbundle.length() == 0) {
                                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Category not found").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        String pcsperbox = jsonobj.getString("pcsperbox");
                                        lists.add(new FilterList(pcsperbox));
                                    }

                                    filterAdapteradapter = new FilterAdapter(context, lists);
                                    lv_subtype.setAdapter(filterAdapteradapter);
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
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("m_productMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void GetSize() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetAllSize";
        final SweetAlertDialog alert = (new SweetAlertDialog(FilterItems.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsbundle = jsonObject.getJSONArray("m_productMaster");

                                if (jsbundle.length() == 0) {
                                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Category not found").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        String size = jsonobj.getString("size");
                                        lists.add(new FilterList(size));
                                    }

                                    filterAdapteradapter = new FilterAdapter(context, lists);
                                    lv_subtype.setAdapter(filterAdapteradapter);
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
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("m_productMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

    public void GetStyleItemName() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "localhost/OrderEntry/GetStyleNoItemName";
        final SweetAlertDialog alert = (new SweetAlertDialog(FilterItems.this, SweetAlertDialog.PROGRESS_TYPE));
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

                                jsbundle = jsonObject.getJSONArray("m_productMaster");

                                if (jsbundle.length() == 0) {
                                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Category not found").show();
                                } else {

                                    for (int i = 0; i < jsbundle.length(); i++) {
                                        JSONObject jsonobj = new JSONObject(jsbundle.get(i).toString());
                                        String styleno = jsonobj.getString("styleno");
                                        lists.add(new FilterList(styleno));
                                    }

                                    filterAdapteradapter = new FilterAdapter(context, lists);
                                    lv_subtype.setAdapter(filterAdapteradapter);

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
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 502) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.SUCCESS_TYPE).setContentText(errorString).show();
                }
            } else if (error.networkResponse.statusCode == 403) {
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        jsbundle = jsonObject.getJSONArray("m_productMaster");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
                }
            } else
                new SweetAlertDialog(FilterItems.this, SweetAlertDialog.WARNING_TYPE).setContentText(response.toString()).show();
            alert.dismissWithAnimation();
        });
        queue.add(stringRequest);
    }

}