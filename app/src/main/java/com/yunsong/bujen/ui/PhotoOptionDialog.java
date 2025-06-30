package com.yunsong.bujen.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.R;

public class PhotoOptionDialog extends BottomSheetDialog {
    private TextView tvTakePhoto, tvChoosePhoto, tvCancel;

    public interface OnOptionSelectedListener {
        void onTakePhoto();
        void onChoosePhoto();
    }

    private OnOptionSelectedListener listener;

    public PhotoOptionDialog(@NonNull Context context) {
        super(context, R.style.CustomBottomSheetDialog);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_photo_options, null);
        setContentView(view);

        tvTakePhoto = view.findViewById(R.id.tv_take_photo);
        tvChoosePhoto = view.findViewById(R.id.tv_choose_photo);
        tvCancel = view.findViewById(R.id.tv_cancel);

        tvTakePhoto.setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onTakePhoto();
        });

        tvChoosePhoto.setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onChoosePhoto();
        });

        tvCancel.setOnClickListener(v -> dismiss());
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.listener = listener;
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
}
