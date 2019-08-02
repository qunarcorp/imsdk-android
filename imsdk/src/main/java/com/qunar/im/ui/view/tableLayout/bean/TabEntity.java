package com.qunar.im.ui.view.tableLayout.bean;

import com.qunar.im.ui.view.tableLayout.listener.CustomTabEntity;

public class TabEntity implements CustomTabEntity {
    public String title;
    public int selectIconText;
    public int unSelectIconText;

    public TabEntity(String title, int selectIconText,int unSelectIconText) {
        this.title = title;
        this.selectIconText = selectIconText;
        this.unSelectIconText = unSelectIconText;
    }

    @Override
    public String getTabTitle() {
        return title;
    }



    @Override
    public int getSelectIconText() {
        return selectIconText;
    }

    @Override
    public int getUnSelectIconText() {
        return unSelectIconText;
    }


}