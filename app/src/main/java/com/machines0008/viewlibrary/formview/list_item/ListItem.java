package com.machines0008.viewlibrary.formview.list_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.machines0008.viewlibrary.databinding.ItemListBinding;
import com.machines0008.viewlibrary.formview.ItemView;
import com.machines0008.viewlibrary.formview.edit_item.EditTextBean;

import java.util.List;

/**
 * Project Name: WLTApplication
 * Created By: user
 * Created On: 2022/10/28
 * Usage:
 **/
public class ListItem<T extends ListCheckItem> extends ItemView {
    private ListCheckItemAdapter<T> adapter;
    private ListItemBean<T> listItem;

    public ListItem(@NonNull Context context) {
        super(context);
    }

    public void setList(List<T> t) {
        adapter.setList(t);
    }
    public void setTitle(CharSequence title) {
        listItem.title.set(title);
    }

    public List<T> getList() {
        return adapter.getList();
    }

    @Override
    protected View initView(@NonNull Context context) {
        ItemListBinding binding = ItemListBinding.inflate(LayoutInflater.from(context));
        adapter = new ListCheckItemAdapter<>();
        listItem = new ListItemBean<>();
        binding.setListItem(listItem);
        binding.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public String getTitle() {
        return listItem.title.get().toString();
    }

    @Override
    public String getContent() {
        return "";
    }

    @Override
    public boolean checkContentRequired() {
        return false;
    }

    @Override
    protected void clearData() {

    }
}
