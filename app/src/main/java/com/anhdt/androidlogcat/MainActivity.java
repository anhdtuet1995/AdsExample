package com.anhdt.androidlogcat;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anhdt.androidlogcat.databinding.ActivityMainBinding;

import org.json.JSONObject;

import vcc.viv.ads.bin.AdsLogger;
import vcc.viv.ads.bin.AdsManager;
import vcc.viv.ads.bin.InitializeParameter;
import vcc.viv.ads.bin.adsenum.AdsBrowser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AdsManager adsManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initAdsSdk(this, "anhdt", "anhdt@gmail.com", "0123456789", "anhdt", "anhdt");

        new Thread(() -> AndroidNetworking.get("https://api.github.com/users/anhdtuet1995")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("anh.dt2", "Test = " + response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("anh.dt2", "Test e = " + anError.getMessage());
                    }
                })).start();

    }

    public void initAdsSdk(Activity activity, String name, String email, String phone, String address, String sessionId) {
        InitializeParameter parameter = new InitializeParameter()
                .setCore(activity, "AppId", BuildConfig.VERSION_CODE + "")                    // required
                .setLogger(true, AdsLogger.Level.verbose, "AdsSDK")   // optional
                .setWebBrowser(AdsBrowser.inapp, null); // optional
        adsManager = AdsManager.getInstance();
        adsManager.initialize(parameter);
    }

}