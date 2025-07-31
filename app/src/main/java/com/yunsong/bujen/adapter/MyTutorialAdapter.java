package com.yunsong.bujen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.MedLocad;
import com.yunsong.bujen.MeditationActivity;
import com.yunsong.bujen.MyTutorial;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.TutorialHistory;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.util.List;

public class MyTutorialAdapter extends BaseAdapter {
    private Context context;
    private List<MyTutorialBean> data;

    public MyTutorialAdapter(MyTutorial local, List<MyTutorialBean> data) {
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
        MyTutorialAdapter.viewHolder holder;
        if (view == null) {
            holder = new MyTutorialAdapter.viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_mytutorial, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_tu);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            holder.second_line=view.findViewById(R.id.second_line);
            holder.love=view.findViewById(R.id.love);
            holder.itemData=view.findViewById(R.id.itemData);
            view.setTag(holder);
        }else {
            holder = (MyTutorialAdapter.viewHolder) view.getTag();
        }
        MyTutorialBean item = data.get(i);
        holder.txt_mname.setText(item.tutorialName);
        Glide.with(context)
                .load(item.backgroundMusicUrl)
                .placeholder(R.drawable.recommend1)
                .into(holder.img_tu);
        holder.second_line.setText(String.valueOf(item.createdAt));

        holder.love.setImageResource(item.sc ? R.drawable.collection_1 : R.drawable.collection_2);

        holder.love.setOnClickListener(v -> {
            item.sc = !item.sc;
            notifyDataSetChanged();
        });

//        // 设置点击事件
//        view.setOnClickListener(v -> {
//            notifyDataSetChanged(); // 刷新适配器
//        });

        holder.itemData.setOnClickListener(v -> {
            Integer userId = UserInfoUtils.getUserId(context);
            if (userId != null) {
                TutorialHistory.recordMeditationHistory(context, userId, item.tutorialId);
            }
            Log.d("本地教程","tutorialName"+item.tutorialName);
            Log.d("本地教程","videoUrl"+item.videoUrl);
            Log.d("本地教程","backgroundMusicUrl"+item.backgroundMusicUrl);

            Intent intent = new Intent(context, MeditationActivity.class);
            intent.putExtra("tutorialName", item.tutorialName);
            intent.putExtra("videoUrl", item.videoUrl);
            intent.putExtra("backgroundMusicUrl", item.backgroundMusicUrl);
            context.startActivity(intent);
        });
        return view;
    }
    private final class viewHolder {
        ImageView img_tu,love;
        TextView txt_mname,second_line;
        LinearLayout itemData;
    }

}
