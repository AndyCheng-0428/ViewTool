package com.machines0008.viewlibrary;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.databinding.BindingAdapter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/18
 * Usage:
 **/
public class ViewAdapter {
    @BindingAdapter("android:minHeight")
    public static void setMinHeight(View view, int minHeight) {
        view.setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minHeight, view.getContext().getResources().getDisplayMetrics()));
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View view, int marginTop) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        layoutParams.setMargins(layoutParams.leftMargin, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginTop, view.getContext().getResources().getDisplayMetrics()), layoutParams.rightMargin, layoutParams.bottomMargin);
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setLayoutMarginBottom(View view, int marginBottom) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginBottom, view.getContext().getResources().getDisplayMetrics()));
    }

    @BindingAdapter("android:textSize")
    public static void setTextSize(TextView textView, float textSize) {
        textView.setTextSize(Dimension.SP, textSize);
    }
}
