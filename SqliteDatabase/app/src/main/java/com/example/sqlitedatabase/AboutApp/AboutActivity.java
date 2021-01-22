package com.example.sqlitedatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.Helper.WebService;
import com.example.sqlitedatabase.R;
import com.example.sqlitedatabase.SplashActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AboutActivity extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";
    ListView lv_listview;
    private List<VersionList> versionlist;
    private VersionAdapter versionAdapter;
    public JSONArray jsonArray = null;
    TextView tv_title, tv_currentversion;
    public static String URL = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        URL = "http:";
        lv_listview = findViewById(R.id.lv_listview);
        tv_title = findViewById(R.id.tv_title);
        tv_currentversion = findViewById(R.id.tv_currentversion);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
            } else {
            }
        }

        //Storage Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        SpannableString content = new SpannableString("What's New");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_title.setText(content);

        tv_currentversion.setText("Currenrt Version - " + SplashActivity.myVersionName);

        (new getVersionUpdateList()).execute(getString(R.string.app_name));

    }

    public class getVersionUpdateList extends AsyncTask<String, String, String> {

        SweetAlertDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new SweetAlertDialog(AboutActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading Please Wait...");
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] a = {"appname"};
            String[] b = {params[0]};
            String methodname = "S_getVersionDetails";
            String URL = "http:";
            return WebService.WebServiceCall(a, b, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            try {
                jsonArray = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            versionlist = new ArrayList<>();

            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());

                        String Version = jsonObject.getString("Version");
                        String Remarks = jsonObject.getString("Remarks");
                        String Description = jsonObject.getString("Description");
                        String UpLoadedDate = jsonObject.getString("UpLoadedDate");

                        String version_date = Version + " - " + UpLoadedDate;

                        String[] res = Description.split("-");

                        /*for (String description : res) {
                            System.out.println(description);
                            Toast.makeText(AboutActivity.this, (CharSequence) description, Toast.LENGTH_SHORT).show();
                            versionlist.add(new VersionList(version_date, Remarks, description, getString(R.string.text_with_bullet)));
                        }*/

                        versionlist.add(new VersionList(version_date, Remarks, Description, getString(R.string.text_with_bullet)));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                versionAdapter = new VersionAdapter(versionlist, getApplicationContext());
                lv_listview.setAdapter(versionAdapter);

            } else {

                Toast.makeText(AboutActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}