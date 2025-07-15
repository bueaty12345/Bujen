package com.yunsong.bujen.fragment;

import com.yunsong.bujen.databean.MyMusicBean;

public class MusicController {
    private static MusicController instance;
    private MusicService.MusicControl musicControl;
    private MyMusicBean currentMusic;

    private MusicController() {}

    public static MusicController getInstance() {
        if (instance == null) {
            instance = new MusicController();
        }
        return instance;
    }

    public void setMusicControl(MusicService.MusicControl control) {
        this.musicControl = control;
    }

    public MusicService.MusicControl getMusicControl() {
        return musicControl;
    }

    public void setCurrentMusic(MyMusicBean music) {
        this.currentMusic = music;
    }

    public MyMusicBean getCurrentMusic() {
        return currentMusic;
    }
}

