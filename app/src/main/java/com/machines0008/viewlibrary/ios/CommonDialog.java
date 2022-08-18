package com.machines0008.viewlibrary.ios;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.machines0008.viewlibrary.R;
import com.machines0008.viewlibrary.databinding.DialogBinding;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/18
 * Usage:
 **/
public class CommonDialog extends AlertDialog {
    protected CommonDialog(Context context) {
        super(context);
    }

    public static class Builder {
        private final DialogController.Params P;
        private final Context context;

        public Builder(@NonNull Context context) {
            P = new DialogController.Params();
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {
            P.setCancelable(cancelable);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            P.setTitle(title);
            return this;
        }

        public Builder setTitle(@StringRes int strRes) {
            P.setTitle(context.getText(strRes));
            return this;
        }

        public Builder setTitleMarginTop(int marginTop) {
            P.setTitleMarginTop(marginTop);
            return this;
        }

        public Builder setTitleMarginBottom(int marginBottom) {
            P.setTitleMarginBottom(marginBottom);
            return this;
        }

        public Builder setTitleTextSize(float textSize) {
            P.setTitleTextSize(textSize);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            P.setMessage(message);
            return this;
        }

        public Builder setMessageTextSize(float textSize) {
            P.setMessageTextSize(textSize);
            return this;
        }

        public Builder setMessage(@StringRes int strRes) {
            P.setMessage(context.getText(strRes));
            return this;
        }

        public Builder setMessageMarginTop(int marginTop) {
            P.setMessageMarginTop(marginTop);
            return this;
        }

        public Builder setMessageMarginBottom(int marginBottom) {
            P.setMessageMarginBottom(marginBottom);
            return this;
        }

        public Builder setBtnLineMinHeight(int minHeight) {
            P.setBtnLineMinHeight(minHeight);
            return this;
        }

        public Builder setPositiveBtnTextColor(@ColorRes int colorRes) {
            P.setPositiveBtnTextColor(colorRes);
            return this;
        }

        public Builder setNegativeBtnTextColor(@ColorRes int colorRes) {
            P.setNegativeBtnTextColor(colorRes);
            return this;
        }

        public Builder setNegativeTextSize(float textSize) {
            P.setNegativeTextSize(textSize);
            return this;
        }

        public Builder setPositiveTextSize(float textSize) {
            P.setPositiveTextSize(textSize);
            return this;
        }

        public Builder setPositiveBtnClickListener(CharSequence btnText, DialogInterface.OnClickListener listener) {
            P.setPositiveBtnText(btnText);
            P.setPositiveBtnListener(listener);
            return this;
        }

        public Builder setNegativeBtnClickListener(CharSequence btnText, DialogInterface.OnClickListener listener) {
            P.setNegativeBtnText(btnText);
            P.setNegativeBtnListener(listener);
            return this;
        }

        public Builder setPositiveBtnClickListener(@StringRes int strRes, DialogInterface.OnClickListener listener) {
            P.setPositiveBtnText(context.getText(strRes));
            P.setPositiveBtnListener(listener);
            return this;
        }

        public Builder setNegativeBtnClickListener(@StringRes int strRes, DialogInterface.OnClickListener listener) {
            P.setNegativeBtnText(context.getText(strRes));
            P.setNegativeBtnListener(listener);
            return this;
        }

        public CommonDialog build() {
            CommonDialog dialog = new CommonDialog(context);
            DialogBinding binding = DialogBinding.inflate(LayoutInflater.from(context));
            binding.setDialog(dialog);
            binding.setParams(P);
            dialog.setView(binding.getRoot());
            dialog.setCancelable(P.isCancelable());
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.background);
            return dialog;
        }

    }
}
