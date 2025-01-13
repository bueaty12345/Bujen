package com.yunsong.bujen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;

import java.util.List;
import java.util.Map;

public class PraysAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> data;
    private static int selectedPosition = 0; // 选中的项索引
    private ConfirmDialog dialog;


    public PraysAdapter(Context local, List<Map<String, String>> data) {
        this.context = local;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        viewHolder holder;
        if (view == null) {
            holder = new viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_prays, viewGroup, false);
            holder.img_selet = view.findViewById(R.id.img_hart);
            holder.txt_mname = view.findViewById(R.id.txt_name);
            holder.txt_gdd = view.findViewById(R.id.txt_gdd);
            holder.btn_dh = view.findViewById(R.id.btn_dh);
            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        Map<String, String> map=data.get(i);
        holder.txt_mname.setText(map.get("name"));
        holder.txt_gdd.setText(map.get("gdd"));
        holder.btn_dh.setText(map.get("dh"));
        // 设置选中状态
        holder.img_selet.setImageResource(map.get("sc").equals("1") ? R.drawable.collection_1 : R.drawable.collection_2);
        holder.img_selet.setOnClickListener(v -> {
            if(data.get(i).get("sc").equals("0")){
                holder.img_selet.setImageResource(R.drawable.collection_1);
                data.get(i).put("sc","1");
            }
            else {
                holder.img_selet.setImageResource(R.drawable.collection_2);
                data.get(i).put("sc","0");
            }
//            notifyDataSetChanged(); // 刷新适配器

        });
        holder.btn_dh.setOnClickListener(v -> {
            if(holder.btn_dh.getText().equals("兑换")){
//                showDialog(v.findViewById(R.id.btn_dh));
                setDialog(holder.btn_dh);
            }else if(holder.btn_dh.getText().equals("加入祈福")){
                showDialog2(holder.btn_dh);
            }
//            notifyDataSetChanged(); // 刷新适配器

        });
        // 设置点击事件
//        view.setOnClickListener(v -> {
//            selectedPosition = i; // 更新选中的项
//
//            notifyDataSetChanged(); // 刷新适配器
//
//        });
        return view;
    }
    private final class viewHolder {
        ImageView img_selet;
        TextView txt_mname,txt_gdd;
        Button btn_dh;
    }
private void setDialog(Button v) {
    ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
    dialog = builder.cancelTouchout(false)
            .view(R.layout.dialog_confirm)
            .style(R.style.Dialog)
            .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    v.setText("开始学习");
                    dialog.dismiss();  // 这里添加取消对话框的代码
                }
            })
            .build();
    dialog.show();
}
    private void showDialog2(Button v) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .setTitle("确定加入祈福吗")
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        v.setText("已加入");
                        dialog.dismiss();  // 这里添加取消对话框的代码
                    }
                })
                .build();
        dialog.show();
    }
}
