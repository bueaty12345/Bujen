package com.yunsong.bujen.adapter;

import static com.yunsong.bujen.fragment.HomeFragment.Mi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.Local;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.fragment.MusicController;
import com.yunsong.bujen.fragment.MusicService;

import java.util.List;

public class LocalMusicAdapter extends BaseAdapter {
    private Context context;
    private List<MyMusicBean> data;


    public LocalMusicAdapter(Local local, List<MyMusicBean> data) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_local, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_tu);
            holder.img_selet = view.findViewById(R.id.img_selet);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        MyMusicBean item = data.get(i);
        holder.txt_mname.setText(item.musicName);
        Glide.with(context)
                .load(item.musicCover)
                .placeholder(R.drawable.recommend1)
                .into(holder.img_tu);
        // 设置背景颜色
        if (i == Mi) {
            holder.img_selet.setVisibility(View.VISIBLE);
        } else {
            holder.img_selet.setVisibility(View.INVISIBLE);
        }

        view.setOnClickListener(v -> {
            Mi = i; // 设置当前点击项为选中项
            notifyDataSetChanged(); // 刷新列表，触发 getView() 再次设置 img_selet
            Intent result = new Intent();
            result.putExtra("selectedMusic", item);
            ((Activity) context).setResult(Activity.RESULT_OK, result);
            ((Activity) context).finish(); // 关闭 Local 页面
        });

        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname;
    }


}
