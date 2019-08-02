package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.module.AtData;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldDetailsCommentHotData;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldDetailsCommenData;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.presenter.WorkWorldDetailsPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldDetailsView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;

public class WorkWorldDetailsManagerPresenter implements WorkWorldDetailsPresenter {

    private WorkWorldDetailsView workWorldDetailsView;

    private int limit = 0;
    private int size = 20;

    private int listSize = 10;
    private int hotSize = 3;
    private List<String> hotId = new ArrayList<>();

    @Override
    public void sendComment() {
        if (!workWorldDetailsView.isOK()) {
            workWorldDetailsView.showToast("出现未知错误,不可评论", true);
            return;
        }

        if (!workWorldDetailsView.isCheck()) {
            workWorldDetailsView.showToast("请输入不超过200个字符的评论", true);
            return;
        }
        if(TextUtils.isEmpty(workWorldDetailsView.getContent())){
            workWorldDetailsView.showToast("请输入文字", true);
            return;
        }
        WorkWorldNewCommentBean data = new WorkWorldNewCommentBean();
        data.setCommentUUID("1-" + UUID.randomUUID().toString().replace("-", ""));
        data.setPostUUID(workWorldDetailsView.getPostUUid());
//        data.setCommentUUID(workWorldDetailsView.getContent());
        data.setPostOwner(workWorldDetailsView.getPostOwner());
        data.setPostOwnerHost(workWorldDetailsView.getPostOwnerHost());
        data.setContent(workWorldDetailsView.getContent());
        data.setIsAnonymous(workWorldDetailsView.isAnonymous() + "");
        data.setAtList(getAtList());
        if (workWorldDetailsView.isAnonymous() == ANONYMOUS_NAME) {
            data.setAnonymousName(workWorldDetailsView.getAnonymousName());
            data.setAnonymousPhoto(workWorldDetailsView.getAnonymousPhoto());
        }
        if (workWorldDetailsView.getToData() != null) {
            data.setToisAnonymous("0");
            data.setToUser(workWorldDetailsView.getToData().getFromUser());
            data.setToHost(workWorldDetailsView.getToData().getFromHost());
            data.setParentCommentUUID(workWorldDetailsView.getToData().getCommentUUID());
            if (TextUtils.isEmpty(workWorldDetailsView.getToData().getSuperParentUUID())) {
                data.setSuperParentUUID(workWorldDetailsView.getToData().getCommentUUID());
            } else {
                data.setSuperParentUUID(workWorldDetailsView.getToData().getSuperParentUUID());
            }

            if (workWorldDetailsView.getToData().getIsAnonymous().equals("1")) {
                data.setToAnonymousName(workWorldDetailsView.getToData().getAnonymousName());
                data.setToAnonymousPhoto(workWorldDetailsView.getToData().getAnonymousPhoto());
                data.setToisAnonymous("1");
            }
        }
        data.setHotCommentUUID(hotId);

        HttpUtil.releaseCommentV2(data, new ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData>() {
            @Override
            public void onCompleted(WorkWorldDetailsCommenData workWorldDetailsCommenData) {
                workWorldDetailsView.showToast("评论成功", true);
                if(workWorldDetailsCommenData.getData().getReturnType()==1){
                    workWorldDetailsView.updateNewCommentData(workWorldDetailsCommenData.getData().getNewComment(),true,false);
                }else{
                    workWorldDetailsView.showNewData(workWorldDetailsCommenData.getData().getNewComment(), true, false, false);
                }
                workWorldDetailsView.updateCommentNum(workWorldDetailsCommenData.getData().getPostCommentNum());
                workWorldDetailsView.updateLikeNum(workWorldDetailsCommenData.getData().getPostLikeNum());
                workWorldDetailsView.updateLikeState(workWorldDetailsCommenData.getData().getIsPostLike());
                workWorldDetailsView.updateOutCommentList(workWorldDetailsCommenData.getData().getAttachCommentList());
                workWorldDetailsView.deleteAtList();
                workWorldDetailsView.saveData();

            }

            @Override
            public void onFailure(String errMsg) {
                workWorldDetailsView.showToast("评论失败", true);
            }
        });

//        HttpUtil.releaseComment(data, new ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData>() {
//            @Override
//            public void onCompleted(WorkWorldDetailsCommenData workWorldDetailsCommenData) {
//                workWorldDetailsView.showToast("评论成功", true);
//                workWorldDetailsView.workworldshowNewData(workWorldDetailsCommenData.getData().getNewComment(), true, false);
//                workWorldDetailsView.updateCommentNum(workWorldDetailsCommenData.getData().getPostCommentNum());
//                workWorldDetailsView.updateLikeNum(workWorldDetailsCommenData.getData().getPostLikeNum());
//                workWorldDetailsView.updateLikeState(workWorldDetailsCommenData.getData().getIsPostLike());
//                workWorldDetailsView.saveData();
//
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                workWorldDetailsView.showToast("评论失败", true);
//            }
//        });
    }

    public String getAtList(){
        Map<String,String> map =  workWorldDetailsView.getAtList();
        List<AtData> dataList = new ArrayList<>();
        AtData ad = new AtData();
        ad.setType(10001);
        List<AtData.DataBean> atList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            AtData.DataBean atdb = new AtData.DataBean();
            atdb.setJid(entry.getKey());
            atdb.setText(entry.getValue().trim());
            atList.add(atdb);
        }
        ad.setData(atList);
        dataList.add(ad);

        String str = "";
        str = JsonUtils.getGson().toJson(dataList);
        return str;
    }

    @Override
    public void setView(WorkWorldDetailsView view) {
        workWorldDetailsView = view;
    }

    @Override
    public void loadingHistory() {
        List<WorkWorldNewCommentBean> list = ConnectionUtil.getInstance().selectHistoryWorkWorldNewCommentBean(size, 0, workWorldDetailsView.getPostUUid());
        workWorldDetailsView.showNewData(list, false, true, true);
        startRefresh();
    }

    @Override
    public void startRefresh() {
        workWorldDetailsView.startRefresh();


        HttpUtil.refreshWorkWorldNewCommentHotV2(hotSize, workWorldDetailsView.getPostUUid(), new ProtocolCallback.UnitCallback<WorkWorldDetailsCommentHotData>() {
            @Override
            public void onCompleted(WorkWorldDetailsCommentHotData wokrWorldDetailsCommentHotData) {
                workWorldDetailsView.showHotNewData(wokrWorldDetailsCommentHotData.getData().getNewComment());
                hotId = new ArrayList<>();
                for (int i = 0; i < wokrWorldDetailsCommentHotData.getData().getNewComment().size(); i++) {
                    hotId.add(wokrWorldDetailsCommentHotData.getData().getNewComment().get(i).getCommentUUID());
                }
                HttpUtil.refreshWorkWorldNewCommentV2(hotId,listSize, workWorldDetailsView.getPostUUid(), new ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData>() {
                    @Override
                    public void onCompleted(WorkWorldDetailsCommenData workWorldResponse) {


                        workWorldDetailsView.showNewData(workWorldResponse.getData().getNewComment(), false, false, false);
                        workWorldDetailsView.updateCommentNum(workWorldResponse.getData().getPostCommentNum());
                        workWorldDetailsView.updateLikeNum(workWorldResponse.getData().getPostLikeNum());
                        workWorldDetailsView.updateLikeState(workWorldResponse.getData().getIsPostLike());
                        workWorldDetailsView.updateOutCommentList(workWorldResponse.getData().getAttachCommentList());
                        workWorldDetailsView.saveData();

                    }

                    @Override
                    public void onFailure(String errMsg) {

                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });









//        if(true){
//            return;
//        }
//
//        HttpUtil.refreshWorkWorldNewComment(listSize, workWorldDetailsView.getPostUUid(), new ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData>() {
//            @Override
//            public void onCompleted(WorkWorldDetailsCommenData workWorldResponse) {
//
//
//                workWorldDetailsView.workworldshowNewData(workWorldResponse.getData().getNewComment(), false, false);
//                workWorldDetailsView.updateCommentNum(workWorldResponse.getData().getPostCommentNum());
//                workWorldDetailsView.updateLikeNum(workWorldResponse.getData().getPostLikeNum());
//                workWorldDetailsView.updateLikeState(workWorldResponse.getData().getIsPostLike());
//                workWorldDetailsView.saveData();
//
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });
//
//        HttpUtil.refreshWorkWorldNewCommentHot(hotSize, workWorldDetailsView.getPostUUid(), new ProtocolCallback.UnitCallback<WorkWorldDetailsCommentHotData>() {
//            @Override
//            public void onCompleted(WorkWorldDetailsCommentHotData wokrWorldDetailsCommentHotData) {
//                workWorldDetailsView.showHotNewData(wokrWorldDetailsCommentHotData.getData());
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });


    }

    @Override
    public void loadingMore(final boolean localMore) {
        WorkWorldNewCommentBean item = workWorldDetailsView.getLastItem();
        if (item == null) {
            workWorldDetailsView.showMoreData(new ArrayList<MultiItemEntity>(), localMore);
            return;
        }
        HttpUtil.loadMoreWorkWorldCommentV2(hotId,listSize, Integer.parseInt(item.getId()), workWorldDetailsView.getPostUUid(), item.getCreateTime(), new ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData>() {
            @Override
            public void onCompleted(WorkWorldDetailsCommenData workWorldResponse) {
                if (workWorldResponse != null) {
                    workWorldDetailsView.showMoreData(workWorldResponse.getData().getNewComment(), localMore);
                } else {
                    workWorldDetailsView.showMoreData(new ArrayList<MultiItemEntity>(), localMore);
                }

            }

            @Override
            public void onFailure(String errMsg) {
                List<WorkWorldNewCommentBean> list = ConnectionUtil.getInstance().selectHistoryWorkWorldCommentItem(size, workWorldDetailsView.getListCount());
                workWorldDetailsView.showMoreData(list, localMore);
            }
        });


//        HttpUtil.loadMoreWorkWorldComment(listSize, Integer.parseInt(item.getId()), workWorldDetailsView.getPostUUid(), item.getCreateTime(), new ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData>() {
//            @Override
//            public void onCompleted(WorkWorldDetailsCommenData workWorldResponse) {
//                if (workWorldResponse != null) {
//                    workWorldDetailsView.workworldshowMoreData(workWorldResponse.getData().getNewComment());
//                } else {
//                    workWorldDetailsView.workworldshowMoreData(new ArrayList<MultiItemEntity>());
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                List<WorkWorldNewCommentBean> list = ConnectionUtil.getInstance().selectHistoryWorkWorldCommentItem(size, workWorldDetailsView.workworldgetListCount());
//                workWorldDetailsView.workworldshowMoreData(list);
//            }
//        });
    }

    @Override
    public void deleteWorkWorldCommentItem(WorkWorldNewCommentBean item) {

//        HttpUtil.deleteWorkWorldCommentItem(item.getCommentUUID(), item.getPostUUID(), new ProtocolCallback.UnitCallback<WorkWorldDeleteResponse>() {
//            @Override
//            public void onCompleted(WorkWorldDeleteResponse workworlddeleteWorkWorldItem) {
//                workWorldDetailsView.removeWorkWorldCommentItem(workworlddeleteWorkWorldItem);
//                workWorldDetailsView.updateCommentNum(workworlddeleteWorkWorldItem.getData().getPostCommentNum());
//                workWorldDetailsView.updateLikeNum(workworlddeleteWorkWorldItem.getData().getPostLikeNum());
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });


        HttpUtil.deleteWorkWorldCommentItemV2(item.getSuperParentUUID(),item.getCommentUUID(), item.getPostUUID(), new ProtocolCallback.UnitCallback<WorkWorldDeleteResponse>() {
            @Override
            public void onCompleted(WorkWorldDeleteResponse deleteWorkWorldItem) {
                workWorldDetailsView.updateOutCommentList(deleteWorkWorldItem.getData().getAttachCommentList());
                workWorldDetailsView.removeWorkWorldCommentItem(deleteWorkWorldItem);
                workWorldDetailsView.updateCommentNum(deleteWorkWorldItem.getData().getPostCommentNum());
                workWorldDetailsView.updateLikeNum(deleteWorkWorldItem.getData().getPostLikeNum());
                workWorldDetailsView.saveData();

            }

            @Override
            public void onFailure(String errMsg) {

            }
        });



    }

}
