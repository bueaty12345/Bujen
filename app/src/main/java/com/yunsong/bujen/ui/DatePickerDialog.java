package com.yunsong.bujen.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.R;

import java.util.Calendar;

public class DatePickerDialog {

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }

    public static void show(Context context, OnDateSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_date_picker, null);
        dialog.setContentView(view);

        View parent = (View) view.getParent();
        parent.setBackground(new ColorDrawable(Color.TRANSPARENT));

        dialog.setCancelable(true);

        NumberPicker npYear = view.findViewById(R.id.np_year);
        NumberPicker npMonth = view.findViewById(R.id.np_month);
        NumberPicker npDay = view.findViewById(R.id.np_day);
        TextView btnCancel = view.findViewById(R.id.btn_cancel);
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        npYear.setMinValue(currentYear - 5);
        npYear.setMaxValue(currentYear + 5);
        npYear.setValue(currentYear);
//        npYear.setFormatter(value -> value + "年");

        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        npMonth.setValue(currentMonth);
//        npMonth.setFormatter(value -> value + "月");

        npDay.setMinValue(1);
        npDay.setMaxValue(31);
        npDay.setValue(currentDay);
//        npDay.setFormatter(value -> value + "日");
        npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        NumberPicker.OnValueChangeListener updateDayListener = (picker, oldVal, newVal) -> {
            Calendar temp = Calendar.getInstance();
            temp.set(npYear.getValue(), npMonth.getValue() - 1, 1);
            int maxDay = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
            npDay.setMaxValue(maxDay);
        };

        npYear.setOnValueChangedListener(updateDayListener);
        npMonth.setOnValueChangedListener(updateDayListener);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateSelected(npYear.getValue(), npMonth.getValue(), npDay.getValue());
            }
            dialog.dismiss();

        });

        dialog.show();
    }
}