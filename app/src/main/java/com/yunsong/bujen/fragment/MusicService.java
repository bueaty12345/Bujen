package com.yunsong.bujen.fragment;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

//这是一个Service服务类
public class MusicService extends Service {
    //声明一个MediaPlayer引用
    private MediaPlayer player;
    //声明一个计时器引用
    private Timer timer;
    //记录音乐是否在播放
    private boolean isPlaying=false;
    //构造函数
    public MusicService() {}
    @Override
    public  IBinder onBind(Intent intent){
        return new MusicControl();
    }
    @Override
    public void onCreate(){
        super.onCreate();
        //创建音乐播放器对象
        player=new MediaPlayer();
    }
    //添加计时器用于设置音乐播放器中的播放进度条
    public void addTimer(){
        //如果timer不存在，也就是没有引用实例
        if(timer==null){
            //创建计时器对象
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    if (player==null) return;
                    int duration=player.getDuration();//获取歌曲总时长
                    int currentPosition=player.getCurrentPosition();//获取播放进度
                    Message msg= Music.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    bundle.putBoolean("play",isPlaying);
                    //再将bundle封装到msg消息对象中
                    msg.setData(bundle);
                    //最后将消息发送到主线程的消息队列
                    Music.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒（0.5s）执行一次
            timer.schedule(task,5,500);
        }
    }
    //Binder是一种跨进程的通信方式
    public class MusicControl extends Binder {
        private boolean isPrepared = false;
        public void playFromUrl(String url) {
            try {
                if (player == null) player = new MediaPlayer();
                player.reset();
                player.setDataSource(url);
                player.prepareAsync();
                player.setOnPreparedListener(mp -> {
                    mp.start();
                    isPrepared = true;
                    isPlaying = true;
                    updatePlaybackStatus();
                });
//                player.setLooping(true);

                player.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    updatePlaybackStatus();
                });
                player.start();
                isPlaying = true;
                addTimer();
                updatePlaybackStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean hasPrepared() {
            return isPrepared;
        }

        public int getDuration() {
            if (player != null && isPrepared) {
                return player.getDuration();
            }
            return 0;
        }

        public int getCurrentPosition() {
            if (player != null && isPrepared) {
                return player.getCurrentPosition();
            }
            return 0;
        }

        public void play(int i){//String path
            String[] m={"m1","m2","m3","m4","m5"};
            Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/"+m[i]);
            if(player==null) player=new MediaPlayer();
            try{
                //重置音乐播放器
                player.reset();
                //加载多媒体文件
                player=MediaPlayer.create(getApplicationContext(),uri);
                player.setLooping(true);
                player.start();//播放音乐
                isPlaying=true;
                addTimer();//添加计时器
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        //下面的暂停继续和退出方法全部调用的是MediaPlayer自带的方法
        public void pausePlay(){
            player.pause();//暂停播放音乐
            isPlaying=false;
        }
        public void continuePlay(){
            player.start();//继续播放音乐
            isPlaying=true;
        }
        public void seekTo(int progress){
            player.seekTo(progress);//设置音乐的播放位置
        }
        public void seekToBackward(){
            player.seekTo(player.getCurrentPosition() - 10000);//设置音乐的播放位置后退十秒
        }
        public void seekToForward(){
            player.seekTo(player.getCurrentPosition() + 10000);//设置音乐的播放位置前进十秒
        }
        public boolean isPlay(){
            return isPlaying;
        }
    }
    private void updatePlaybackStatus() {
        Intent intent = new Intent("ACTION_PLAYBACK_STATUS");
        intent.putExtra("isPlaying", isPlaying);
        sendBroadcast(intent);
    }

    //销毁多媒体播放器
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player==null) return;
        if(player.isPlaying()) player.stop();//停止播放音乐
        player.release();//释放占用的资源
        player=null;//将player置为空
    }
}
