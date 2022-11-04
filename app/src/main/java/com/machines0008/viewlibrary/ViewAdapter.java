package com.machines0008.viewlibrary;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Dimension;
import androidx.databinding.BindingAdapter;

import com.machines0008.viewlibrary.wheelview.WheelView;
import com.machines0008.viewlibrary.wheelview.WheelViewVo;

import java.util.List;

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

    @BindingAdapter("android:textColor")
    public static void setTextColor(TextView textView, @ColorRes int colorRes) {
        textView.setTextColor(textView.getContext().getColor(colorRes));
    }

    @BindingAdapter(value = {"android:layout_height", "heightDimension"})
    public static void setLayoutHeight(View view, int layoutHeight, int heightDimension) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (heightDimension == TypedValue.COMPLEX_UNIT_PX) {
            layoutParams.height = layoutHeight;
        } else if (heightDimension == TypedValue.COMPLEX_UNIT_DIP) {
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, layoutHeight, view.getContext().getResources().getDisplayMetrics());
        }
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter(value = {"android:layout_width", "widthDimension"})
    public static void setLayoutWidth(View view, int layoutWidth, int widthDimension) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (widthDimension == TypedValue.COMPLEX_UNIT_PX) {
            layoutParams.width = layoutWidth;
        } else if (widthDimension == TypedValue.COMPLEX_UNIT_DIP) {
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, layoutWidth, view.getContext().getResources().getDisplayMetrics());
        }
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, int layoutWidth) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, layoutWidth, view.getContext().getResources().getDisplayMetrics());
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, int layoutHeight) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, layoutHeight, view.getContext().getResources().getDisplayMetrics());
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("app:wheelItems")
    public static <T extends WheelViewVo> void setWheelItems(WheelView<T> wheelView, List<T> wheelItems) {
        wheelView.setDataList(wheelItems, null);
    }

    @BindingAdapter("app:defaultItem")
    public static <T extends WheelViewVo> void setWheelDefaultItem(WheelView<T> wheelView, T defaultItem) {
        wheelView.setSelectedItem(defaultItem);
    }

    @BindingAdapter("app:onSelect")
    public static <T extends WheelViewVo> void setOnSelectListener(WheelView<T> wheelView, WheelView.OnSelectListener<T> listener) {
        wheelView.setOnSelectListener(listener);
    }
}