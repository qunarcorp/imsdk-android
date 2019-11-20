package com.qunar.im.ui.view.tableLayout.listener;

import androidx.annotation.StringRes;

public interface CustomTabEntity {
    String getTabTitle();

    @StringRes
    int getSelectIconText();


    @StringRes
    int getUnSelectIconText();
}