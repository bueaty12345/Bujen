package com.yunsong.bujen.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yunsong.bujen.Lighting;
import com.yunsong.bujen.Prays;
import com.yunsong.bujen.R;
import com.yunsong.bujen.Supplies;
import com.yunsong.bujen.Tutorial;
import com.yunsong.bujen.adapter.LightAdapter;
import com.yunsong.bujen.adapter.MusicAdapter;
import com.yunsong.bujen.adapter.MyAdapter;
import com.yunsong.bujen.adapter.MyCollectAdapter;
import com.yunsong.bujen.adapter.MyData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.yunsong.bujen.adapter.MyTutorialAdapter;
import com.yunsong.bujen.adapter.TutorialAdapter;
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.ApiHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CenterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CenterFragment extends Fragment implements View.OnClickListener, OnBannerListener {
    LinearLayout lay_music,lay_jiaocheng,lay_qifu,lay_haocai;
    TabLayout tabLayout;
    ListView listView;
    Banner banner;

    int position=0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CenterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CenterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CenterFragment newInstance(String param1, String param2) {
        CenterFragment fragment = new CenterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_center, container, false);
        lay_music=view.findViewById(R.id.lay_music);
        lay_jiaocheng=view.findViewById(R.id.lay_jiaocheng);
        lay_qifu=view.findViewById(R.id.lay_qifu);
        lay_haocai=view.findViewById(R.id.lay_haocai);
        tabLayout=view.findViewById(R.id.tab_center);
        listView=view.findViewById(R.id.list_center);
        banner=view.findViewById(R.id.banner);
        lay_music.setOnClickListener(this);
        lay_jiaocheng.setOnClickListener(this);
        lay_qifu.setOnClickListener(this);
        lay_haocai.setOnClickListener(this);

        setSound();
        List<MyData> myDataList = new ArrayList<>();
        myDataList.add(new MyData("Item 1"));
        myDataList.add(new MyData("Item 2"));
        myDataList.add(new MyData("Item 2"));
        myDataList.add(new MyData("Item 2"));
// 添加更多数据...

        RecyclerView recyclerView = view.findViewById(R.id.horizontalRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        MyAdapter adapter = new MyAdapter(myDataList);
        recyclerView.setAdapter(adapter);

        List<Integer> imgList = new ArrayList<>();

        // 添加本地图片资源ID到 imgList 中
        imgList.add(R.drawable.banner1);  // 替换为你本地图片的资源ID
        imgList.add(R.drawable.banner2);
        // 设置 Banner 适配器
        banner.setAdapter(new BannerImageAdapter<Integer>(imgList) {
                    @Override
                    public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                        // 使用 Glide 加载本地资源图片
                        Glide.with(holder.itemView)
                                .load(data) // 加载本地资源图片
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30))) // 圆角效果
                                .into(holder.imageView);
                    }
                }).setIndicator(new CircleIndicator(getContext())) // 设置圆形指示器
                .setLoopTime(1000) // 设置轮播时间为1秒
                .setOnBannerListener(this); // 设置点击监听器


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当选项卡被选中时更新 TextView 内容
                switch (tab.getPosition()) {
                    case 0:
                        setSound();//音乐
                        position=0;
                        break;
                    case 1:
                        setLamplight();//灯光
                        position=1;
                        break;
                    case 2:
                        setTutorial();//教程
                        position=1;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void setTutorial() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);
        ApiHelper.fetchTutorialList(getContext(), token, new ApiHelper.Callback<MyTutorialBean>() {
            @Override
            public void onSuccess(List<MyTutorialBean> list) {
                TutorialAdapter adapter = new TutorialAdapter(getContext(), list,R.layout.item_lighting);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLamplight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);
        ApiHelper.fetchLightList(getContext(), token, new ApiHelper.Callback<MyLightBean>() {
            @Override
            public void onSuccess(List<MyLightBean> list) {
                LightAdapter adapter = new LightAdapter(getContext(), list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSound() {
        SharedPreferences sharedPreferences =  getContext().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);  // 从 SharedPreferences 获取 token
        ApiHelper.fetchMusicList(getContext(), token, new ApiHelper.Callback<MusicBean>() {
            @Override
            public void onSuccess(List<MusicBean> list) {
                MusicAdapter adapter = new MusicAdapter(getContext(), list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lay_music:
                startActivity(new Intent(getActivity(), Lighting.class));break;
            case R.id.lay_jiaocheng:
                startActivity(new Intent(getActivity(), Tutorial.class));break;
            case R.id.lay_qifu:
                startActivity(new Intent(getActivity(), Prays.class));break;
            case R.id.lay_haocai:
                startActivity(new Intent(getActivity(), Supplies.class));break;
        }
    }

    @Override
    public void OnBannerClick(Object data, int position) {
        Log.i("tag", "你点了第" + position + "张轮播图");
    }

}