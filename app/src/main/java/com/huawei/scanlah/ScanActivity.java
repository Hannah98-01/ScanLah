package com.huawei.scanlah;

import static com.huawei.hms.hmsscankit.ScanUtil.RESULT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class ScanActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SCAN = 111;
    private static final int DEFAULT_VIEW = 222 ;
    TextView result_url;
    TextView result_title;
    Button btnResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //Banner Ads Code Start here
        BannerView bannerView = new BannerView(this);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_360_57);
        ConstraintLayout constraintLayout = findViewById(R.id.ad_frame);
        constraintLayout.addView(bannerView);
        bannerView.setBannerRefresh(30);
        // Create an ad request to load an ad.
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);
        //Banner Ads Code End here

        result_url = findViewById(R.id.result);
        result_title = findViewById(R.id.resultTitle);
        btnResult = findViewById(R.id.btnResult);
    }

    public void newViewBtnClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    DEFAULT_VIEW);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFAULT_VIEW) {
            //After applying for permission, call the DefaultView scan code interface
            ScanUtil.startScan(ScanActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //When the scan code page is over, process the scan code result
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        //From the data returned from onActivityResult, use ScanUtil.RESULT as the key value to get the HmsScan return value

        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    result_title.setVisibility(View.VISIBLE);
                    result_url.setVisibility(View.VISIBLE);
                    result_url.setTextColor(getColor(R.color.black));
                    result_url.setText(((HmsScan) obj).getOriginalValue());
                    btnResult.setVisibility(View.VISIBLE);
                    btnResult.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {
                            Uri webpage = Uri.parse(((HmsScan) obj).getOriginalValue());
                            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    });

                }
                return;
            }
        }
    }
}