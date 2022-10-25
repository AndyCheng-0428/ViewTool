package com.machines0008.viewlibrary.ios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableField;

import com.machines0008.viewlibrary.databinding.DialogProgressBinding;

import lombok.Data;

/**
 * Project Name: WLTApplication
 * Created By: user
 * Created On: 2022/10/24
 * Usage:
 **/
public class ProgressDialog extends AlertDialog {
    private View baseView;
    private Params params;
    @Override
    public void show() {
        Window win = getWindow();
        super.show();
        WindowManager.LayoutParams lp = win.getAttributes();
        win.setContentView(baseView);
        if (null != lp) {
            lp.width = 320;
            lp.height = 320;
        }
        win.setAttributes(lp);
    }

    private ProgressDialog(Context context) {
        super(context);
        setCancelable(false);
    }
    public void setMessage(CharSequence msg) {
        if (null == params) {
            return;
        }
        params.message.set(msg);
    }

    public void setView(View baseView) {
        this.baseView = baseView;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Builder {
        private Context context;
        private Params params = new Params();
        private DialogProgressBinding binding;

        public Builder(Context context) {
            this.context = context;
            binding = DialogProgressBinding.inflate(LayoutInflater.from(context));
            binding.setParams(params);
        }

        public Builder setMessage(CharSequence msg) {
            params.message.set(msg);
            return this;
        }

        public Builder setCancelListener(CharSequence text, View.OnClickListener onClickListener) {
            params.cancelText.set(text);
            params.cancelListener = onClickListener;
            return this;
        }

        public ProgressDialog build() {
            ProgressDialog dialog = new ProgressDialog(context);
            binding.setDialog(dialog);
            dialog.setView(binding.getRoot());
            dialog.setParams(params);
            dialog.setCancelable(false);
            return dialog;
        }
    }

    @Data
    public static class Params {
        private ObservableField<CharSequence> message = new ObservableField<>("請稍後");
        private ObservableField<CharSequence> cancelText = new ObservableField<>();
        private View.OnClickListener cancelListener;
    }
}