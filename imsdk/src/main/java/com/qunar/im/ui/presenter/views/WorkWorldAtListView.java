package com.qunar.im.ui.presenter.views;

import java.util.List;

public interface WorkWorldAtListView {
    String getSearText();
    void showSearchUser(List<String> list);
    void showToast(String  string);
    String getJid();

}
