package com.example.sqlitedatabase.Login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtpVerify extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";

    EditText eT_otp;
    Button resendotp, confirmotp;
    SQLiteLogin sqLiteLogin;
    CountDownTimer countDownTimer;
    TextView timer;
    TextView addtime, subtime;
    public JSONArray jsonArray = null;
    String username = "", otp = "", otpdate = "",userid = "",name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);


        eT_otp = findViewById(R.id.otpid);
        resendotp = findViewById(R.id.resendid);
        confirmotp = findViewById(R.id.confirmid);
        timer = findViewById(R.id.timerid);
        addtime = findViewById(R.id.timeridd);
        subtime = findViewById(R.id.timeriddd);

        final Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        otp = intent.getExtras().getString("OTP");
        otpdate = intent.getExtras().getString("OTPdate");
        userid = intent.getExtras().getString("userid");
        name = intent.getExtras().getString("name");

        sqLiteLogin = new SQLiteLogin(OtpVerify.this);

        countDownTimer = new CountDownTimer(180000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText(millisUntilFinished / 1000 + " " + "sec left");

            }

            public void onFinish() {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                addtime.setText(currentDateandTime);

                String date3 = addtime.getText().toString();
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date newDates1 = null;
                try {
                    newDates1 = sdf2.parse(date3);
                    newDates1.setTime(System.currentTimeMillis() + (6 * 6 * 3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String currentDateandTime1 = sdf2.format(newDates1);
                subtime.setText(currentDateandTime1);

                timer.setText("Your OTP Expiry..Please Select Resend OTP");

                eT_otp.setEnabled(false);
                confirmotp.setEnabled(false);
                eT_otp.setText("");

                new SweetAlertDialog(OtpVerify.this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Your OTP Expiry....Please Select Resend OTP").show();
            }

        }.start();


        confirmotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eT_otp.getText().toString().isEmpty()) {

                    eT_otp.setError("Please Enter OTP");

                } else {

                    (new VerifyOTP()).execute(username, eT_otp.getText().toString());

                }
            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new ResendOTP()).execute(username);

            }
        });
    }


    public class ResendOTP extends AsyncTask<String, String, String> {

        SweetAlertDialog bar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new SweetAlertDialog(OtpVerify.this,SweetAlertDialog.PROGRESS_TYPE);
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

                Toast.makeText(OtpVerify.this, "Please Registered Your MailID", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(OtpVerify.this, "OTP send to your mobile number/Emailid", Toast.LENGTH_SHORT).show();

                countDownTimer.start();
                eT_otp.setFocusable(true);
                eT_otp.setEnabled(true);
                eT_otp.setCursorVisible(true);
                eT_otp.isEnabled();
                confirmotp.setEnabled(true);

            }
        }
    }


    public class VerifyOTP extends AsyncTask<String, String, String> {

        SweetAlertDialog bar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new SweetAlertDialog(OtpVerify.this,SweetAlertDialog.PROGRESS_TYPE);
            bar.setCancelable(false);
            bar.setContentText("Please Wait");
            bar.setCanceledOnTouchOutside(false);
            bar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] paras = {"email", "otp"};
            String[] values = {params[0], params[1]};
            String methodname = "verifyOTP";
            String URL = "https://bennyhillsindia.com/seller/SellerLogin.asmx";
            return WebService.WebServiceCall(paras, values, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            bar.dismiss();

            try {
                jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    String res = jsonObject.getString("result");
                    //   String userid = jsonObject.getString("userid");
                    if (res.equals("ok")) {
                        Intent intent1 = new Intent(OtpVerify.this, SwitchBuyer.class);
                        intent1.putExtra("appuser",username);
                        intent1.putExtra("userid",userid);
                        intent1.putExtra("name",name);
                        long h = sqLiteLogin.saveit(username, "otp",userid,name);
                        Toast.makeText(OtpVerify.this, "Login Sucess", Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                    } else {
                        Toast.makeText(OtpVerify.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
