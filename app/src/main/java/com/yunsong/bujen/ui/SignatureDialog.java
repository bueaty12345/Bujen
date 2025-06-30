package com.yunsong.bujen.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yunsong.bujen.R;
import com.yunsong.bujen.utils.UserInfoUtils;

public class SignatureDialog extends Dialog {
    private EditText etNickname;
    private ImageView ivClear;
    private TextView tvCount;
    private TextView btnCancel, btnConfirm;
    private OnConfirmListener confirmListener;

    public SignatureDialog(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_set_signature);

        // 初始化控件
        etNickname = findViewById(R.id.et_nickname);
        ivClear = findViewById(R.id.iv_clear);
        tvCount = findViewById(R.id.tv_count);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        // 初始填入本地昵称
        etNickname.setText(UserInfoUtils.getUserSignature(getContext()));
        etNickname.setSelection(etNickname.getText().length());

        // 实时监听输入内容，更新字数显示与清除按钮
        etNickname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                tvCount.setText(String.valueOf(len));
                ivClear.setVisibility(len > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 清空输入内容
        ivClear.setOnClickListener(v -> etNickname.setText(""));

        // 取消按钮：关闭弹窗
        btnCancel.setOnClickListener(v -> dismiss());

        // 确认按钮：校验输入并通过回调返回
        btnConfirm.setOnClickListener(v -> {
            String nickname = etNickname.getText().toString().trim();
            if (nickname.length() < 2 || nickname.length() > 11) {
                Toast.makeText(getContext(), "签名长度需为2~11位", Toast.LENGTH_SHORT).show();
                return;
            }

            // 校验重复昵称
            String currentNickname = UserInfoUtils.getUserNickname(getContext());
            if (nickname.equals(currentNickname)) {
                Toast.makeText(getContext(), "新签名与当前签名相同", Toast.LENGTH_SHORT).show();
                return;
            }



            if (confirmListener != null) {
                confirmListener.onConfirm(nickname);
            }
            dismiss();
        });

        // 设置 Dialog 背景为透明圆角
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /**
     * 设置弹窗宽度为屏幕宽度 90%
     */
    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (metrics.widthPixels * 0.9);
            window.setAttributes(layoutParams);

            etNickname.requestFocus();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    /**
     * 设置确认按钮回调
     */
    public void setOnConfirmListener(OnConfirmListener listener) {
        this.confirmListener = listener;
    }

    /**
     * 回调接口，用于外部获取输入结果
     */
    public interface OnConfirmListener {
        void onConfirm(String nickname);
    }
}