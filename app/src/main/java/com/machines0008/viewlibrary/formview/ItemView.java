package com.machines0008.viewlibrary.formview;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemView {
    public static final String EMPTY_CONTENT = "";
    private final View rootView;
    private boolean isVisible = true;
    private boolean clickable = true;
    private boolean required = false;
    private final List<CheckRuleListener> checkRuleListenerList = new ArrayList<>();

    protected ItemView(@NonNull Context context) {
        rootView = initView(context);
    }

    public View getView() {
        return rootView;
    }

    protected abstract View initView(@NonNull Context context);

    public void setClickable(boolean flag) {
        clickable = flag;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void addCheckRuleListener(CheckRuleListener checkRuleListener) {
        checkRuleListenerList.add(checkRuleListener);
    }

    public List<CheckRuleListener> getCheckRuleListenerList() {
        return checkRuleListenerList;
    }

    public boolean isRequired() {
        return required;
    }

    public void hide() {
        clearData();
        isVisible = false;
        rootView.setVisibility(View.GONE);
    }

    public abstract String getTitle();

    public abstract String getContent();

    public void show() {
        isVisible = true;
        rootView.setVisibility(View.VISIBLE);
    }

    public void refresh() {
        rootView.invalidate();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public abstract boolean checkContentRequired();

    protected abstract void clearData();

    public interface CheckRuleListener {
        @NonNull
        String check(String content);
    }
}