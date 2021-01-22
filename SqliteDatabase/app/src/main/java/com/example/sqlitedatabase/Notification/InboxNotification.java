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

public class InboxNotification extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";
    ListView lv_listview;
    public List<TitleList> listitems, searchlistitems;
    public TitleAdapter adapter, searchadapter;
    public JSONArray jsonArray = null;
    public int listposition;
    String pendingmsgCount;
    EditText eT_filter;
    String Category,sendtime,userid;
    TextView tv_messageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_notification);

        lv_listview = findViewById(R.id.lv_listview);
        eT_filter = findViewById(R.id.et_filter);
        tv_messageCount = findViewById(R.id.tv_messageCount);

        Intent intent = getIntent();
        Category = intent.getStringExtra("Category");
        sendtime = intent.getStringExtra("sendtime");
        userid = intent.getStringExtra("userid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.log);
        toolbar.setTitle(Category);

        Button btn_readall=new Button(this);
        btn_readall.setText("ReadAll");
        btn_readall.setBackgroundResource(R.color.grey_400);
        Toolbar.LayoutParams linearlayout = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        linearlayout.gravity= Gravity.END;
        btn_readall.setLayoutParams(linearlayout);
        toolbar.addView(btn_readall);

        searchlistitems = new ArrayList<>();


        (new getInboxNotification()).execute(userid, Category);
        (new getNotificationCount()).execute(userid, Category);

        btn_readall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new U_notificationreadall()).execute(userid,Category);
                (new getInboxNotification()).execute(userid, Category);
                (new getNotificationCount()).execute(userid, Category);
            }
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

                Intent intent1 = new Intent(InboxNotification.this,InboxNotificationView.class);

                if (searchlistitems.size() == 0) {

                    String title = listitems.get(listposition).getTitle();
                    intent1.putExtra("title", title);
                    intent1.putExtra("Category", Category);
                    intent1.putExtra("userid", userid);

                } else {
                    String title = searchlistitems.get(listposition).getTitle();
                    String category = searchlistitems.get(listposition).getTitle();
                    intent1.putExtra("title", title);
                    intent1.putExtra("Category", Category);
                    intent1.putExtra("userid", userid);
                    finish();
                }

                startActivity(intent1);

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
            String[] paras = {"userid", "category"};
            String[] values = {params[0], params[1]};
            String methodname = "S_GetNotifications_TitlePendingsCount";
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
            p = new SweetAlertDialog(InboxNotification.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading!..");
            p.setCancelable(false);
            p.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userId", "category"};
            String[] values = {params[0], params[1]};
            String methodname = "S_GetNotificationsTitleWise";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            if (result.equals("no data found")) {

                Toast.makeText(InboxNotification.this, "Inbox is Empty", Toast.LENGTH_SHORT).show();

            } else {

                listitems = new ArrayList<>();

                try {
                    jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());

                            String title = jsonobj.getString("Title");
                            String msgcontent = jsonobj.getString("MsgContent");
                            String sendtime = jsonobj.getString("SendTime");
                            String isread = jsonobj.getString("IsRead");
                            String count = jsonobj.getString("noofunreadcount");

                            listitems.add(new TitleList(title, msgcontent, sendtime, isread, count));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new TitleAdapter(getApplicationContext(), listitems);
                    lv_listview.setAdapter(adapter);

                    searchmessages();

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
            p = new SweetAlertDialog(InboxNotification.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading!..");
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userid", "category"};
            String[] values = {params[0], params[1]};
            String methodname = "U_NotificationReadCategory";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            if (result.equals("ok")) {

                Toast.makeText(InboxNotification.this, "Read All Message", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(InboxNotification.this, "failed", Toast.LENGTH_SHORT).show();
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
                if (listitems.get(i).getTitle().contains(searchstr)
                        || listitems.get(i).getContent().contains(searchstr)
                ) {
                    searchlistitems.add(listitems.get(i));
                }
            }
            searchadapter = new TitleAdapter(InboxNotification.this, searchlistitems);
            lv_listview.setAdapter(searchadapter);
        } else {
            searchlistitems.clear();
            lv_listview.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new getInboxNotification()).execute(userid, Category);
        (new getNotificationCount()).execute(userid, Category);
    }
}