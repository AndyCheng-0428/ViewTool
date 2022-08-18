package com.machines0008.viewlibrary.ios;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

import com.machines0008.viewlibrary.R;

import lombok.Data;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/18
 * Usage:
 **/
public class DialogController {

    @Data
    public static class Params {
        private boolean cancelable = false;

        private CharSequence title;
        private float titleTextSize = 16f;
        private int titleMarginTop = 24;
        private int titleMarginBottom = 0;

        private CharSequence message;
        private float messageTextSize = 16f;
        private int messageMarginTop = 0;
        private int messageMarginBottom = 24;

        private int btnLineMinHeight = 48;
        private CharSequence positiveBtnText;
        @ColorRes
        private int positiveBtnTextColor = R.color.light_blue;
        private float positiveTextSize = 16f;
        private DialogInterface.OnClickListener positiveBtnListener;

        private CharSequence negativeBtnText;
        @ColorRes
        private int negativeBtnTextColor = R.color.light_blue;
        private float negativeTextSize = 16f;
        private DialogInterface.OnClickListener negativeBtnListener;
    }
}
