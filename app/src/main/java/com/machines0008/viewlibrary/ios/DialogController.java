package com.machines0008.viewlibrary.ios;

import android.content.DialogInterface;

import androidx.annotation.ColorRes;
import androidx.databinding.ObservableField;

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
     * 1.若使用者未定義開始時間及結束時間 則以1900年1月1日至2100年12月31日為止
     * 2.若使用者定義之開始時間大於結束時間，內部會進行置換，確保開始時間早於結束時間
     * 3.若使用者未定義預設時間，則預設時間為系統日
     * 4.若預設時間(包含使用者設定及未設定之預設時間)超出開始時間及結束時間限制，則預設時間修正為開始時間或結束時間
     * ex.開始日期、結束日期、預設日期
     *
     * @param params 系統彈窗參數
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
        if (params.getDefaultTime().after(endTime)) {
            params.setDefaultTime(endTime);
        }
        if (params.getDefaultTime().before(startTime)) {
            params.setDefaultTime(startTime);
        }
        int startYear = startTime.get(Calendar.YEAR);
        int endYear = endTime.get(Calendar.YEAR);
        params.yearList = new ArrayList<>(endYear - startYear + 1);
        int defaultYear = params.getDefaultTime().get(Calendar.YEAR);
        int defaultMonth = params.getDefaultTime().get(Calendar.MONTH) + 1;
        int defaultDay = params.getDefaultTime().get(Calendar.DATE);
        for (int year = startYear; year <= endYear; year++) {
            DateBean dateBean = new DateBean(DateBean.FORMAT_YEAR, year, params.getYearPrefix(), params.getYearSuffix());
            params.yearList.add(dateBean);
            if (defaultYear == year) {
                params.setSelectedYear(dateBean);
            }
        }
        params.monthList = new ArrayList<>(12);
        for (int month = 1; month <= 12; month++) {
            DateBean dateBean = new DateBean(DateBean.FORMAT_MONTH, month, params.getMonthPrefix(), params.getMonthSuffix());
            params.monthList.add(dateBean);
            if (defaultMonth == month) {
                params.setSelectedMonth(dateBean);
            }
        }
        params.dayList = new ArrayList<>(31);
        for (int day = 1; day <= 31; day++) {
            DateBean dateBean = new DateBean(DateBean.FORMAT_DAY, day, params.getDayPrefix(), params.getDaySuffix());
            params.dayList.add(dateBean);
            if (defaultDay == day) {
                params.setSelectedDay(dateBean);
            }
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
        private DateBean selectedYear;
        private String yearPrefix;
        private String yearSuffix;

        private List<DateBean> monthList;
        private DateBean selectedMonth;
        private String monthPrefix;
        private String monthSuffix;

        private List<DateBean> dayList;
        private DateBean selectedDay;
        private String dayPrefix;
        private String daySuffix;
        private CharSequence errorMsgTmp; //超出期限時所用之暫存訊息
        private ObservableField<CharSequence> errorMsg = new ObservableField<>();

        private float errorMsgTextSize = 16f;
        private int errorMsgMarginTop = 0;
        private int errorMsgMarginBottom = 24;

        @ColorRes
        private int errorMsgTextColor = R.color.light_red;
        private OnDateSelectedListener dateSelectedListener;

        private DialogInterface.OnClickListener interruptedListener; //此監聽器做為中斷監聽器，其值恆為null，作為DataBinding需阻斷過程方法執行之監聽器
    }

    public interface OnDateSelectedListener {
        void onSelected(int year, int month, int day);
    }
}
