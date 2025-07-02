package com.yunsong.bujen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder>{
    private List<CalendarDay> days = new ArrayList<>();

    public void setData(List<CalendarDay> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDay day = days.get(position);

        if (day.day == 0) {
            // 空白项
            holder.tvDay.setText("");
            holder.tvDay.setVisibility(View.INVISIBLE);
            holder.bgToday.setVisibility(View.GONE);
            holder.tvPray.setVisibility(View.GONE);
        } else if (day.hasPray) {
            holder.tvDay.setText("");
            holder.tvDay.setVisibility(View.INVISIBLE);
            holder.tvPray.setVisibility(View.VISIBLE);
            holder.bgToday.setVisibility(View.GONE);
        } else {
            // 正常日期
            holder.tvDay.setText(String.valueOf(day.day));
            holder.tvDay.setVisibility(View.VISIBLE);
            holder.tvPray.setVisibility(View.GONE);
            holder.bgToday.setVisibility(day.isToday ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View bgToday;
        ImageView tvPray;

        DayViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvPray = itemView.findViewById(R.id.tv_pray);
            bgToday = itemView.findViewById(R.id.bg_today);
        }
    }
}
