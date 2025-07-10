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
    private List<Integer> times;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(int time);
    }

    public TimerAdapter(List<Integer> times, OnItemClickListener listener) {
        this.times = times;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerAdapter.ViewHolder holder, int position) {
        int time = times.get(position);
        holder.textView.setText(time + "分钟");
        holder.imageCheck.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onItemClick(time);
        });
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageCheck;
        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_time_option);
            imageCheck = itemView.findViewById(R.id.image_check);
        }
    }
}