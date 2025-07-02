package com.yunsong.bujen.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.MyCollect;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.model.CollectItem;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.util.List;

public class MyCollectAdapter extends BaseAdapter {
    private Context context;
    private List<CollectItem> data;
    private static int selectedPosition = 0; // 选中的项索引
    private boolean isRequesting = false; // 防止重复请求
    private ConfirmDialog dialog;

    public MyCollectAdapter(Context context,  List<CollectItem> data) {
        this.context = context;
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
        CollectItem item = data.get(i);
        viewHolder holder;

        if (view == null || !(view.getTag() instanceof viewHolder)) {
            holder = new viewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);

            switch (item.getType()) {
                case 0: // 音乐
                case 1: // 灯光
                    view = inflater.inflate(R.layout.item_collect, viewGroup, false);
                    holder.img_tu = view.findViewById(R.id.img_tu);
                    holder.img_selet = view.findViewById(R.id.img_selet);
                    holder.txt_mname = view.findViewById(R.id.txt_mname);
                    holder.txt_gdd = view.findViewById(R.id.txt_gdd);
                    holder.txt_hy = view.findViewById(R.id.txt_hy);
                    holder.txt_auther = view.findViewById(R.id.txt_auther);
                    holder.txt_star = view.findViewById(R.id.txt_star);
                    holder.txt_time = view.findViewById(R.id.txt_time);
                    break;
                case 2: // 教程
                    view = inflater.inflate(R.layout.item_tutorial, viewGroup, false);
                    holder.img_tu = view.findViewById(R.id.img_rectu);
                    holder.img_selet = view.findViewById(R.id.img_hart);
                    holder.txt_mname = view.findViewById(R.id.txt_name);
                    holder.txt_gdd = view.findViewById(R.id.txt_gdd);
                    holder.txt_auther = view.findViewById(R.id.txt_auther);
                    holder.txt_star = view.findViewById(R.id.txt_star);
                    break;
                case 3: // 祈福
                    view = inflater.inflate(R.layout.item_mypray, viewGroup, false);
                    holder.img_tu = view.findViewById(R.id.img_tu);
                    holder.img_selet = view.findViewById(R.id.love);
                    holder.txt_mname = view.findViewById(R.id.txt_mname);
                    holder.txt_alreadyHave = view.findViewById(R.id.txt_alreadyHave);
                    holder.txt_hy = view.findViewById(R.id.txt_alreadyHave);
                    holder.second_line=view.findViewById(R.id.second_line);
                    break;
            }
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }

        bindData(holder, item);

        holder.img_selet.setImageResource(R.drawable.collection_1);
        holder.img_selet.setOnClickListener(v -> showCancelConfirmDialog(i, item));

        return view;
    }

    private void bindData(viewHolder holder, CollectItem item) {
        switch (item.getType()) {
            case 0:
                MyMusicBean music = (MyMusicBean) item;
                holder.txt_mname.setText(music.musicName);
                holder.txt_auther.setText("作者：" + music.singer);
                holder.txt_gdd.setText("需功德点：" + music.requiredMeritPoints);
                holder.txt_hy.setText(music.dh ? "已拥有" : "待兑换");
                holder.txt_star.setText(String.format("%.1f", music.rating));
                if (holder.txt_time != null) {
                    holder.txt_time.setText(String.format("%d:%02d", music.duration / 60, music.duration % 60));
                }
                Glide.with(context).load(music.musicCover).placeholder(R.drawable.recommend1).into(holder.img_tu);
                break;
            case 1:
                MyLightBean light = (MyLightBean) item;
                holder.txt_mname.setText(light.backgroundName);
                holder.txt_auther.setText("作者：" + light.author);
                holder.txt_gdd.setText("需功德点：" + light.requiredMeritPoints);
                holder.txt_hy.setText(light.dh ? "已拥有" : "待兑换");
                holder.txt_star.setText(String.format("%.1f", light.rating));
                Glide.with(context).load(light.backgroundImageUrl).placeholder(R.drawable.recommend1).into(holder.img_tu);
                break;
            case 2:
                MyTutorialBean tutorial = (MyTutorialBean) item;
                holder.txt_mname.setText(tutorial.tutorialName);
                holder.txt_auther.setText("作者：" + tutorial.author);
                holder.txt_gdd.setText("需功德点：" + tutorial.requiredMeritPoints);
                holder.txt_star.setText(String.format("%.1f", (double) tutorial.rating));
                Glide.with(context).load(tutorial.videoUrl).placeholder(R.drawable.recommend1).into(holder.img_tu);
                break;
            case 3:
                BlessingBean blessing = (BlessingBean) item;
                holder.txt_mname.setText(blessing.blessingTheme);
                holder.txt_alreadyHave.setText(
                        blessing.exchangeQuantity <= 0
                                ? "待兑换"
                                : "已拥有 " + blessing.exchangeQuantity
                );
                holder.second_line.setText(blessing.blessingMethod);
                if (blessing.blessingMethod == null) {
                    holder.second_line.setText("800字限定");
                } else {
                    switch (blessing.blessingMethod) {
                        case "Text":
                            holder.second_line.setText("800字限定");
                            break;
                        case "ImageText":
                            holder.second_line.setText("900字 + 1图限定");
                            break;
                        case "Audio":
                            holder.second_line.setText("6'00\"语音限定");
                            break;
                        default:
                            holder.second_line.setText("800字限定");
                            break;
                    }
                }
                Glide.with(context).load(blessing.blessingBackgroundUrl).placeholder(R.drawable.recommend1).into(holder.img_tu);
                break;
        }
    }

    private void showCancelConfirmDialog(int position, CollectItem item) {
        new AlertDialog.Builder(context)
                .setMessage("确定取消收藏该项目？")
                .setPositiveButton("确定", (dialog, which) -> cancelCollect(position, item.getResourceType(), item.getResourceId()))
                .setNegativeButton("取消", null)
                .show();
    }

    private void cancelCollect(int position, String resourceType, int resourceId) {
        if (isRequesting) return;
        isRequesting = true;

        int userId = UserInfoUtils.getUserId(context);
        String token = UserInfoUtils.getToken(context);

        FavoriteHelper.updateFavoriteStatus(false, token, userId, resourceType, resourceId, new FavoriteHelper.Callback() {
            @Override
            public void onSuccess() {
                isRequesting = false;
                data.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                isRequesting = false;
                Toast.makeText(context, "取消收藏失败：" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname,txt_gdd,txt_hy,txt_auther,txt_star,txt_time,txt_alreadyHave,second_line;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

}