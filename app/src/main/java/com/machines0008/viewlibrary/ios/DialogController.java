package com.machines0008.viewlibrary.ios;

import android.content.DialogInterface;

import androidx.annotation.ColorRes;

import com.machines0008.viewlibrary.R;
import com.machines0008.viewlibrary.wheelview.DateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.Data;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/18
 * Usage:
 **/
public class DialogController {

    /**
     * 使用日期選擇彈窗，將使用者未撰寫之資料填充
     * ex.開始日期、結束日期、預設日期
     *
     * @param params
     */
    static void fillDatePicker(Params params) {
        if (null == params.getStartTime()) {
            Calendar startTime = Calendar.getInstance();
            startTime.set(1900, 0, 1);
            params.setStartTime(startTime);
        }
        if (null == params.getEndTime()) {
            Calendar endTime = Calendar.getInstance();
            endTime.set(2100, 11, 31);
            params.setEndTime(endTime);
        }
        if (null == params.getDefaultTime()) {
            Calendar current = Calendar.getInstance();
            params.setDefaultTime(current);
        }
        if (params.getStartTime().after(params.getEndTime())) {
            Calendar temp = params.getStartTime();
            params.setStartTime(params.getEndTime());
            params.setEndTime(temp);
        }
        Calendar startTime = params.getStartTime();
        Calendar endTime = params.getEndTime();
        int startYear = startTime.get(Calendar.YEAR);
        int endYear = endTime.get(Calendar.YEAR);
        params.yearList = new ArrayList<>(endYear - startYear + 1);
        for (int year = startYear; year <= endYear; year++) {
            DateBean dateBean = new DateBean(DateBean.FORMAT_YEAR, year, "", "年");
            params.yearList.add(dateBean);
        }
        params.monthList = new ArrayList<>(12);
        for (int month = 1; month < 12; month++) {
            DateBean dateBean = new DateBean(DateBean.FORMAT_MONTH, month, "", "月");
            params.monthList.add(dateBean);
        }
        params.dayList = new ArrayList<>(31);
        for (int day = 1; day < 31; day++) {
            DateBean dateBean = new DateBean(DateBean.FORMAT_DAY, day, "", "日");
            params.dayList.add(dateBean);
        }
    }


    @Data
    public static class Params {
        //Dialog attribute
        private boolean cancelable = false;

        //Title
        private CharSequence title;
        private float titleTextSize = 16f;
        private int titleMarginTop = 24;
        private int titleMarginBottom = 0;
        @ColorRes
        private int titleTextColor = R.color.black;

        //Message
        private CharSequence message;
        private float messageTextSize = 16f;
        private int messageMarginTop = 0;
        private int messageMarginBottom = 24;
        @ColorRes
        private int messageTextColor = R.color.gray;

        //ButtonLine
        private int btnLineMinHeight = 48;

        //PositiveButton
        private CharSequence positiveBtnText;
        private float positiveTextSize = 16f;
        private DialogInterface.OnClickListener positiveBtnListener;
        @ColorRes
        private int positiveBtnTextColor = R.color.light_blue;

        //NegativeButton
        private CharSequence negativeBtnText;
        private float negativeTextSize = 16f;
        private DialogInterface.OnClickListener negativeBtnListener;
        @ColorRes
        private int negativeBtnTextColor = R.color.light_blue;

        //DatePicker
        private boolean isDatePickerDialog;
        private int datePickerHeight = 250;
        private int cancelBtnMarginTop = 16;
        private Calendar startTime; //若為DatePickerDialog且使用者未定義初始時間才賦預設值 1900/01/01
        private Calendar endTime; //若為DatePickerDialog且使用者未定義結束時間才賦預設值 2100/12/31
        private Calendar defaultTime; //若為DatePickerDialog且使用者未定義預設時間，才賦預設值 系統日

        private List<DateBean> yearList;
        private List<DateBean> monthList;
        private List<DateBean> dayList;

    }
}
