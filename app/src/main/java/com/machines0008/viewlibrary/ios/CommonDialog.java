package com.machines0008.viewlibrary.ios;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.machines0008.viewlibrary.R;
import com.machines0008.viewlibrary.databinding.DialogBinding;
import com.machines0008.viewlibrary.databinding.DialogDatePickerBinding;
import com.machines0008.viewlibrary.databinding.DialogItemBinding;
import com.machines0008.viewlibrary.formview.FormViewAdapter;
import com.machines0008.viewlibrary.formview.ItemView;
import com.machines0008.viewlibrary.wheelview.DateBean;

import java.util.ArrayList;
import java.util.Calendar;


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

        public Builder setPositiveBtnClickListener(@StringRes int strRes, DialogController.OnDateSelectedListener listener) {
            P.setDateSelectedListener(listener);
            P.setPositiveBtnText(context.getText(strRes));
            return this;
        }

        public Builder setDatePickerConstraint(Calendar startCalendar, Calendar endCalendar, CharSequence errorMsg) {
            P.setStartTime(startCalendar);
            P.setEndTime(endCalendar);
            P.setErrorMsgTmp(errorMsg);
            return this;
        }

        public Builder setYearPrefix(String prefix) {
            P.setYearPrefix(prefix);
            return this;
        }

        public Builder setYearSuffix(String suffix) {
            P.setYearSuffix(suffix);
            return this;
        }

        public Builder setMonthPrefix(String prefix) {
            P.setMonthPrefix(prefix);
            return this;
        }

        public Builder setMonthSuffix(String suffix) {
            P.setMonthSuffix(suffix);
            return this;
        }

        public Builder setDayPrefix(String prefix) {
            P.setDayPrefix(prefix);
            return this;
        }

        public Builder setDaySuffix(String suffix) {
            P.setDaySuffix(suffix);
            return this;
        }

        public Builder setErrorMessageTextSize(float textSize) {
            P.setErrorMsgTextSize(textSize);
            return this;
        }

        public Builder setErrorMessageMarginTop(int marginTop) {
            P.setErrorMsgMarginTop(marginTop);
            return this;
        }

        public Builder setErrorMessageMarginBottom(int marginBottom) {
            P.setErrorMsgMarginBottom(marginBottom);
            return this;
        }

        public Builder setErrorMessageTextColor(@ColorRes int textColor) {
            P.setErrorMsgTextColor(textColor);
            return this;
        }

        public Builder addEditorItem(@NonNull ItemView itemView) {
            if (null == itemView) {
                return this;
            }
            if (P.getItemViewList() == null) {
                P.setItemViewList(new ArrayList<>());
                P.setItemViewDialog(true);
            }
            P.getItemViewList().add(itemView);
            return this;
        }

        public Builder isDatePickerDialog() {
            P.setDatePickerDialog(true);
            return this;
        }

        @SuppressWarnings("unchecked")
        public CommonDialog build() {
            CommonDialog dialog = new CommonDialog(context);
            Window window = dialog.getWindow();
            if (P.isDatePickerDialog()) {
                DialogController.fillDatePicker(P); //充填未填寫之參數
                DialogDatePickerBinding binding = DialogDatePickerBinding.inflate(LayoutInflater.from(context));
                binding.year.setOnSelectListener(item -> {
                    // 1:檢查日期合法性 若不符合現實日期規定，則以上次所選擇合法年分作為選擇年
                    // 2:檢查日期是否符合期間限定 若不符合限定期間，以錯誤訊息方式呈現
                    int checkResult = checkDate((DateBean) binding.year.getSelectedItem(), (DateBean) binding.month.getSelectedItem(), (DateBean) binding.day.getSelectedItem(), P.getStartTime(), P.getEndTime());
                    switch (checkResult) {
                        case 0:
                            P.setSelectedYear((DateBean) item);
                            P.getErrorMsg().set("");
                            break;
                        case 1:
                            binding.year.setSelectedItem(P.getSelectedYear());
                            break;
                        case 2:
                            P.getErrorMsg().set(P.getErrorMsgTmp());
                            break;
                    }
                });
                binding.month.setOnSelectListener(item -> {
                    // 1:檢查日期合法性 若不符合現實日期規定，則以上次所選擇合法月分作為選擇月
                    // 2:檢查日期是否符合期間限定 若不符合限定期間，以錯誤訊息方式呈現
                    int checkResult = checkDate((DateBean) binding.year.getSelectedItem(), (DateBean) binding.month.getSelectedItem(), (DateBean) binding.day.getSelectedItem(), P.getStartTime(), P.getEndTime());
                    switch (checkResult) {
                        case 0:
                            P.setSelectedMonth((DateBean) item);
                            P.getErrorMsg().set("");
                            break;
                        case 1:
                            binding.month.setSelectedItem(P.getSelectedMonth());
                            break;
                        case 2:
                            P.getErrorMsg().set(P.getErrorMsgTmp());
                            break;
                    }
                });
                binding.day.setOnSelectListener(item -> {
                    // 1:檢查日期合法性 若不符合現實日期規定，則以上次所選擇合法日作為選擇日
                    // 2:檢查日期是否符合期間限定 若不符合限定期間，以錯誤訊息方式呈現
                    int checkResult = checkDate((DateBean) binding.year.getSelectedItem(), (DateBean) binding.month.getSelectedItem(), (DateBean) binding.day.getSelectedItem(), P.getStartTime(), P.getEndTime());
                    switch (checkResult) {
                        case 0:
                            P.setSelectedDay((DateBean) item);
                            P.getErrorMsg().set("");
                            break;
                        case 1:
                            binding.day.setSelectedItem(P.getSelectedDay());
                            break;
                        case 2:
                            P.getErrorMsg().set(P.getErrorMsgTmp());
                            break;
                    }
                });
                binding.setDialog(dialog);
                binding.setParams(P);
                dialog.setView(binding.getRoot());
                window.setBackgroundDrawableResource(R.color.transparent);
                window.setGravity(Gravity.BOTTOM);
            } else if (P.isItemViewDialog()) {
                DialogItemBinding binding = DialogItemBinding.inflate(LayoutInflater.from(context));
                FormViewAdapter fvAdapter = new FormViewAdapter();
                for (ItemView itemView : P.getItemViewList()) {
                    fvAdapter.add(itemView);
                }
                P.setFormViewAdapter(fvAdapter);
                binding.formView.setAdapter(fvAdapter);
                binding.setDialog(dialog);
                binding.setParams(P);
                dialog.setView(binding.getRoot());
                window.setBackgroundDrawableResource(R.drawable.background);
            } else {
                DialogBinding binding = DialogBinding.inflate(LayoutInflater.from(context));
                binding.setDialog(dialog);
                binding.setParams(P);
                dialog.setView(binding.getRoot());
                window.setBackgroundDrawableResource(R.drawable.background);
            }
            dialog.setCancelable(P.isCancelable());
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        /**
         * 檢查日期合法性
         * 1. 若日期本身不符合西元曆法，則回傳1
         * 2. 若日期不符合使用者自定義之開始及結束時間，則回傳2
         * 3. 若日期符合上述兩者，則回傳0 表示成功
         *
         * @param year      年被選項
         * @param month     月被選項
         * @param day       日被選項
         * @param startTime 使用者自定義開始時間
         * @param endTime   使用者自定義結束時間
         * @return 日期檢查結果
         */
        public int checkDate(DateBean year, DateBean month, DateBean day, Calendar startTime, Calendar endTime) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setLenient(false);
                calendar.set(year.getData(), month.getData() - 1, day.getData());
                calendar.getTime();
                if (startTime.before(calendar) && endTime.after(calendar)) {
                    return 2;
                }
                return 0;
            } catch (IllegalArgumentException e) {
                return 1;
            }
        }
    }
}
