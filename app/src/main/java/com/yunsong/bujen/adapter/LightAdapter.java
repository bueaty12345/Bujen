package com.yunsong.bujen.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.utils.ExchangeHelper;

import java.util.List;
import java.util.Map;

public class LightAdapter extends BaseAdapter {
    private Context context;
    private List<MyLightBean> data;
    private static int selectedPosition = 0; // 选中的项索引
    private ConfirmDialog dialog;

    public LightAdapter(Context local, List<MyLightBean> data) {
        this.context = local;
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
        viewHolder holder;
        if (view == null) {
            holder = new viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_lighting, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_cover);
            holder.img_selet = view.findViewById(R.id.img_selet);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            holder.txt_gdd = view.findViewById(R.id.txt_gdd);
            holder.txt_hy = view.findViewById(R.id.txt_hy);
            holder.txt_auther = view.findViewById(R.id.txt_auther);
            holder.txt_star=view.findViewById(R.id.txt_star);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }

        MyLightBean item = data.get(i);
        holder.txt_mname.setText(item.getBackgroundName());
        holder.txt_gdd.setText("需功德点：" + item.getRequiredMeritPoints());
        holder.txt_auther.setText("作者：" + item.getAuthor());
        if(item.getRating()==null){
            holder.txt_star.setText("0");
        }else {
            holder.txt_star.setText(item.getRating().toString());
        }

        holder.img_selet.setImageResource(item.isSc() ? R.drawable.collection_1 : R.drawable.collection_2);

        holder.img_selet.setOnClickListener(v -> {
            item.setSc(!item.isSc());
            notifyDataSetChanged();
        });

        boolean dh = ExchangeHelper.isExchanged(context, item.getResourceType(), item.getBackgroundId());
        holder.txt_hy.setText(dh ? "已兑换" : "待兑换");


//        holder.img_tu.setImageResource(item.getBackgroundImageUrl());
        //封面
        String backgroundImageUrl = item.getBackgroundImageUrl();
        Log.d("图片","地址"+backgroundImageUrl);
        if (backgroundImageUrl != null && !backgroundImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(backgroundImageUrl)
                    .placeholder(R.drawable.detail_bg)
                    .error(R.drawable.detail_bg)
                    .into(holder.img_tu);
        }


        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname,txt_gdd,txt_hy,txt_auther,txt_star;
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
