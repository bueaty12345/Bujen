package com.yunsong.bujen;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ConfirmDialog extends Dialog {
    private Context context;
    private int height, width;
    private boolean cancelTouchout;
    private View view;
    private String title;

    public ConfirmDialog(@NonNull Builder builder) {
        super(builder.context);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelTouchout = builder.cancelTouchout;
        view = builder.view;
        title = builder.title;  // 添加 title 的赋值
    }

    public ConfirmDialog(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelTouchout = builder.cancelTouchout;
        view = builder.view;
        title = builder.title;  // 添加 title 的赋值
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);

        setCanceledOnTouchOutside(cancelTouchout);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;

        // 将 dp 转换为像素
        float density = context.getResources().getDisplayMetrics().density;
        lp.width = (int) (width * density);
        lp.height = (int) (height * density);
        win.setAttributes(lp);

        TextView txt_title = view.findViewById(R.id.txt_title);
        if (title != null) {
            txt_title.setText(title);
        }

        // 绑定按钮点击事件
        view.findViewById(R.id.txt_cancel).setOnClickListener(v -> dismiss());
//        view.findViewById(R.id.txt_confirm).setOnClickListener(v -> {
//            Toast.makeText(context, "已确认", Toast.LENGTH_SHORT).show();
//            dismiss();
//        });
    }

    public static final class Builder {
        private Context context;
        private int height=168, width=301;
        private boolean cancelTouchout;
        private View view;
        private int resStyle = -1;
        private String title;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder view(int resView) {
            view = LayoutInflater.from(context).inflate(resView, null);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder heightpx(int val) {
            height = val;
            return this;
        }

        public Builder widthpx(int val) {
            width = val;
            return this;
        }

        public Builder heightDimenRes(int dimenRes) {
            height = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder widthDimenRes(int dimenRes) {
            width = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        public Builder cancelTouchout(boolean val) {
            cancelTouchout = val;
            return this;
        }

        public Builder addViewOnclick(int viewRes, View.OnClickListener listener) {
            if (view != null) {
                view.findViewById(viewRes).setOnClickListener(listener);
            }
            return this;
        }

        public ConfirmDialog build() {
            if (resStyle != -1) {
                return new ConfirmDialog(this, resStyle);
            } else {
                return new ConfirmDialog(this);
            }
        }
    }
}

