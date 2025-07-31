package com.yunsong.bujen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunsong.bujen.R;

import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {
    private final List<Integer> timeList;
    private int selectedTime = -1;
    private final OnTimeSelectedListener listener;

    public interface OnTimeSelectedListener {
        void onTimeSelected(int time);
    }

    public TimerAdapter(List<Integer> timeList, OnTimeSelectedListener listener) {
        this.timeList = timeList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView selectedIcon;

        public ViewHolder(View view) {
            super(view);
            timeText = view.findViewById(R.id.txt_time_option);
            selectedIcon = view.findViewById(R.id.img_selected);
        }
    }

    @Override
    public TimerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int time = timeList.get(position);
        holder.timeText.setText(time + " 分钟");

        // 根据是否是当前选中项来显示勾选图标
        holder.selectedIcon.setVisibility(time == selectedTime ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(v -> {
            selectedTime = time;
            notifyDataSetChanged(); // 更新所有item状态
            listener.onTimeSelected(time);
        });
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }
}