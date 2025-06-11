package com.yunsong.bujen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.Local;
import com.yunsong.bujen.R;

import java.util.List;
import java.util.Map;

public class LocalLightAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> data;
    private static int selectedPosition = 0; // 选中的项索引

    public LocalLightAdapter(Local local, List<Map<String, String>> data) {
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
//        int[] dg={R.drawable.home_bg2,R.drawable.dg_green,R.drawable.dg_red,R.drawable.dg_orange,R.drawable.dg_blue};
        String[] bgColors = {
                "#C5D4BD",
                "#F5AB9D",
                "#E1A47D",
                "#BECFD7"
        };
        String[] bar={"#ECE0D2","#DCE7D7","#F7CFC3","#F3E0C9","#D9E2E7"};
        if (view == null) {
            holder = new viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_locallight, viewGroup, false);
            holder.view_color = view.findViewById(R.id.view_color);
            holder.img_selet = view.findViewById(R.id.img_selet);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        Map<String, String> map=data.get(i);
        holder.txt_mname.setText(map.get("name"));
        // 设置圆角背景颜色
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(bgColors[i]));
        drawable.setCornerRadius(16f); // 圆角半径
        holder.view_color.setBackground(drawable);

        // 显示选中
        if (i == selectedPosition) {
            holder.img_selet.setVisibility(View.VISIBLE);
        } else {
            holder.img_selet.setVisibility(View.INVISIBLE);
        }
        // 设置点击事件
        view.setOnClickListener(v -> {
            selectedPosition = i; // 更新选中的项
            notifyDataSetChanged(); // 刷新适配器
//            Homepage.rl_bg.setBackgroundResource(dg[i]);
//            Homepage.homebg=dg[i];
//            Homepage.ly_tab.setBackgroundColor(Color.parseColor(bar[i]));
//            Homepage.homeColor=bar[i];
        });
        return view;
    }
    private final class viewHolder {
        ImageView img_selet;
        TextView txt_mname;
        View view_color;
    }
}
