package com.machines0008.viewlibrary.formview;

import android.view.View;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FormViewAdapter {
    private final List<ItemView> list = new ArrayList<>();

    public View getView(int position) {
        return position < list.size() ? (list.get(position)).getView() : null;
    }

    public ItemView getItemView(int position) {
        return position < list.size() ? list.get(position) : null;
    }

    public void add(ItemView itemView) {
        list.add(itemView);
    }

    public void add(int position, ItemView itemView) {
        list.add(position, itemView);
    }

    public void remove(int position) {
        list.remove(position);
    }

    public void removeAll() {
        list.clear();
    }

    public int getCount() {
        return list.size();
    }

    public Collection<ItemView> getItemViewCollection() {
        return list;
    }

    public String checkAllRule() {
        StringBuilder sb = new StringBuilder();
        for (ItemView itemView : list) {
            List<ItemView.CheckRuleListener> listeners = itemView.getCheckRuleListenerList();
            for (ItemView.CheckRuleListener listener : listeners) {
                String result = listener.check(itemView.getContent());
                if (StringUtils.isBlank(result)) {
                    continue;
                }
                sb.append(result).append("\r\n");
            }
        }
        String result = sb.toString();
        if (StringUtils.isNotBlank(result)) {
            return StringUtils.substring(result, 0, StringUtils.lastIndexOf(result, "\r\n"));
        }
        return result;
    }
}