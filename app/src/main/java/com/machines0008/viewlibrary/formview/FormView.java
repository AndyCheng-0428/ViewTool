package com.machines0008.viewlibrary.formview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class FormView extends LinearLayout {
    private FormView.OnItemClickListener onItemClickListener;
    private FormViewAdapter adapter;

    public FormView(Context context) {
        super(context);
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(FormViewAdapter adapter) {
        this.adapter = adapter;
        reloadChildView();
    }

    private void reloadChildView() {
        removeAllViews();
        if (adapter == null) {
            return;
        }
        for (int i = 0, count = adapter.getCount(); i < count; ++i) {
            View view = adapter.getView(i);
            if (view == null) {
                continue;
            }
            addView(view);
        }
        if (onItemClickListener != null) {
            setOnItemClickListener(onItemClickListener);
        }
        requestLayout();
    }

    private void setOnItemClickListener(FormView.OnItemClickListener listener) {
        this.onItemClickListener = listener;

        for (int i = 0, count = adapter.getCount(); i < count; ++i) {
            final int ii = i;
            if (adapter.getItemView(i).isClickable()) {
                adapter.getView(i).setOnClickListener(v -> onItemClickListener.onItemClick(v, ii));
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(View var1, int position);
    }
}