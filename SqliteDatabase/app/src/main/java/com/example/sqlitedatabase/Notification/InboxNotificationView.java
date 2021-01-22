package com.example.sqlitedatabase.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import com.example.sqlitedatabase.Helper.WebService;
import com.example.sqlitedatabase.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class InboxNotificationView extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";
    ListView lv_listview;
    public List<InboxViewList> listitems;
    public JSONArray jsonArray = null;
    public InboxViewAdapter adapter;
    String title,Category;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_notification_view);

        lv_listview = findViewById(R.id.lv_listview);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        Category = intent.getStringExtra("Category");
        userid = intent.getStringExtra("userid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.log);
        toolbar.setTitle(title);


        (new getInboxNotification()).execute(userid,Category,title);
    }


    public class getInboxNotification extends AsyncTask<String, String, String> {

        SweetAlertDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new SweetAlertDialog(InboxNotificationView.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading!..");
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userId","category","Title"};
            String[] values = {params[0],params[1],params[2]};
            String methodname = "S_GetNotifications";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            if (result.equals("no data found")) {

                Toast.makeText(InboxNotificationView.this, "Inbox is Empty", Toast.LENGTH_SHORT).show();

            } else {

                listitems = new ArrayList<>();
                try {
                    jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());

                            String MsgContent = jsonobj.getString("MsgContent");
                            String senddate = jsonobj.getString("SendTime");
                            String readtime = jsonobj.getString("ReadTime");

                            listitems.add(new InboxViewList(senddate,MsgContent,""));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter = new InboxViewAdapter(getApplicationContext(), listitems);
                    lv_listview.setAdapter(adapter);

                    (new U_readpending()).execute(userid,Category,title);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class U_readpending extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"userid","category","title"};
            String[] values = {params[0],params[1],params[2]};
            String methodname = "U_NotificationReadTitle";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }
}