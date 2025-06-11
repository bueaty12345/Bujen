package com.yunsong.bujen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.MyCollect;
import com.yunsong.bujen.MyPray;
import com.yunsong.bujen.R;

import java.util.List;
import java.util.Map;

public class MyCollectAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> data;
    private static int selectedPosition = 0; // 选中的项索引
    private ConfirmDialog dialog;

    public MyCollectAdapter(MyCollect local, List<Map<String, String>> data) {
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
        MyCollectAdapter.viewHolder holder;
        if (view == null) {
            holder = new MyCollectAdapter.viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_collect, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_tu);
            holder.img_selet = view.findViewById(R.id.img_selet);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            holder.txt_gdd = view.findViewById(R.id.txt_gdd);
            holder.txt_hy = view.findViewById(R.id.txt_hy);
            holder.txt_auther = view.findViewById(R.id.txt_auther);
            view.setTag(holder);
        }else {
            holder = (MyCollectAdapter.viewHolder) view.getTag();
        }
        Map<String, String> map=data.get(i);
        holder.txt_mname.setText(map.get("name"));
        holder.txt_gdd.setText(map.get("gdd"));
        holder.txt_auther.setText(map.get("auther"));
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
            notifyDataSetChanged(); // 刷新适配器

        });
        holder.txt_hy.setOnClickListener(v -> {
            if(holder.txt_hy.getText().equals("待兑换")){
//               showDialog(v.findViewById(R.id.txt_hy));
                setDialog(holder.txt_hy);
                notifyDataSetChanged(); // 刷新适配器
            }

        });
        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname,txt_gdd,txt_hy,txt_auther;
    }
    private void setDialog(TextView v) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        v.setText("已拥有");
                        dialog.dismiss();  // 这里添加取消对话框的代码
                    }
                })
                .build();
        dialog.show();
    }
}
