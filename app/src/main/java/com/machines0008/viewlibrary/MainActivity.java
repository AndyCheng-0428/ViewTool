package com.machines0008.viewlibrary;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.machines0008.viewlibrary.adbox.AdBoxView;
import com.machines0008.viewlibrary.ios.CommonDialog;
import com.machines0008.viewlibrary.test.AdBoxBean;
import com.machines0008.viewlibrary.test.WheelBean;
import com.machines0008.viewlibrary.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdBoxView<AdBoxBean> adBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 300);
            return;
        }
        setContentView(R.layout.activity_main);
        initView();
        initWheel();
    }

    private void initWheel() {
        WheelView<WheelBean> wheelView = findViewById(R.id.wheelView);
        List<WheelBean> wheelBeans = new ArrayList<>();
        for (int i = 0; i < 100; i++) {

            WheelBean bean = new WheelBean();
            bean.setName(String.valueOf(i));
            wheelBeans.add(bean);
        }
        wheelView.setDataList(wheelBeans, null);

        new CommonDialog.Builder(this)
                .setTitle("你好啊")
                .setMessage("這是測試待而露格!")
                .setPositiveBtnClickListener(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeBtnClickListener(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .build()
                .show();
    }

    private void initView() {
        initAdBox();
    }

    private void initAdBox() {
        adBox = findViewById(R.id.adBox);
        List<AdBoxBean> list = new ArrayList<>();
        AdBoxBean ad3 = new AdBoxBean();
        ad3.setImageUrl("https://ws.kcg.gov.tw/001/KcgUploadFiles/263/relpic/7103/73550/0c555e66-59c6-4fe0-8522-2cd4657a26c6.jpg");
        ad3.setAdType(AdBoxView.AdType.NETWORK_URL);
        list.add(ad3);
        AdBoxBean ad2 = new AdBoxBean();
        ad2.setImageUrl("https://ws.kcg.gov.tw/001/KcgUploadFiles/263/relpic/7103/73225/54ce8b64-0a3f-4452-9b4f-06e41a7b25f3.jpg");
        ad2.setAdType(AdBoxView.AdType.NETWORK_URL);
        list.add(ad2);
        AdBoxBean ad1 = new AdBoxBean();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        ad1.setAdType(AdBoxView.AdType.BITMAP);
        ad1.setBitmap(bitmap);
        list.add(ad1);

//        AdBoxBean ad2 = new AdBoxBean();
//        ad2.setAdType(AdType.DRAWABLE);
//        ad2.setDrawableRes(R.drawable.ic_launcher_foreground);
//        list.add(ad2);


        adBox.setContent(list);
        adBox.setOnItemClickListener((v, adBoxVo) -> Log.i("MainActivity", "inner type" + adBoxVo.getType()));
    }
}