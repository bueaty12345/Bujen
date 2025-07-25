package com.yunsong.bujen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.MeditationActivity;
import com.yunsong.bujen.MyTutorial;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.ModuleItem;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeditationModuleAdapter extends RecyclerView.Adapter<MeditationModuleAdapter.ViewHolder> {
    private List<ModuleItem> data;
    private Context context;
    private final String RECORD_HISTORY= BuildConfig.API_SERVER+"/system/recordh";

    public MeditationModuleAdapter(Context context, List<ModuleItem> data) {
        this.context = context;
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        ImageView img1, img2;
        TextView title1, author1, title2, author2;
        LinearLayout item1,item2,more_data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);

            img1 = itemView.findViewById(R.id.img_thumb1);
            img2 = itemView.findViewById(R.id.img_thumb2);
            title1 = itemView.findViewById(R.id.tv_item_title1);
            author1 = itemView.findViewById(R.id.tv_item_author1);
            title2 = itemView.findViewById(R.id.tv_item_title2);
            author2 = itemView.findViewById(R.id.tv_item_author2);
            item1=itemView.findViewById(R.id.item1);
            item2=itemView.findViewById(R.id.item2);
            more_data=itemView.findViewById(R.id.more_data);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModuleItem item = data.get(position);

        holder.tvTitle.setText(item.title);
        holder.tvSubtitle.setText(item.subtitle);

        if (item.tutorials.size() > 0) {
            MyTutorialBean t1 = item.tutorials.get(0);
            holder.title1.setText(t1.tutorialName);
            holder.author1.setText(t1.author);
            Glide.with(context).load(t1.backgroundMusicUrl).into(holder.img1);
        }else {
            holder.title1.setText("");
            holder.author1.setText("");
            holder.img1.setImageDrawable(null);
        }

        if (item.tutorials.size() > 1) {
            MyTutorialBean t2 = item.tutorials.get(1);
            holder.title2.setText(t2.tutorialName);
            holder.author2.setText(t2.author);
            Glide.with(context).load(t2.backgroundMusicUrl).into(holder.img2);
        } else {
            holder.title2.setText("");
            holder.author2.setText("");
            holder.img2.setImageDrawable(null);
        }

        Integer userId=UserInfoUtils.getUserId(context);

        holder.item1.setOnClickListener(v -> {
            if (item.tutorials.size() > 0 && context instanceof Activity) {
                if (userId != null) {
                    recordMeditationHistory(userId,item.tutorials.get(0).tutorialId);
                } else {
                    Log.e("Meditation", "用户ID为null，无法记录冥想历史");
                }

                Intent result = new Intent();
                result.putExtra("tutorialName", item.tutorials.get(0).tutorialName);
                result.putExtra("videoUrl", item.tutorials.get(0).videoUrl);
                result.putExtra("backgroundMusicUrl", item.tutorials.get(0).backgroundMusicUrl);
                ((Activity) context).setResult(Activity.RESULT_OK, result);
                ((Activity) context).finish(); // 回传数据并关闭 MedLocad
            }
        });

        holder.item2.setOnClickListener(v -> {
            if (item.tutorials.size() > 1 && context instanceof Activity) {
                MyTutorialBean t2 = item.tutorials.get(1);
                if (userId != null) {
                    recordMeditationHistory(userId, t2.tutorialId);
                } else {
                    Log.e("Meditation", "用户ID为null，无法记录冥想历史");
                }

                Intent result = new Intent();
                result.putExtra("tutorialName", item.tutorials.get(1).tutorialName);
                result.putExtra("videoUrl", item.tutorials.get(1).videoUrl);
                result.putExtra("backgroundMusicUrl", item.tutorials.get(1).backgroundMusicUrl);
                ((Activity) context).setResult(Activity.RESULT_OK, result);
                ((Activity) context).finish(); // 回传数据并关闭 MedLocad
            }
        });

        holder.more_data.setOnClickListener(v->{
            Context context = v.getContext();
            Intent intent = new Intent(context, MyTutorial.class);
            context.startActivity(intent);
        });
    }

    private void recordMeditationHistory(int userId, int tutorialId) {
        OkHttpClient client = new OkHttpClient();
        String token= UserInfoUtils.getToken(context);

        // 当前时间
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("tutorialId", tutorialId);
            jsonObject.put("watchTime", currentTime);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(RECORD_HISTORY)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Meditation", "记录冥想失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Meditation", "记录冥想成功"+response);
                } else {
                    Log.w("Meditation", "记录冥想失败: " + response.code());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
