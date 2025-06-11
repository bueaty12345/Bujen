package com.yunsong.bujen.adapter;

import static com.yunsong.bujen.fragment.HomeFragment.Mi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.Local;
import com.yunsong.bujen.MyTutorial;
import com.yunsong.bujen.R;
import com.yunsong.bujen.fragment.MusicController;
import com.yunsong.bujen.fragment.MusicService;

import java.util.List;
import java.util.Map;

public class MyTutorialAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> data;

    public MyTutorialAdapter(MyTutorial local, List<Map<String, Object>> data) {
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
        MyTutorialAdapter.viewHolder holder;
        if (view == null) {
            holder = new MyTutorialAdapter.viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_mytutorial, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_tu);
            holder.img_selet = view.findViewById(R.id.img_selet);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            view.setTag(holder);
        }else {
            holder = (MyTutorialAdapter.viewHolder) view.getTag();
        }
        Map<String, Object> map=data.get(i);
        holder.txt_mname.setText((String) map.get("name"));
        holder.img_tu.setImageResource((int) map.get("image"));
        // 设置背景颜色
//        if (i == Mi) {
//            holder.img_selet.setVisibility(View.VISIBLE);
//        } else {
//            holder.img_selet.setVisibility(View.INVISIBLE);
//        }
        // 设置点击事件
        view.setOnClickListener(v -> {
            notifyDataSetChanged(); // 刷新适配器
//            Mi=i;
//            MusicService.MusicControl control = MusicController.getInstance().getMusicControl();
//            if (control != null) {
//                control.play(Mi);
//            }

        });
        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet;
        TextView txt_mname;
    }

}
