package com.qunar.rn_service.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class RNViewModel extends AndroidViewModel{
    private MutableLiveData<Integer> unreadCountLD = new MutableLiveData<>();

    public MutableLiveData<Integer> getUnreadCountLD() {
        return unreadCountLD;
    }

    public RNViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
