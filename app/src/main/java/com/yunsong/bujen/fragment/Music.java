package com.yunsong.bujen.fragment;

import static com.yunsong.bujen.Homepage.isPaused;
import static com.yunsong.bujen.fragment.HomeFragment.Mi;
import static com.yunsong.bujen.fragment.HomeFragment.img_bf;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.Local;
import com.yunsong.bujen.R;

public class Music extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back;
    ImageView img_sc;
    ImageView img_fx;
    ImageView img_circle;
    public static ImageView img_bf1;
    ImageView img_reset;
    ImageView img_previous;
    ImageView img_next;
    ImageView img_list;
    static SeekBar seekBar;
    public static TextView txt_stateTime,txt_endTime;
    LinearLayout lin_mm;
    MusicService.MusicControl control;
    ObjectAnimator rotateAnimator;
    private float initialY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    void init(){
        img_back=findViewById(R.id.img_back);
        img_sc=findViewById(R.id.img_sc);
        img_fx=findViewById(R.id.img_fx);
        img_circle=findViewById(R.id.img_circle);
        img_bf1=findViewById(R.id.img_bf);
        img_reset=findViewById(R.id.img_reset);
        img_previous=findViewById(R.id.img_previous);
        img_next=findViewById(R.id.img_next);
        img_list=findViewById(R.id.img_list);
        seekBar=findViewById(R.id.seekBar);
        txt_stateTime=findViewById(R.id.txt_stateTime);
        txt_endTime=findViewById(R.id.txt_endTime);
        lin_mm=findViewById(R.id.lin_mm);
        control = MusicController.getInstance().getMusicControl();


        img_back.setOnClickListener(this);
        img_sc.setOnClickListener(this);
        img_fx.setOnClickListener(this);
        img_bf1.setOnClickListener(this);
        img_reset.setOnClickListener(this);
        img_previous.setOnClickListener(this);
        img_next.setOnClickListener(this);
        img_list.setOnClickListener(this);


        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(Music.this, "生活节奏已经很快啦，不要操之过急哦", Toast.LENGTH_SHORT).show();
                return true; // 返回 true 表示消费事件，用户无法调节进度
            }
        });

        lin_mm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录触摸点的Y坐标
                        initialY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getY() - initialY;
                        // 判断是否为下滑事件
                        if (deltaY > 100) {  // 当滑动距离超过阈值（比如100px）
                            finish();
                            overridePendingTransition(R.anim.slide2_in_up, R.anim.slide2_out_down);
                        }
                        break;
                }
                return true;
            }
        });
        rotateAnimator = ObjectAnimator.ofFloat(img_circle, "rotation", 0f, 360f);
        rotateAnimator.setDuration(3000); // 旋转的持续时间，单位毫秒
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE); // 无限循环
        rotateAnimator.setInterpolator(new LinearInterpolator()); // 设置匀速旋转

        if(control.isPlay()){
            rotateAnimator.start();
        }else {
            rotateAnimator.pause();
        }
    }
    //handler机制，可以理解为线程间的通信，我获取到一个信息，然后把这个信息告诉你，就这么简单
    @SuppressLint("HandlerLeak")
    public static Handler handler=new Handler(){//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition和总时长duration
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            if(seekBar!=null){
                //对进度条进行设置
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
            }
            //歌曲是多少分钟多少秒钟
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+"";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+"";
            }
            //这里就显示了歌曲总时长
            if(txt_endTime!=null){
                txt_endTime.setText(strMinute+":"+strSecond);
            }

            //歌曲当前播放时长
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+" ";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+" ";
            }
            //显示当前歌曲已经播放的时间
            if(txt_stateTime!=null){
                txt_stateTime.setText(strMinute+":"+strSecond);
            }
            //设置播放按键
            if(bundle.getBoolean("play"))
            {
                if(img_bf!=null){
                    img_bf.setImageResource(R.drawable.home_start);// 音频正在播放
                }

                if(img_bf1!=null){
                    img_bf1.setImageResource(R.drawable.home_start);// 音频正在播放
                }

            }
            else {
                if(img_bf!=null){
                    img_bf.setImageResource(R.drawable.home_stop);//音频停止
                }
                if(img_bf1!=null){
                    img_bf1.setImageResource(R.drawable.home_stop);//音频停止
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_previous:
                Mi=(Mi+4)%5;

                if (control != null) {
                    control.play(Mi);
                }
                break;
            case R.id.img_list:
                startActivity(new Intent(Music.this, Local.class));break;
            case R.id.img_next:
                Mi=(Mi+6)%5;
                if (control != null) {
                    control.play(Mi);
                }
                break;
            case R.id.img_fx:
                break;
            case R.id.img_sc:
                break;
            case R.id.img_reset:
                break;
            case R.id.img_bf:
                if (control.isPlay()) {
                    // 音频正在播放
                    isPaused = true;
                    control.pausePlay();
                    img_bf1.setImageResource(R.drawable.home_stop);
                    // 暂停动画
                    rotateAnimator.pause();
                } else if (isPaused) {
                    // 音频处于暂停状态
                    // 继续播放
                    isPaused = false;
                    control.continuePlay();
                    img_bf1.setImageResource(R.drawable.home_start);
                    if(rotateAnimator.isPaused()){
                        // 恢复动画
                        rotateAnimator.resume();
                    }else {
                        rotateAnimator.start();
                    }
                } else {
                    control.play(Mi);
                    img_bf1.setImageResource(R.drawable.home_start);
                    //开始旋转
                    rotateAnimator.start();
                }
                break;
            case R.id.img_back:
                finish();
                overridePendingTransition(R.anim.slide2_in_up, R.anim.slide2_out_down);
                break;
        }
    }
}