package com.yunsong.bujen.adapter;

import static com.yunsong.bujen.fragment.HomeFragment.Mi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.MyPray;
import com.yunsong.bujen.MyTutorial;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.databean.MyPrayBean;
import com.yunsong.bujen.fragment.MusicController;
import com.yunsong.bujen.fragment.MusicService;

import java.util.List;
import java.util.Map;

public class MyPrayAdapter extends BaseAdapter {
    private Context context;
    private List<BlessingBean> data;

    public MyPrayAdapter(Context context, List<BlessingBean> data) {
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
        MyPrayAdapter.viewHolder holder;
        if (view == null) {
            holder = new MyPrayAdapter.viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_mypray, viewGroup, false);
            holder.img_tu = view.findViewById(R.id.img_tu);
            holder.txt_mname = view.findViewById(R.id.txt_mname);
            holder.second_line=view.findViewById(R.id.second_line);
            holder.txt_alreadyHave=view.findViewById(R.id.txt_alreadyHave);
            holder.love=view.findViewById(R.id.love);
            view.setTag(holder);
        }else {
            holder = (MyPrayAdapter.viewHolder) view.getTag();
        }
        BlessingBean item = data.get(i);
        holder.txt_mname.setText(item.blessingTheme);
        Glide.with(context)
                .load(item.blessingBackgroundUrl)
                .placeholder(R.drawable.recommend1)
                .into(holder.img_tu);

        holder.second_line.setText(item.blessingMethod);//Text,ImageText,Audio
        switch (item.blessingMethod) {
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

        holder.txt_alreadyHave.setText("已有"+item.exchangeQuantity);
        holder.love.setImageResource(item.sc?R.drawable.collection_1 :R.drawable.collection_2);

        return view;
    }
    private final class viewHolder {
        ImageView img_tu,img_selet,love;
        TextView txt_mname,second_line,txt_alreadyHave;
    }

}
