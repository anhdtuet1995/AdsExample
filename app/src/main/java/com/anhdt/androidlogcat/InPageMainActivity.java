package com.anhdt.androidlogcat;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anhdt.androidlogcat.databinding.ActivityInpageBinding;
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

public class InPageMainActivity extends AppCompatActivity {

    private String TAG = InPageMainActivity.class.getSimpleName();
    /**
     * Nếu trên 1 màn hình mỗi zoneid chỉ có hiển thị ở 1 nơi duy nhất thì tham số này không quan trọng, fix 1 String bất kì nhưng không empty  hoặc null
     */
    private String requestId = "1";

    /**
     * Mã quảng cáo được cấp. Cái này check với bên hỗ trợ tích hợp quảng cáo
     */
    private String zoneId = "2027360";

    /**
     * Đây là id user, cái này tùy thuộc vào server trả ra là gì, nếu không có thì truyền empty l
     */
    private String userId = "userId_1000223";

    private ActivityInpageBinding binding;
    private AdsManager adsManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inpage);

        initAdsSdk(this, "anhdt", "anhdt@gmail.com", "0123456789", "anhdt", "anhdt");

        binding.layerView1.setOnClickListener(v -> {
        });
        binding.layerView2.setOnClickListener(v -> {
        });
        binding.emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AdsManager.getInstance().click(event, TAG, requestId, zoneId);
                return true;
            }
        });

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

        // Đăng ký nhận callback về
        adsManager.callbackRegister(TAG, new AdsManagerCallback() {
            @Override
            public void initSuccess() {
                super.initSuccess();
                // Đây là url và channel bên báo của bên tích hợp
                String url = "https://docbao24h.me/tin-tuc";
                String channel = "https://docbao24h.me//home";

                // Gửi request lấy quảng cáo
                adsManager.request(TAG, requestId, new AdsRequest.ReaderParameter(userId, new ArrayList<Zone>() {{
                    add(new Zone(zoneId));
                }}, new ArrayList<String>() {{
                    // Fix cứng
                    add("1");
                }}, url, channel));
            }

            @Override
            public void requestAdsSuccess(String id, String requestId, List<AdsManager.AdsInfo> adsInfo) {
                super.requestAdsSuccess(id, requestId, adsInfo);
                if (!TAG.equals(id)) return;
                if (adsInfo == null) return;
                runOnUiThread(() -> {
                    for (AdsManager.AdsInfo item : adsInfo) {
                        ConstraintLayout constraintLayout = null;
                        if (item == null) {
                            continue;
                        } else if (zoneId.equals(item.zoneId)) {
                            constraintLayout = binding.adsLayout;
                        }
                        if (constraintLayout != null) {
                            AdsData info = adsManager.addAds(AdsForm.inPage, constraintLayout, TAG, requestId, item.zoneId);
                        }
                    }
                });
            }
        });
    }

}