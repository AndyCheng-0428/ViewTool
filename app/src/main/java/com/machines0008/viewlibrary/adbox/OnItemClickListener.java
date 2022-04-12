package com.machines0008.viewlibrary.adbox;

import android.view.View;

/**
 * Created by Android Studio
 * User   : Andy
 * Date   : 2022/4/11
 * Time  : 下午 10:57
 * Usage :
 * To change this template use File | Settings | File and Code Templates.
 */
public interface OnItemClickListener<T extends AdBoxVo> {
    void onItemClick(View v, T itemVo);
}
