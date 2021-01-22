package com.example.sqlitedatabase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlitedatabase.Helper.ProductMasterSync;
import com.example.sqlitedatabase.Helper.SQLiteLogin;
import com.example.sqlitedatabase.Helper.WebService;
import com.example.sqlitedatabase.Login.SwitchBuyer;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SplashActivity extends AppCompatActivity {

    private static final String NAMESPACE = "http://tempuri.org/";
    private static final int UI_ANIMATION_DELAY = 3000;
    private final Handler mHideHandler = new Handler();
    public static String myIP = "";
    public static String URL = "";
    TextView version;
    public static String myVersionName;
    public int MY_REQUEST_CODE = 100;
    AppUpdateManager appUpdateManager;
    Task<AppUpdateInfo> appUpdateInfoTask;
    InstallStateUpdatedListener listener;

    /*private PackageInfo packageInfo;
    TextView versioncode;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;*/
    String DbVersion = "";



    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        version = findViewById(R.id.text);
        myIP = "localhost";
        URL = "localhost";

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
            version.setText("Version" + myVersionName);
        }


        /*versioncode = findViewById(R.id.versioncode);
        versioncode.setText("Current Version Code: "+ getVersionCode());

        HashMap<String, Object> defaultRate = new HashMap<>();
        defaultRate.put("New_VersionCode",String.valueOf(getVersionCode()));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultRate);*/

        appUpdateManager = AppUpdateManagerFactory.create(context);

        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        checkPermission();
    }


    int PERMISSION_ALL = 1;
    private void checkPermission() {
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            Toast.makeText(this, "Please Allow All Permission To Work App Smoothly toast 3", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else{
            (new checkApptoUpdate()).execute(getResources().getString(R.string.app_name));
        }
    }

    public class checkApptoUpdate extends AsyncTask<String, String, String> {

        SweetAlertDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            p.setTitleText("Loading Please Wait...");
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] a = {"appname"};
            String[] b = {params[0]};
            String methodname = "CheckAppToUpdate";
            String URL = "localhost";
            return WebService.WebServiceCall(a, b, methodname, NAMESPACE, URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();

            try {
                if (result.isEmpty()) {
                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Sorry!....No AppInfo Available For This Application..Please Update App In PlayStore or Contact IT-Team").show();
                } else if (result.equals("false")) {
                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Sorry!....No AppInfo Available For This Application..Please Update App In PlayStore or Contact IT-Team").show();
                } else {
                    String[] splitrows = result.split(";");
                    DbVersion = splitrows[1];

                    if (!DbVersion.equalsIgnoreCase(myVersionName)) {
                        showUpdateDialog();
                    } else {
                        (new check()).execute(myVersionName, getResources().getString(R.string.app_name));
                    }
                }
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        int permissions_count = permissions.length;
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    permissions_count--;
                }
            }
        }
        if (permissions_count == 0)
            return true;
        else
            return false;
    }

    public void checkupdate() {

        listener = state -> {

            if (state.installStatus() == InstallStatus.DOWNLOADING) {
                long bytesDownloaded = state.bytesDownloaded();
                long totalBytesToDownload = state.totalBytesToDownload();

            } else if (state.installStatus() == InstallStatus.INSTALLED)

                appUpdateManager.unregisterListener(listener);

        };

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {

                    appUpdateManager.registerListener(listener);
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,this,MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else{
                (new check()).execute(myVersionName, getResources().getString(R.string.app_name));
            }

        });
        appUpdateInfoTask.addOnFailureListener(Errorinfo -> {
            Errorinfo.printStackTrace();
            (new check()).execute(myVersionName, getResources().getString(R.string.app_name));
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length >= 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Please Allow All Permission To Work App Smoothly", Toast.LENGTH_LONG).show();
                checkPermission();
            }
        } /*else {
            Toast.makeText(this, "Please Allow All Permission To Work App Smoothly toast2", Toast.LENGTH_LONG).show();
        }*/
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(3000);
    }

    private void hide() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    public class check extends AsyncTask<String, String, String> {

        SweetAlertDialog bar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new SweetAlertDialog(SplashActivity.this,SweetAlertDialog.PROGRESS_TYPE);
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
                if (result.contains("false")) {

                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("No AppInfo Available For This Application..Please Update App or Contact IT-Team").show();

                } else {
                    String[] splitrows = result.split(";");
                    String Status = splitrows[2];
                    String Life = splitrows[4];
                    int ExpireDayCount = Integer.parseInt(splitrows[5]);
                    if (Status.equalsIgnoreCase("true")) {
                        if (Life.equalsIgnoreCase("false")) {

                            new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("Your Application Is Expired").show();

                        } else {
                            if (ExpireDayCount < 5) {
                                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Your Application going To Expire within " + splitrows[5] + " Days").show();
                                openactivity();
                            } else if (ExpireDayCount < 0) {
                                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("Your Application  Expired please update new one").show();

                            } else {
                                openactivity();
                            }
                        }
                    } else if (Status.equalsIgnoreCase("false")) {
                        new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setContentText("Your Application Currently Stopped").show();

                    }
                }
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
    }

    public void openactivity() {

        SQLiteLogin db = new SQLiteLogin(SplashActivity.this);

        String s = db.getuser();

        if (!s.isEmpty()) {
            Intent intent = new Intent(SplashActivity.this, SwitchBuyer.class);
            intent.putExtra("appuser", s);
            intent.putExtra("userid",  db.getuserid());
            intent.putExtra("name",  db.getusername());
            startActivity(intent);
        } else {
            Intent intent1 = new Intent(SplashActivity.this, ProductMasterSync.class);
            startActivity(intent1);
        }
    }

    private void showUpdateDialog(){

        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Update")
                .setMessage("This version is Expire, Please Update to new version")
                .setPositiveButton("Update",null)
                .setNegativeButton("Later",null)
                .show();
        dialog.setCancelable(false);

        Button positivbutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativebutton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        negativebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new check()).execute(myVersionName, getResources().getString(R.string.app_name));
            }
        });

        positivbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkupdate();
                /*try {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+"com.example.sqlitedatabase")));
                }
                catch (android.content.ActivityNotFoundException anfe){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="+"com.example.sqlitedatabase")));
                }*/
            }
        });
    }

}
