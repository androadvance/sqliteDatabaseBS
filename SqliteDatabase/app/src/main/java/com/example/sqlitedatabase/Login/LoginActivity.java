package com.example.sqlitedatabase.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sqlitedatabase.Helper.SQLiteLogin;
import com.example.sqlitedatabase.Helper.WebService;
import com.example.sqlitedatabase.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";
    EditText eT_username, eT_password;
    Button signin, loginusingotp;
    SQLiteLogin sqLiteLogin;
    public JSONArray jsonArray = null;
    public String myVersionName;
    TextView version;
    public static String myIP = "";
    public static String URL = "";
    TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myIP = "localhost";
        URL = "localhost";

        eT_username = findViewById(R.id.userid);
        eT_password = findViewById(R.id.passwordid);
        signin = findViewById(R.id.signid);
        loginusingotp = findViewById(R.id.otpid);
        version = findViewById(R.id.version);
        tv_name = findViewById(R.id.tv_name);

        sqLiteLogin = new SQLiteLogin(LoginActivity.this);

        Context context = getApplicationContext();


        if (WebService.IsNetWork(getApplicationContext())) {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            myVersionName = "not available"; // initialize String

            try {
                myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            version.setText("Version " + myVersionName);

            CountDownTimer aCounter = new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {

                    (new check()).execute(myVersionName, getResources().getString(R.string.app_name));

                }
            }.start();
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(eT_username.getText().toString())) {
                    eT_username.setError("Please Enter Username");
                } else if (TextUtils.isEmpty(eT_password.getText().toString())) {
                    eT_password.setError("Please Enter Password id");
                } else {
                    (new Login()).execute(eT_username.getText().toString(), eT_password.getText().toString());
                }
            }
        });


        loginusingotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eT_username.getText().toString().isEmpty()) {
                    eT_username.setError("Username should not empty");
                } else {
                    (new sendOTP()).execute(eT_username.getText().toString());
                }
            }
        });
    }


    public class Login extends AsyncTask<String, String, String> {

        SweetAlertDialog bar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            bar.setCancelable(false);
            bar.setContentText("Please Wait");
            bar.setCanceledOnTouchOutside(false);
            bar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"email", "password"};
            String[] values = {params[0], params[1]};
            String methodname = "checklogin";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            bar.dismiss();
            try {
                if (result.equals("Enter valid username/password")) {
                    Toast.makeText(LoginActivity.this, "Please enter valid username and password", Toast.LENGTH_SHORT).show();
                } else {
                    jsonArray = new JSONArray(result);
                    Intent intent = new Intent(LoginActivity.this, SwitchBuyer.class);
                    intent.putExtra("appuser", eT_username.getText().toString());
                    intent.putExtra("password", eT_password.getText().toString());
                    intent.putExtra("name", jsonArray.getJSONObject(0).getString("name"));
                    intent.putExtra("userid", jsonArray.getJSONObject(0).getString("userid"));
                    long h = sqLiteLogin.saveit(eT_username.getText().toString(), eT_password.getText().toString(), jsonArray.getJSONObject(0).getString("userid"),jsonArray.getJSONObject(0).getString("name"));
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class sendOTP extends AsyncTask<String, String, String> {

        SweetAlertDialog bar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            bar.setCancelable(false);
            bar.setContentText("Please Wait");
            bar.setCanceledOnTouchOutside(false);
            bar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"email"};
            String[] values = {params[0]};
            String methodname = "sendOTP";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            bar.dismiss();

            if (result.equals("EmailId not valid")) {

                Toast.makeText(LoginActivity.this, "Please Registered Your MailID", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonobj = new JSONObject(jsonArray.get(i).toString());
                        String otp = jsonobj.getString("OTP");
                        String otpdate = jsonobj.getString("OTPDate");
                        Intent intent = new Intent(LoginActivity.this, OtpVerify.class);
                        intent.putExtra("username", eT_username.getText().toString());
                        intent.putExtra("OTP", otp);
                        intent.putExtra("OTPdate", otpdate);
                        intent.putExtra("userid", jsonArray.getJSONObject(0).getString("userid"));
                        intent.putExtra("name", jsonArray.getJSONObject(0).getString("name"));
                        Toast.makeText(LoginActivity.this, "OTP send to your mobile number/Emailid", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class check extends AsyncTask<String, String, String> {

        SweetAlertDialog bar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            bar.setCancelable(false);
            bar.setContentText("Please Wait");
            bar.setCanceledOnTouchOutside(false);
            bar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"Version", "Appname"};
            String[] values = {params[0], params[1]};
            String methodname = "Appcheck";
            String URL = "localhost";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            bar.dismiss();
            try {
                if (result.equals("false")) {

                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("No AppInfo Available For This Application..Please Update App or Contact IT-Team").show();

                    loginusingotp.setEnabled(false);
                    signin.setEnabled(false);

                } else {
                    String[] splitrows = result.split(";");
                    String Status = splitrows[2];
                    String ExpiryDate = splitrows[3];
                    String Life = splitrows[4];
                    int ExpireDayCount = Integer.parseInt(splitrows[5]);
                    if (Status.equalsIgnoreCase("true")) {
                        if (Life.equalsIgnoreCase("false")) {
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("Your Application Is Expired").show();
                            loginusingotp.setEnabled(false);
                            signin.setEnabled(false);
                        } else {
                            if (ExpireDayCount < 5) {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Your Application going To Expire within " + splitrows[5] + " Days").show();

                            } else if (ExpireDayCount < 0) {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Your Application  Expired please update new one").show();
                                loginusingotp.setEnabled(false);
                                signin.setEnabled(false);
                            }
                        }
                    } else if (Status.equalsIgnoreCase("false")) {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setContentText("Your Application Currently Stopped").show();
                        loginusingotp.setEnabled(false);
                        signin.setEnabled(false);
                    }
                }
            } catch (Exception er) {
                er.printStackTrace();
            }
            if (bar.isShowing())
                bar.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}