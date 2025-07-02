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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.databean.TutorialBundleBean;
import com.yunsong.bujen.model.CollectItem;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.util.List;

public class TutorialAdapter extends BaseAdapter {
    private Context context;
    private List<?> data;
    private int beanType = 0;
    private ConfirmDialog dialog;

    private final int layoutId;

    public TutorialAdapter(Context local, List<?> data,int layoutId) {
        this.context = local;
        this.data = data;
        this.layoutId=layoutId;

        if (!data.isEmpty()) {
            Object first = data.get(0);
            if (first instanceof MyTutorialBean) {
                beanType = 0;
            } else if (first instanceof TutorialBundleBean) {
                beanType = 1;
            }
        }
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
        if (beanType == 0) {
            MyTutorialBean item = (MyTutorialBean) data.get(i);
            if (holder.txt_mname != null) holder.txt_mname.setText(item.tutorialName);
            if (holder.txt_auther != null) holder.txt_auther.setText("作者：" + item.author);
            if (holder.txt_gdd != null) holder.txt_gdd.setText("功德消耗：" + item.requiredMeritPoints);
            if (holder.txt_star != null) holder.txt_star.setText(String.format("%.1f", (double) item.rating));
            if (holder.txt_description != null) holder.txt_description.setText(item.description);
            if (holder.img_selet != null) holder.img_selet.setImageResource(item.sc ? R.drawable.collection_1 : R.drawable.collection_2);

            holder.img_selet.setOnClickListener(v -> {
                boolean newStatus = !(item.sc != null && item.sc);
                String token = UserInfoUtils.getToken(context);
                int userId = UserInfoUtils.getUserId(context);
                int resourceId = item.tutorialId;
                String resourceType=item.resourceType;

                FavoriteHelper.updateFavoriteStatus(newStatus, token, userId, resourceType, resourceId, new FavoriteHelper.Callback() {
                    @Override
                    public void onSuccess() {
                        item.sc = newStatus;
                        notifyDataSetChanged();
                        Toast.makeText(context, newStatus ? "收藏成功" : "取消收藏", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(context, "收藏失败：" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
            loadImage(holder.img_cover, item.videoUrl);

        } else if (beanType == 1) {
            TutorialBundleBean item = (TutorialBundleBean) data.get(i);
            if (holder.txt_mname != null) holder.txt_mname.setText(item.name);
            if (holder.txt_auther != null) holder.txt_auther.setText("作者：系统");
            if (holder.txt_gdd != null) holder.txt_gdd.setText("功德消耗：" + item.requiredMeritPoints);
            if (holder.txt_description != null) holder.txt_description.setText(item.description);
            if (holder.img_selet != null) holder.img_selet.setImageResource(item.sc ? R.drawable.collection_1 : R.drawable.collection_2);

            // 没有封面图片就跳过
        }

        return view;
    }

    private void loadImage(View view, String url) {
        if (url == null || url.isEmpty() || view == null) return;

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.detail_bg)
                .error(R.drawable.detail_bg)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (view instanceof ImageView) {
                            ((ImageView) view).setImageDrawable(resource);
                        } else {
                            view.setBackground(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (view instanceof ImageView) {
                            ((ImageView) view).setImageDrawable(placeholder);
                        } else {
                            view.setBackground(placeholder);
                        }
                    }
                });
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
