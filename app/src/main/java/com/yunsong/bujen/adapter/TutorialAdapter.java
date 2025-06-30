package com.yunsong.bujen.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.model.CollectItem;

import java.util.List;

public class TutorialAdapter extends BaseAdapter {
    private Context context;
    private List<MyTutorialBean> data;
    private static int selectedPosition = 0;
    private ConfirmDialog dialog;

    private final int layoutId;

    public TutorialAdapter(Context local, List<MyTutorialBean> data,int layoutId) {
        this.context = local;
        this.data = data;
        this.layoutId=layoutId;
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
        if (view == null || !(view.getTag() instanceof viewHolder)) {
            view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
            holder = new viewHolder();
            try { holder.txt_mname = view.findViewById(R.id.txt_mname); } catch (Exception ignored) {}
            try { holder.txt_gdd = view.findViewById(R.id.txt_gdd); } catch (Exception ignored) {}
            try { holder.txt_auther = view.findViewById(R.id.txt_auther); } catch (Exception ignored) {}
            try { holder.txt_hy = view.findViewById(R.id.txt_hy); } catch (Exception ignored) {}
            try { holder.txt_star = view.findViewById(R.id.txt_star); } catch (Exception ignored) {}
            try { holder.txt_time = view.findViewById(R.id.txt_time); } catch (Exception ignored) {}
            try { holder.img_tu = view.findViewById(R.id.img_tu); } catch (Exception ignored) {}
            try { holder.img_selet = view.findViewById(R.id.img_selet); } catch (Exception ignored) {}
            try {holder.txt_description=view.findViewById(R.id.txt_description);} catch (Exception e) {}
            holder.img_cover=view.findViewById(R.id.img_cover);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }
        MyTutorialBean item = data.get(i);
        if (holder.txt_mname != null) {
            holder.txt_mname.setText(item.tutorialName);
        }

        if (holder.txt_auther != null) {
            holder.txt_auther.setText("作者：" + item.author);
        }

        if (holder.txt_gdd != null) {
            holder.txt_gdd.setText("功德消耗：" + item.requiredMeritPoints);
        }

        if (holder.txt_star != null) {
            holder.txt_star.setText(String.format("%.1f", (double) item.rating));
        }


        holder.img_selet.setImageResource(item.sc ? R.drawable.collection_1 : R.drawable.collection_2);


        if(holder.txt_description!=null){
            holder.txt_description.setText(item.description);
        }

        String videoUrl = item.videoUrl;
        if (videoUrl != null && !videoUrl.isEmpty() && holder.img_cover != null) {
            Glide.with(context)
                    .load(videoUrl)
                    .placeholder(R.drawable.detail_bg)
                    .error(R.drawable.detail_bg)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (holder.img_cover instanceof ImageView) {
                                ((ImageView) holder.img_cover).setImageDrawable(resource);
                            } else {
                                holder.img_cover.setBackground(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (holder.img_cover instanceof ImageView) {
                                ((ImageView) holder.img_cover).setImageDrawable(placeholder);
                            } else {
                                holder.img_cover.setBackground(placeholder);
                            }
                        }
                    });
        }

        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname,txt_gdd,txt_hy,txt_auther,txt_star,txt_time,txt_description;
        View img_cover;
    }
    private void setDialog(TextView v) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        v.setText("已拥有");
                        dialog.dismiss();  // 这里添加取消对话框的代码
                    }
                })
                .build();
        dialog.show();
    }
}
