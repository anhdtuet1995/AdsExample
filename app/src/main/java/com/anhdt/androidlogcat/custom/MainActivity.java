package com.anhdt.androidlogcat.custom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.anhdt.androidlogcat.BuildConfig;
import com.anhdt.androidlogcat.MainAdapter;
import com.anhdt.androidlogcat.R;

import vcc.viv.ads.bin.AdsLogger;
import vcc.viv.ads.bin.AdsManager;
import vcc.viv.ads.bin.AdsManagerCallback;
import vcc.viv.ads.bin.InitializeParameter;
import vcc.viv.ads.bin.adsenum.AdsBrowser;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    ViewPager viewPager;
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        viewPager = findViewById(R.id.viewpager);
        mainAdapter = new MainAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(10);

        InitializeParameter parameter = new InitializeParameter().setCore(this, "AppId", BuildConfig.VERSION_CODE + "")                    // required
                .setLogger(true, AdsLogger.Level.verbose, "AdsSDK")   // optional
                .setWebBrowser(AdsBrowser.inapp, null); // optional
        AdsManager adsManager = AdsManager.getInstance();
        adsManager.callbackRegister(TAG, new AdsManagerCallback() {
            @Override
            public void initSuccess() {
                super.initSuccess();
                runOnUiThread(() -> viewPager.setAdapter(mainAdapter));
            }
        });
        adsManager.initialize(parameter);
    }
}