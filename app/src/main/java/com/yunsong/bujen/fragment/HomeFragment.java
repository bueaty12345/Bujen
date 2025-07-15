package com.yunsong.bujen.fragment;

import static android.content.Context.BIND_AUTO_CREATE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import static com.yunsong.bujen.Homepage.isPaused;
import static com.yunsong.bujen.Homepage.txt_gdd;
import static com.yunsong.bujen.fragment.Music.handler;
import static com.thingclips.sdk.blelib.utils.BluetoothUtils.registerReceiver;
import static com.thingclips.sdk.blelib.utils.BluetoothUtils.unregisterReceiver;

import static java.lang.Integer.parseInt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;

import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsong.bujen.Local;
import com.yunsong.bujen.R;
import com.bumptech.glide.Glide;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.utils.DataStorageUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    TextView txt_musicName,txt_siger;
    static ImageView img_bf;
    ImageView img_dg,img_list,img_qiao,img_y_setting;
    View view;
    LinearLayout lin_setting;
    public static LinearLayout lin_dg;
    public static int gdd_cont=0;
    public static int Mi=0;
//    private Handler handler = new Handler();  // 用于更新 UI
    private MusicService.MusicControl musicControl;
    private MyServiceConn conn;
    private Intent intent1,intent2;
    boolean isPlaying;
    private float initialY;
    //记录服务是否被解绑，默认没有
    private boolean isUnbind =false;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SettingsViewModel sharedViewModel;
    private static final int REQUEST_CODE_LOCAL = 1001;
    private MyMusicBean restoredMusic = null;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 定时更新数据
            updateData();

            // 每1秒再次执行
            handler.postDelayed(this, 1000);
        }
    };

    private void updateData() {
        if (getContext() == null) return;

        int localGddCount = DataStorageUtils.getGddCount(requireContext());

        int currentUiGdd = Integer.parseInt(txt_gdd.getText().toString());

        if (currentUiGdd < localGddCount) {
            createBubbleTextView();
            txt_gdd.setText(String.valueOf(localGddCount));
        }

        // 更新 ViewModel 中的数据（用于数据共享）
        sharedViewModel.setGddCont(localGddCount);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view=inflater.inflate(R.layout.fragment_home, container, false);
        txt_musicName=view.findViewById(R.id.txt_musicName);
        txt_siger=view.findViewById(R.id.txt_siger);
        img_dg=view.findViewById(R.id.img_musicImg);
        img_bf=view.findViewById(R.id.img_bofang);
        img_list=view.findViewById(R.id.img_list);
        img_y_setting=view.findViewById(R.id.img_y_setting);
        img_qiao = view.findViewById(R.id.img_qiao);
        lin_dg=view.findViewById(R.id.lin_dg);
        lin_setting=view.findViewById(R.id.lin_setting);
//        lin_dg.setBackgroundResource(dg_bg);
        txt_gdd.setText(gdd_cont+"");
        //创建一个意图对象，是从当前的Activity跳转到Service
        intent2 = new Intent(getContext(), MusicService.class);
        getContext().startService(intent2); // 先启动服务，防止首次 bind 失败
        conn = new MyServiceConn();         // 初始化连接对象
        getContext().bindService(intent2, conn, Context.BIND_AUTO_CREATE); // 再绑定

        // 播放按钮
        img_bf.setOnClickListener(this);
        //敲木鱼
        img_qiao.setOnClickListener(this);
        //跳转本地列表
        img_list.setOnClickListener(this);
        img_y_setting.setOnClickListener(this);
        lin_setting.setOnClickListener(this);
        Glide.with(this)
                .asGif()
                .load(R.drawable.muyu) // 或者使用网络链接
                .into(img_qiao);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        // 开始更新
        handler.post(runnable);

        restoreAndPlayMusicIfNeeded();

        return view;
    }

    private void restoreAndPlayMusicIfNeeded() {
        SharedPreferences prefs = requireContext().getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        String name = prefs.getString("musicName", null);
        String singer = prefs.getString("singer", null);
        String cover = prefs.getString("cover", null);
        String url = prefs.getString("url", null);

        if (name != null && url != null) {
            MyMusicBean music = new MyMusicBean();
            music.musicName = name;
            music.singer = singer;
            music.musicCover = cover;
            music.musicUrl = url;

            restoredMusic = music;
            updateMusicUI(music);
//            playMusic(music);//重进后自动播放

            // 绑定成功后才可以播放
//            if (musicControl != null) {
//                musicControl.playFromUrl(music.musicUrl);
//                img_bf.setImageResource(R.drawable.home_start);
//            }
        }
    }


    private void incrementScore() {

//            gdd_cont++;
            txt_gdd.setText(gdd_cont+"");
            createBubbleTextView();
    }

    @SuppressLint("ResourceType")
    public void createBubbleTextView() {//增加‘+1’气泡动画
        final TextView bubbleTextView = new TextView(getActivity());//通过代码动态创建了一个 TextView，用于显示 +1
        bubbleTextView.setText("+1");//设置文字显示为“+1”
        bubbleTextView.setTextSize(30);
        bubbleTextView.setX(img_qiao.getX() + 600);//位置
        bubbleTextView.setY(img_qiao.getY() - 100);
        bubbleTextView.setAlpha(1f);//透明度，1为不透明
        ((ViewGroup) view.findViewById(R.id.lin_111)).addView(bubbleTextView);

        // 动画效果，上升动画，Y坐标
        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(bubbleTextView, "translationY", -500f);
        moveAnimator.setDuration(1500);

        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(bubbleTextView, "alpha", 1f, 0f);
        fadeAnimator.setDuration(2000);

        moveAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ((ViewGroup)view. findViewById(R.id.lin_111)).removeView(bubbleTextView);//监听事件，结束后移除动画
            }
        });
        // 创建震动动画
        TranslateAnimation shake = new TranslateAnimation(0, 0, -10f, 10f);
        shake.setDuration(100); // 动画持续时间
        shake.setRepeatCount(3); // 重复次数
        shake.setRepeatMode(Animation.REVERSE); // 反向播放
        img_qiao.startAnimation(shake); // 启动动画
        // 启动两个动画
        moveAnimator.start();
        fadeAnimator.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_bofang:
                if (musicControl != null) {
                    if (musicControl.isPlay()) {
                        musicControl.pausePlay();
                        img_bf.setImageResource(R.drawable.home_stop);
                    } else {
                        if (musicControl.hasPrepared()) {
                            // 如果之前已经设置过播放源，直接继续播放
                            musicControl.continuePlay();
                        } else if (restoredMusic != null) {
                            // 没有准备，重新设置播放源
                            musicControl.playFromUrl(restoredMusic.musicUrl);
                        } else {
                            Toast.makeText(getContext(), "未选择音乐", Toast.LENGTH_SHORT).show();
                        }
                        img_bf.setImageResource(R.drawable.home_start);
                    }
                }
                break;
            case R.id.img_qiao: incrementScore();break;
            case R.id.img_list:
                startActivityForResult(new Intent(getContext(), Local.class), REQUEST_CODE_LOCAL);
                break;
            case R.id.img_y_setting: case R.id.lin_setting:
//                startActivity(new Intent(getContext(), Music.class));
//                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);//入场/出场动画
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL && resultCode == Activity.RESULT_OK && data != null) {
            MyMusicBean music = (MyMusicBean) data.getSerializableExtra("selectedMusic");
            if (music != null) {
                updateMusicUI(music);
                playMusic(music);
            }
        }
    }

    private void updateMusicUI(MyMusicBean music) {
        TextView txtMusicName = view.findViewById(R.id.txt_musicName);
        TextView txtSiger = view.findViewById(R.id.txt_siger);
        ImageView imgMusicImg = view.findViewById(R.id.img_musicImg);

        txtMusicName.setText(music.musicName);
        txtSiger.setText(music.singer);
        Glide.with(this).load(music.musicCover).into(imgMusicImg);
    }

    private void playMusic(MyMusicBean music) {
        if (musicControl != null) {
            musicControl.playFromUrl(music.musicUrl);
            img_bf.setImageResource(R.drawable.home_start);

            // 保存当前播放音乐信息
            saveCurrentMusicToPrefs(music);
        }
    }

    private void saveCurrentMusicToPrefs(MyMusicBean music) {
        SharedPreferences prefs = requireContext().getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("musicName", music.musicName)
                .putString("singer", music.singer)
                .putString("cover", music.musicCover)
                .putString("url", music.musicUrl)
                .apply();
    }

    //用于实现连接服务，比较模板化，不需要详细知道内容,用来连接和管理后台音乐播放的 MusicService 服务
    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl = (MusicService.MusicControl) service;//绑定到后台的音乐服务（MusicService），然后通过获取到的 MusicControl 对象，实现了对音乐的控制功能
            MusicController.getInstance().setMusicControl(musicControl);

            restoreAndPlayMusicIfNeeded();
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }
    //判断服务是否被解绑
    private void unbind(boolean isUnbind){
        //如果解绑了
        if(!isUnbind){
            musicControl.pausePlay();//音乐暂停播放
            getContext().unbindService(conn);//解绑服务
        }
    }
    private BroadcastReceiver playbackStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isPlaying = intent.getBooleanExtra("isPlaying", false);
            // 更新 UI 或处理播放状态
        }
    };

    // 在合适的地方注册接收器
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("ACTION_PLAYBACK_STATUS");
        registerReceiver(playbackStatusReceiver, filter);
    }

    // 不再需要时注销接收器
    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(playbackStatusReceiver);
    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//       unbind(isUnbind);//解绑服务
//    }



}