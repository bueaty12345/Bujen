package com.yunsong.bujen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.MeditationActivity;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyTutorialBean;

import java.util.List;

public class MedHistoryAdapter  extends RecyclerView.Adapter<MedHistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<MyTutorialBean> data;
    public MedHistoryAdapter(Context context, List<MyTutorialBean> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_med_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        MyTutorialBean item = data.get(position);
        holder.txtName.setText(item.tutorialName);
        holder.txtAuthor.setText("作者：" + item.author);
        if (item.backgroundMusicUrl != null && !item.backgroundMusicUrl.isEmpty()) {
            Glide.with(context)
                    .load(item.backgroundMusicUrl)
                    .placeholder(R.drawable.jc_bg1) // 默认图
                    .into(holder.img_bg);
        } else {
            holder.img_bg.setImageResource(R.drawable.jc_bg1);
        }
        holder.btnGo.setOnClickListener(v -> {
            Intent intent = new Intent(context, MeditationActivity.class);
            intent.putExtra("tutorialName", item.tutorialName);
            intent.putExtra("videoUrl", item.videoUrl);
            intent.putExtra("backgroundMusicUrl", item.backgroundMusicUrl);
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtAuthor, txtTime;
        Button btnGo;
        ImageView imgHeart,img_bg;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtAuthor = itemView.findViewById(R.id.txt_gdd);
            txtTime = itemView.findViewById(R.id.txt_time);
            imgHeart = itemView.findViewById(R.id.img_hart);
            btnGo = itemView.findViewById(R.id.btn_dh);
            img_bg=itemView.findViewById(R.id.img_bg);
        }
    }
}
