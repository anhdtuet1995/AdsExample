package com.anhdt.androidlogcat;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anhdt.androidlogcat.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vcc.viv.ads.bin.AdsData;
import vcc.viv.ads.bin.AdsLogger;
import vcc.viv.ads.bin.AdsManager;
import vcc.viv.ads.bin.AdsManagerCallback;
import vcc.viv.ads.bin.AdsRequest;
import vcc.viv.ads.bin.InitializeParameter;
import vcc.viv.ads.bin.Zone;
import vcc.viv.ads.bin.adsenum.AdsBrowser;
import vcc.viv.ads.bin.adsenum.AdsForm;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    /**
     * Nếu trên 1 màn hình mỗi zoneid chỉ có hiển thị ở 1 nơi duy nhất thì tham số này không quan trọng, fix 1 String bất kì nhưng không empty  hoặc null
     */
    private String requestId = "1";
    private String zoneId = "2026943";

    private String userId = "userId_1000223";

    private ActivityMainBinding binding;
    private AdsManager adsManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initAdsSdk(this, "anhdt", "anhdt@gmail.com", "0123456789", "anhdt", "anhdt");

        new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.get("https://api.github.com/users/anhdtuet1995")
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
                        });
            }
        }).start();
    }

    public void initAdsSdk(Activity activity, String name, String email, String phone, String address, String sessionId) {
        InitializeParameter parameter = new InitializeParameter()
                .setCore(activity, "AppId", BuildConfig.VERSION_CODE + "")                    // required
                .setLogger(true, AdsLogger.Level.verbose, "AdsSDK")   // optional
                .setWebBrowser(AdsBrowser.inapp, null); // optional
        adsManager = AdsManager.getInstance();
        adsManager.initialize(parameter);

        // Đăng ký nhận callback về màn hình
        adsManager.callbackRegister(TAG, new AdsManagerCallback() {
            @Override
            public void initSuccess() {
                super.initSuccess();
                // Gửi request lấy quảng cáo
                adsManager.request(TAG, requestId, new AdsRequest.ReaderParameter(userId, new ArrayList<Zone>() {{
                    add(new Zone(zoneId));
                }}, new ArrayList<String>() {{
                    add("1");
                }}, "https://app.kenh14.vn/home&v=5.1.51", "https://app.kenh14.vn/home"));
            }

            @Override
            public void requestAdsSuccess(String id, String requestId, List<AdsManager.AdsInfo> adsInfo) {
                super.requestAdsSuccess(id, requestId, adsInfo);
                if (!TAG.equals(id)) return;
                runOnUiThread(() -> {
                    ConstraintLayout constraintLayout = findViewById(R.id.ads_parent);
                    // Add quảng cáo vào view
                    AdsData info = adsManager.addAds(AdsForm.normal, constraintLayout, TAG, requestId, adsInfo.get(0).zoneId);
                });
            }
        });
    }

}