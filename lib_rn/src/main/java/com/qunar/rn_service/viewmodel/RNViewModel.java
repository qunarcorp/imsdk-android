package com.qunar.rn_service.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

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
