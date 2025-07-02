package com.yunsong.bujen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.util.List;

public class TutorialSquareAdapter extends BaseAdapter {
    private Context context;
    private List<MyTutorialBean> data;
    private LayoutInflater inflater;

    public TutorialSquareAdapter(Context context, List<MyTutorialBean> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txt_name, txt_gdd, txt_auther, txt_star;
        ImageView img_hart;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_tutorial, parent, false);
            holder = new ViewHolder();
            holder.txt_name = convertView.findViewById(R.id.txt_name);
            holder.txt_gdd = convertView.findViewById(R.id.txt_gdd);
            holder.txt_auther = convertView.findViewById(R.id.txt_auther);
            holder.txt_star = convertView.findViewById(R.id.txt_star);
            holder.img_hart = convertView.findViewById(R.id.img_hart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyTutorialBean bean = data.get(position);
        holder.txt_name.setText(bean.tutorialName);
        holder.txt_gdd.setText("功德值：" + bean.requiredMeritPoints);
        holder.txt_auther.setText("作者：" + bean.author);
        holder.txt_star.setText(String.format("%.1f", (double) bean.rating));

        if (bean.sc != null && bean.sc) {
            holder.img_hart.setImageResource(R.drawable.collection_1);
        } else {
            holder.img_hart.setImageResource(R.drawable.collection_2);
        }

        holder.img_hart.setOnClickListener(v -> {
            boolean newStatus = !(bean.sc != null && bean.sc);
            String token = UserInfoUtils.getToken(context);
            int userId = UserInfoUtils.getUserId(context);  // 你项目中的用户 ID 获取方法
            int resourceId = bean.tutorialId;
            String resourceType=bean.resourceType;

            FavoriteHelper.updateFavoriteStatus(newStatus, token, userId, resourceType, resourceId, new FavoriteHelper.Callback() {
                @Override
                public void onSuccess() {
                    bean.sc = newStatus;
                    notifyDataSetChanged();
                    Toast.makeText(context, newStatus ? "收藏成功" : "取消收藏", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(context, "收藏失败：" + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        return convertView;
    }

}
