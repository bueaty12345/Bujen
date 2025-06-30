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
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.utils.ExchangeHelper;

import java.util.List;
import java.util.Map;

public class MusicAdapter extends BaseAdapter {
    private Context context;
    private List<MusicBean> data;
    private static int selectedPosition = 0; // 选中的项索引
    private ConfirmDialog dialog;

    public MusicAdapter(Context local, List<MusicBean> data) {
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
            holder.txt_time = view.findViewById(R.id.txt_time);
            holder.txt_star = view.findViewById(R.id.txt_star);
            holder.img_star = view.findViewById(R.id.img_star);
            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        MusicBean musicBean=data.get(i);
        holder.img_star.setImageResource(R.drawable.icon_star);
        holder.txt_mname.setText(musicBean.getMusicName());
        holder.txt_gdd.setText("需功德值："+musicBean.getRequiredMeritPoints().toString());
        holder.txt_auther.setText("作者："+musicBean.getSinger());
        long musicd = musicBean.getDuration();
        holder.txt_time.setText(musicd/60+":"+musicd%60/10+""+musicd%60%10);
        if(musicBean.getRating()==null){
            holder.txt_star.setText("0");
        }else {
            holder.txt_star.setText(musicBean.getRating().toString());
        }
        //判断是否兑换
        boolean dh = ExchangeHelper.isExchanged(context, musicBean.getResourceType(), musicBean.getMusicId());
        holder.txt_hy.setText(dh ? "已兑换" : "待兑换");

        //判断是否收藏
        if(data.get(i).getSC()){
            holder.img_selet.setImageResource(R.drawable.collection_1);
        }else {
            holder.img_selet.setImageResource(R.drawable.collection_2);
        }


        holder.img_selet.setOnClickListener(v -> {
            if(data.get(i).getSC()){
                holder.img_selet.setImageResource(R.drawable.collection_2);
            }
            else {
                holder.img_selet.setImageResource(R.drawable.collection_1);
            }
            notifyDataSetChanged(); // 刷新适配器

        });

        //封面
        String musicCover = musicBean.getMusicCover();
        Log.d("图片","地址"+musicCover);
        if (musicCover != null && !musicCover.isEmpty()) {
            Glide.with(context)
                    .load(musicCover)
                    .placeholder(R.drawable.detail_bg)
                    .error(R.drawable.detail_bg)
                    .into(holder.img_tu);
        }

        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet,img_star;
        TextView txt_mname,txt_gdd,txt_hy,txt_auther,txt_time,txt_star;
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