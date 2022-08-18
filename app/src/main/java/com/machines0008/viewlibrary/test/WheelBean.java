package com.machines0008.viewlibrary.test;

import com.machines0008.viewlibrary.wheelview.WheelViewVo;

import lombok.Setter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/18
 * Usage:
 **/
public class WheelBean implements WheelViewVo {
    @Setter
    private String name;

    @Override
    public String getName() {
        return name;
    }
}
