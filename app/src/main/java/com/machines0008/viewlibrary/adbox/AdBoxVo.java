package com.machines0008.viewlibrary.adbox;

import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * Created by Android Studio
 * User   : Andy
 * Date   : 2022/3/27
 * Time  : 上午 01:17
 * Usage :
 * To change this template use File | Settings | File and Code Templates.
 */
public interface AdBoxVo {
    @NonNull
    AdBoxView.AdType getType();

    @DrawableRes int getDrawableRes();

    String getImageUrl();

    Bitmap getBitmap();

    void setBitmap(Bitmap bitmap);

}
