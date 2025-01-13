package com.yunsong.bujen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yunsong.bujen.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<MyData> dataList;

    public MyAdapter(List<MyData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.textView.setText(dataList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
//            textView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null){
                        mListener.onRecyclerItemClick(getAdapterPosition());
                    }
                }
            });
        }
        private OnRecyclerItemClickListener mListener;

        public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
            mListener=listener;
        }

        public interface OnRecyclerItemClickListener{
            void onRecyclerItemClick(int position);
        }
    }

}
