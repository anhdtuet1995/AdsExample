package com.anhdt.androidlogcat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anhdt.androidlogcat.databinding.ActivityInpageBinding;
import com.anhdt.androidlogcat.databinding.ActivityMain2Binding;
import com.anhdt.androidlogcat.databinding.ItemAdsBinding;
import com.anhdt.androidlogcat.databinding.ItemEmptyBinding;
import com.anhdt.androidlogcat.databinding.ItemFakeBinding;

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

public class MainActivity2 extends AppCompatActivity {

    private String TAG = MainActivity2.class.getSimpleName();
    /**
     * Nếu trên 1 màn hình mỗi zoneid chỉ có hiển thị ở 1 nơi duy nhất thì tham số này không quan trọng, fix 1 String bất kì nhưng không empty  hoặc null
     */
    private String requestId = "1";

    /**
     * Mã quảng cáo được cấp. Cái này check với bên hỗ trợ tích hợp quảng cáo
     */
    private String zoneId = "2027359";
    private String zoneInPageId = "2027132";

    /**
     * Đây là id user, cái này tùy thuộc vào server trả ra là gì, nếu không có thì truyền empty l
     */
    private String userId = "userId_1000223";

    private ActivityMain2Binding binding;
    private AdsManager adsManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2);

        initAdsSdk(this, "anhdt", "anhdt@gmail.com", "0123456789", "anhdt", "anhdt");

        binding.list.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(this, new ItemClickListener() {
            @Override
            public void onEmptyClick(MotionEvent motionEvent) {
                AdsManager.getInstance().click(motionEvent, TAG, requestId, zoneInPageId);
            }
        });
        binding.list.setAdapter(adapter);
    }

    public void initAdsSdk(Activity activity, String name, String email, String phone, String address, String sessionId) {
        InitializeParameter parameter = new InitializeParameter().setCore(activity, "AppId", BuildConfig.VERSION_CODE + "")                    // required
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
                    add(new Zone(zoneInPageId));
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
                        } else if (zoneInPageId.equals(item.zoneId)) {
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

    public interface ItemClickListener {
        void onEmptyClick(MotionEvent motionEvent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EmptyViewHolder extends ViewHolder {
        public ItemEmptyBinding binding;

        EmptyViewHolder(ItemEmptyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class AdsViewHolder extends ViewHolder {
        public ItemAdsBinding binding;

        AdsViewHolder(ItemAdsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ItemViewHolder extends ViewHolder {
        public ItemFakeBinding binding;

        ItemViewHolder(ItemFakeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int ITEM_COUNT = 30;

        private final int ADS_POSITION = 1;
        private final int EMPTY_POSITION = 10;

        private final int ADS_TYPE = 0;
        private final int EMPTY_TYPE = 1;
        private final int ITEM_TYPE = 2;

        private List<String> data;
        private LayoutInflater inflate;
        private ItemClickListener callback;

        MyAdapter(Context context, ItemClickListener callback) {
            this.inflate = LayoutInflater.from(context);
            this.callback = callback;
            this.data = new ArrayList() {{
                for (int i = 0; i < ITEM_COUNT; i++) {
                    add(i);
                }
            }};
        }

        @Override
        public int getItemViewType(int position) {
            if (position == ADS_POSITION) return ADS_TYPE;
            else if (position == EMPTY_POSITION) return EMPTY_TYPE;
            else return ITEM_TYPE;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ADS_TYPE) {
                return new AdsViewHolder(ItemAdsBinding.inflate(inflate, parent, false));
            } else if (viewType == EMPTY_TYPE) {
                return new EmptyViewHolder(ItemEmptyBinding.inflate(inflate, parent, false));
            } else {
                return new ItemViewHolder(ItemFakeBinding.inflate(inflate, parent, false));
            }
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder instanceof EmptyViewHolder) {
                ItemEmptyBinding binding = ((EmptyViewHolder) holder).binding;
                binding.layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        callback.onEmptyClick(motionEvent);
                        return true;
                    }
                });
            } else if (holder instanceof AdsViewHolder) {
                ItemAdsBinding binding = ((AdsViewHolder) holder).binding;
                AdsData info = adsManager.addAds(binding.adsLayout, TAG, requestId, zoneId);
            } else {
                ItemFakeBinding binding = ((ItemViewHolder) holder).binding;
                binding.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
            }
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}