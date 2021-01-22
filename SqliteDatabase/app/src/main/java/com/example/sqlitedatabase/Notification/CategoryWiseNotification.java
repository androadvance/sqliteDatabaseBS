package com.example.sqlitedatabase.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlitedatabase.Helper.WebService;
import com.example.sqlitedatabase.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CategoryWiseNotification extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";
    ListView lv_listview;
    EditText eT_filter;
    TextView tv_messageCount;
    public List<CategoryList> listitems,searchlistitems;
    public CategoryAdapter adapter,searchadapter;
    public JSONArray jsonArray = null;
    String category,sendtime;
    public int listposition;
    String pendingmsgCount,userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise_notification);

        lv_listview = findViewById(R.id.lv_listview);
        eT_filter = findViewById(R.id.et_filter);
        tv_messageCount = findViewById(R.id.tv_messageCount);

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.log);
        toolbar.setTitle("Category");

        Button btn_readall=new Button(this);
        btn_readall.setText("ReadAll");
        btn_readall.setBackgroundResource(R.color.grey_400);
        Toolbar.LayoutParams linearlayout = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        linearlayout.gravity= Gravity.END;
        btn_readall.setLayoutParams(linearlayout);
        toolbar.addView(btn_readall);

        searchlistitems = new ArrayList<>();

        (new getNotificationCount()).execute(userid);
        (new getInboxNotification()).execute(userid);


        btn_readall.setOnClickListener(v -> {

            (new U_notificationreadall()).execute(userid);
            (new getInboxNotification()).execute(userid);
            (new getNotificationCount()).execute(userid);


        });

        eT_filter.addTextChangedListener(new TextWatcher() {


            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchmessages();
            }
        });



        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listposition = position;

                Intent intent = new Intent(CategoryWiseNotification.this,InboxNotification.class);

                try {
                    if (searchlistitems.size() == 0) {
                        String category = listitems.get(listposition).getCategory();
                        intent.putExtra("Category", category);
                        intent.putExtra("userid", userid);

                    } else {
                        String category = searchlistitems.get(listposition).getCategory();
                        intent.putExtra("Category", category);
                        intent.putExtra("userid", userid);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });
    }

    public class getNotificationCount extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                    pendingmsgCount = jsonobj.getString("count");
                    String res = "(" + pendingmsgCount + ")";
                    tv_messageCount.setText(res);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class getInboxNotification extends AsyncTask<String, String, String> {

        SweetAlertDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new SweetAlertDialog(CategoryWiseNotification.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading!..");
            p.setCancelable(false);
            p.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userId"};
            String[] values = {params[0]};
            String methodname = "S_GetNotificationsCategoryWise";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            if (result.equals("no data found")) {

                Toast.makeText(CategoryWiseNotification.this, "Inbox is Empty", Toast.LENGTH_SHORT).show();

            } else {

                listitems = new ArrayList<>();
                try {
                    jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());

                            category = jsonobj.getString("category");
                            sendtime = jsonobj.getString("SendTime");
                            String isread = jsonobj.getString("IsRead");
                            String count = jsonobj.getString("noofunreadcount");
                            String title = jsonobj.getString("Title");
                            listitems.add(new CategoryList(category, sendtime, title, count, isread));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new CategoryAdapter(getApplicationContext(), listitems);
                    lv_listview.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class U_notificationreadall extends AsyncTask<String, String, String> {

        SweetAlertDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new SweetAlertDialog(CategoryWiseNotification.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading!..");
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userid"};
            String[] values = {params[0]};
            String methodname = "U_NotificationReadAll";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            if (result.equals("ok")) {

                Toast.makeText(CategoryWiseNotification.this, "Read All Message", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(CategoryWiseNotification.this, "failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchmessages() {

        Integer textlength = eT_filter.getText().length();

        if (textlength > 0) {

            String searchstr = eT_filter.getText().toString();
            searchlistitems = new ArrayList<>();
            searchlistitems.clear();

            for (int i = 0; i < listitems.size(); i++) {
                if (listitems.get(i).getCategory().contains(searchstr)
                        || listitems.get(i).getMessage().contains(searchstr)) {

                    searchlistitems.add(listitems.get(i));
                }
            }

            searchadapter = new CategoryAdapter(CategoryWiseNotification.this, searchlistitems);
            lv_listview.setAdapter(searchadapter);

        } else {
            searchlistitems.clear();
            lv_listview.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new getInboxNotification()).execute(userid);
        (new getNotificationCount()).execute(userid);

    }
}