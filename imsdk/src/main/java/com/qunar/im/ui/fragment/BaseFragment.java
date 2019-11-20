package com.qunar.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.CommonDialog;

/**
 * Created by jiang.cheng on 2015/2/2.
 */
public class BaseFragment extends Fragment {
    protected CommonDialog.Builder commonDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        commonDialog =new CommonDialog.Builder(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public Handler getHandler() {
        return QunarIMApp.mainHandler;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.atom_ui_in_from_right, R.anim.atom_ui_out_to_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.atom_ui_in_from_right, R.anim.atom_ui_out_to_left);
    }

}
