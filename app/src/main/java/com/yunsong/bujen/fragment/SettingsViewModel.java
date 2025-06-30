package com.yunsong.bujen.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Integer> gddCont = new MutableLiveData<>(0);
    private final MutableLiveData<String> avatarBase64 = new MutableLiveData<>();
    public void setGddCont(int value) {
        gddCont.setValue(value);
    }

    public LiveData<Integer> getGddCont() {
        return gddCont;
    }

}
