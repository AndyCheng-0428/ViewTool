package com.machines0008.viewlibrary.wheelview;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import lombok.Getter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/19
 * Usage:
 **/
public class DateBean implements WheelViewVo {
    @Getter
    @DateType
    private int dateType;
    @Getter
    private int data; //實際資料

    private String prefix = "";
    private String suffix = "";

    public DateBean(@DateType int dateType, int data) {
        this.dateType = dateType;
        this.data = data;
    }

    public DateBean(@DateType int dateType, int data, String prefix, String suffix) {
        this.dateType = dateType;
        this.data = data;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getName() {
        return String.format("%s%02d%s", prefix, data, suffix);
    }


    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @IntDef({FORMAT_YEAR, FORMAT_MONTH, FORMAT_DAY})
    public @interface DateType {

    }

    public static final int FORMAT_YEAR = 1;
    public static final int FORMAT_MONTH = 2;
    public static final int FORMAT_DAY = 3;
}
