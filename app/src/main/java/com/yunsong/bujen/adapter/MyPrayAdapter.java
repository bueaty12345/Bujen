package com.yunsong.bujen.adapter;

import static com.yunsong.bujen.fragment.HomeFragment.Mi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.MyPray;
import com.yunsong.bujen.MyTutorial;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyPrayBean;
import com.yunsong.bujen.fragment.MusicController;
import com.yunsong.bujen.fragment.MusicService;

import java.util.List;
import java.util.Map;

public class MyPrayAdapter extends BaseAdapter {
    private Context context;
    private List<MyPrayBean> data;

    public MyPrayAdapter(MyPray local, List<MyPrayBean> data) {
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
        MyPrayAdapter.viewHolder holder;
        if (view == null) {
            holder = new MyPrayAdapter.viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_mypray, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_tu);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            holder.second_line=view.findViewById(R.id.second_line);
            view.setTag(holder);
        }else {
            holder = (MyPrayAdapter.viewHolder) view.getTag();
        }
        MyPrayBean item = data.get(i);
        holder.txt_mname.setText(item.blessing_theme);
        Glide.with(context)
                .load(item.blessing_background_url)
                .placeholder(R.drawable.recommend1)
                .into(holder.img_tu);
        holder.second_line.setText(item.zen_quote);
        // 设置点击事件
        view.setOnClickListener(v -> {
            notifyDataSetChanged(); // 刷新适配器

        });
        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname,second_line;
    }

}
