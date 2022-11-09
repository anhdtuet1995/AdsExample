package com.anhdt.androidlogcat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.anhdt.androidlogcat.databinding.FragmentBlankBinding;

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


public class BlankFragment extends Fragment {
    private AdsManager adsManager;
    private FragmentBlankBinding binding;
    private int index = 0;
    private String TAG = BlankFragment.class.getSimpleName();

    public BlankFragment(int index) {
        this.index = index;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBlankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdsSdk(this, "anhdt", "anhdt@gmail.com", "0123456789", "anhdt", "anhdt");

    }

    public void initAdsSdk(BlankFragment activity, String name, String email, String phone, String address, String sessionId) {
        adsManager = AdsManager.getInstance();

        adsManager.callbackRegister(TAG + index, new AdsManagerCallback() {
            @Override
            public void requestAdsSuccess(String id, String requestId, List<AdsManager.AdsInfo> adsInfo) {
                super.requestAdsSuccess(id, requestId, adsInfo);
                if (!(TAG + index).equals(id)) return;
                AdsData info = adsManager.addAds(AdsForm.inPage, binding.root, TAG + index, requestId, adsInfo.get(0).zoneId);
                Log.d(TAG + index, "requestAdsSuccess: ");
            }

            @Override
            public void loadAdsFinish(String s, String s1, String s2) {
                super.loadAdsFinish(s, s1, s2);
                if (!(TAG + index).equals(s)) return;
                Log.d(TAG + index, "loadAdsFinish: ");
            }
        });

        List<String> positions = new ArrayList<String>() {{
            add("1");
        }};
        AdsRequest.ReaderParameter requestParameter = new AdsRequest.ReaderParameter("1", new ArrayList<Zone>() {{
            add(new Zone("2027132", Zone.AdsType.inpage));
        }}, positions, "https://app.kenh14.vn/home", "https://app.kenh14.vn/home");
        adsManager.request(TAG + index, "1", requestParameter);
    }
}