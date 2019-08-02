package com.qunar.im.ui.presenter.views;

/**
 * Created by saber on 15-12-11.
 */
public abstract class DeleteBuddyView implements IBuddyView {
    @Override
    public void setQuestion(String question) {

    }

    @Override
    public String updateView(int mode) {
        return null;
    }

    @Override
    public void setNofity(boolean isSuccess, String message) {
    }

    @Override
    public String getAnswerForQuestion() {
        return null;
    }

    @Override
    public String getRequestReason() {
        return null;
    }

    @Override
    public int getAuthType() {
        return 0;
    }
}
