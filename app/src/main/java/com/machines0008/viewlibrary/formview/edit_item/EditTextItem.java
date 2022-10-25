package com.machines0008.viewlibrary.formview.edit_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.machines0008.viewlibrary.R;
import com.machines0008.viewlibrary.databinding.ItemEditTextBinding;
import com.machines0008.viewlibrary.formview.ItemView;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class EditTextItem extends ItemView {
    private final Context context;
    private EditTextBean etb;

    public EditTextItem(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View initView(@NonNull Context context) {
        ItemEditTextBinding binding = ItemEditTextBinding.inflate(LayoutInflater.from(context));
        etb = new EditTextBean();
        binding.setEditTextItem(etb);
        return binding.getRoot();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_edit_text;
    }

    @Override
    public boolean checkContentRequired() {
        String content = Objects.requireNonNull(etb.content.get()).toString();
        return !StringUtils.isBlank(content) || !isRequired();
    }

    @Override
    public void clearData() {
        etb.content.set("");
    }

    public void setTitle(String title) {
        etb.title.set(title);
    }

    public void setTitle(@StringRes int titleResId) {
        etb.title.set(context.getString(titleResId));
    }

    public String getContent() {
        String content = Objects.requireNonNull(etb.content.get()).toString();
        return StringUtils.defaultString(content);
    }

    public void setContent(CharSequence content) {
        etb.content.set(content);
    }

    @Override
    public String getTitle() {
        return Objects.requireNonNull(etb.title.get()).toString();
    }
}