package com.qunar.im.ui.presenter.views;

/**
 * Created by saber on 15-12-4.
 */
public interface IBuddyView {

    /**
     * 显示对方设置的问题
     * @param question
     */
    void setQuestion(String question);
    /**
     * @param mode  要添加好友的隐私模式 //0 全部拒绝 1.人工认证 2.答案认证 3.全部接收
     * @return
     */
    String updateView(int mode);

    /**
     * @param isSuccess 添加好友是否成功
     * @param message  提示消息
     * @return
     */
    void setNofity(boolean isSuccess, String message);

    /**
     * @return  返回view上显示的用户需要的验证方式
     */
    int getAuthType();

    String getAnswerForQuestion();
    String getTargetId();
    String getRequestReason();
}
