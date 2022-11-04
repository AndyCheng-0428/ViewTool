package com.machines0008.viewlibrary.formview.list_item;

/**
 * Project Name: WLTApplication
 * Created By: user
 * Created On: 2022/10/28
 * Usage:
 **/
public interface ListCheckItem {
    boolean isChecked();

    void setChecked(boolean checked);

    String getItemName();

    String getSubItemName();
}
