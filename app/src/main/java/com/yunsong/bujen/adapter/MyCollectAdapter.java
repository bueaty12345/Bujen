package com.yunsong.bujen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.MyCollect;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.model.CollectItem;

import java.util.List;

public class MyCollectAdapter extends BaseAdapter {
    private Context context;
    private List<CollectItem> data;
    private static int selectedPosition = 0; // 选中的项索引
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
        int type = item.getType();

        if (type == 0) {
            // Music
            MyMusicBean music = (MyMusicBean) item;
            viewHolder holder;
            if (view == null || !(view.getTag() instanceof viewHolder)) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_collect, viewGroup, false);
                holder.img_tu = view.findViewById(R.id.img_tu);
                holder.img_selet = view.findViewById(R.id.img_selet);
                holder.txt_mname = view.findViewById(R.id.txt_mname);
                holder.txt_gdd = view.findViewById(R.id.txt_gdd);
                holder.txt_hy = view.findViewById(R.id.txt_hy);
                holder.txt_auther = view.findViewById(R.id.txt_auther);
                holder.txt_star=view.findViewById(R.id.txt_star);
                holder.txt_time=view.findViewById(R.id.txt_time);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            holder.txt_mname.setText(music.musicName);
            holder.txt_auther.setText("作者：" + music.singer);
            holder.txt_gdd.setText("需功德点：" + music.requiredMeritPoints);
            holder.txt_hy.setText(music.dh ? "已拥有" : "待兑换");
            holder.txt_star.setText(String.format("%.1f", music.rating));
            String timeText = String.format("%d:%02d", music.duration / 60, music.duration % 60);
            holder.txt_time.setText(timeText);

            Glide.with(context)
                    .load(music.musicCover)
                    .placeholder(R.drawable.recommend1)
                    .into(holder.img_tu);

//            holder.img_selet.setImageResource(music.sc ? R.drawable.collection_1 : R.drawable.collection_2);
            holder.img_selet.setImageResource(R.drawable.collection_1);

        } else if (type == 1) {
            MyLightBean light=(MyLightBean) item;
            viewHolder holder;
            if (view == null || !(view.getTag() instanceof viewHolder)) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_collect, viewGroup, false);
                holder.img_tu = view.findViewById(R.id.img_tu);
                holder.img_selet = view.findViewById(R.id.img_selet);
                holder.txt_mname = view.findViewById(R.id.txt_mname);
                holder.txt_gdd = view.findViewById(R.id.txt_gdd);
                holder.txt_hy = view.findViewById(R.id.txt_hy);
                holder.txt_auther = view.findViewById(R.id.txt_auther);
                holder.txt_star=view.findViewById(R.id.txt_star);
                holder.txt_time=view.findViewById(R.id.txt_time);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            holder.txt_mname.setText(light.backgroundName);
            holder.txt_auther.setText("作者：" + light.author);
            holder.txt_gdd.setText("需功德点：" + light.requiredMeritPoints);
            holder.txt_hy.setText(light.dh ? "已拥有" : "待兑换");
            holder.txt_star.setText(String.format("%.1f", light.rating));

            Glide.with(context)
                    .load(light.backgroundImageUrl)
                    .placeholder(R.drawable.recommend1)
                    .into(holder.img_tu);

//            holder.img_selet.setImageResource(light.sc ? R.drawable.collection_1 : R.drawable.collection_2);
            holder.img_selet.setImageResource(R.drawable.collection_1);

        } else if (type == 2) {
            MyTutorialBean tutorial = (MyTutorialBean) item;
            viewHolder holder;
            if (view == null || !(view.getTag() instanceof viewHolder)) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_tutorial, viewGroup, false);
                holder.img_tu = view.findViewById(R.id.img_rectu);
                holder.img_selet = view.findViewById(R.id.img_hart);
                holder.txt_mname = view.findViewById(R.id.txt_name);
                holder.txt_gdd = view.findViewById(R.id.txt_gdd);
                holder.txt_auther = view.findViewById(R.id.txt_auther);
                holder.txt_star=view.findViewById(R.id.txt_star);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            holder.txt_mname.setText(tutorial.tutorialName);
            holder.txt_auther.setText("作者：" + tutorial.author);
            holder.txt_gdd.setText("需功德点：" + tutorial.requiredMeritPoints);

            holder.txt_star.setText(String.format("%.1f", (double) tutorial.rating));

//        Glide.with(context)
//                .load(item.musicCover)
//                .placeholder(R.drawable.recommend1)
//                .into(holder.img_tu);

//            holder.img_selet.setImageResource(tutorial.sc ? R.drawable.collection_1 : R.drawable.collection_2);
            holder.img_selet.setImageResource(R.drawable.collection_1);


        }else if(type==3) {
            BlessingBean blessing=(BlessingBean) item;
            viewHolder holder;
            if(view==null || !(view.getTag() instanceof viewHolder)){
                holder=new viewHolder();
                view=LayoutInflater.from(context).inflate(R.layout.item_mypray,viewGroup,false);
                holder.img_tu = view.findViewById(R.id.img_tu);
                holder.img_selet = view.findViewById(R.id.love);
                holder.txt_mname = view.findViewById(R.id.txt_mname);
                holder.txt_gdd = view.findViewById(R.id.txt_gdd);
                holder.txt_hy = view.findViewById(R.id.txt_alreadyHave);
                view.setTag(holder);
            }else {
                holder=(viewHolder) view.getTag();
            }
            holder.txt_mname.setText(blessing.blessingTheme);
//            holder.txt_hy.setText(blessing.dh == 0 ? "待兑换" : "已拥有");
//            holder.txt_star.setText(String.format("%.1f", blessing.rating));


            Glide.with(context)
                    .load(blessing.blessingBackgroundUrl)
                    .placeholder(R.drawable.recommend1)
                    .into(holder.img_tu);

//            holder.img_selet.setImageResource(blessing.sc ? R.drawable.collection_1 : R.drawable.collection_2);
            holder.img_selet.setImageResource(R.drawable.collection_1);


        }

        return view;
    }

    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname,txt_gdd,txt_hy,txt_auther,txt_star,txt_time;
    }
//    private void showDialog(TextView v, MyMusicBean item) {
//        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
//        dialog = builder.cancelTouchout(false)
//                .view(R.layout.dialog_confirm)
//                .style(R.style.Dialog)
//                .addViewOnclick(R.id.txt_confirm, view -> {
//                    item.dh = 1;
//                    v.setText("已拥有");
//                    dialog.dismiss();
//                }).build();
//        dialog.show();
//    }
    private void showDialog2(TextView v, MyLightBean item) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, view -> {
                    item.dh = true;
                    v.setText("已拥有");
                    dialog.dismiss();
                }).build();
        dialog.show();
    }
}