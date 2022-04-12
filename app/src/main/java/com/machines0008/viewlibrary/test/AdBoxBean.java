package com.machines0008.viewlibrary.test;

import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;

import com.machines0008.viewlibrary.adbox.AdBoxView;
import com.machines0008.viewlibrary.adbox.AdBoxVo;

/**
 * Created by Android Studio
 * User   : Andy
 * Date   : 2022/3/26
 * Time  : 上午 01:21
 * Usage :
 * To change this template use File | Settings | File and Code Templates.
 */
public class AdBoxBean implements AdBoxVo {
    private AdBoxView.AdType adType = AdBoxView.AdType.DRAWABLE;
    @DrawableRes private int drawableRes;
    private Bitmap bitmap;
    private String imageUrl;


    public void setAdType(AdBoxView.AdType adType) {
        this.adType = adType;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public AdBoxView.AdType getType() {
        return adType;
    }

    @Override
    public int getDrawableRes() {
        return drawableRes;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}
