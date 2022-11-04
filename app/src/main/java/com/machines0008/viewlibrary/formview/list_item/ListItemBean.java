package com.machines0008.viewlibrary.formview.list_item;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

/**
 * Project Name: WLTApplication
 * Created By: user
 * Created On: 2022/10/28
 * Usage:
 **/
public class ListItemBean<T> {
    public ObservableField<CharSequence> title = new ObservableField<>("");
    public MutableLiveData<List<T>> content = new MutableLiveData<>();
}
