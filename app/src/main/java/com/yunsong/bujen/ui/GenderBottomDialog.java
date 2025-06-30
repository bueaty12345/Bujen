package com.yunsong.bujen.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.R;

public class GenderBottomDialog extends BottomSheetDialog {
    private RadioGroup rgGender;
    private TextView tvCancel, tvConfirm;
    private String selectedGender = "";

    public interface OnGenderSelectedListener {
        void onGenderSelected(String gender);
    }

    private OnGenderSelectedListener listener;

    public GenderBottomDialog(@NonNull Context context, String currentGender) {
        super(context, R.style.CustomBottomSheetDialog);
        init(context, currentGender);
    }

    private void init(Context context, String currentGender) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_gender_selector, null);
        setContentView(view);

        rgGender = view.findViewById(R.id.rg_gender);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvConfirm = view.findViewById(R.id.tv_confirm);

        // 根据当前性别选中对应项
        if ("女".equals(currentGender)) {
            rgGender.check(R.id.rb_female);
            selectedGender = "女";
        } else if ("男".equals(currentGender)) {
            rgGender.check(R.id.rb_male);
            selectedGender = "男";
        }

        rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_female) {
                selectedGender = "女";
            } else if (checkedId == R.id.rb_male) {
                selectedGender = "男";
            }
        });

        tvCancel.setOnClickListener(v -> dismiss());

        tvConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGenderSelected(selectedGender);
            }
            dismiss();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int width = (int) (getContext().getResources().getDisplayMetrics().density * 300);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            bottomSheet.setLayoutParams(params);

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void setOnGenderSelectedListener(OnGenderSelectedListener listener) {
        this.listener = listener;
    }
}