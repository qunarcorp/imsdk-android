package com.qunar.im.ui.util;

import android.content.Context;
import android.widget.Toast;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.jsonbean.GetDepartmentResult;
import com.qunar.im.ui.presenter.views.IOrganizationView;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.presenter.views.IOrganizationView;
import com.qunar.im.ui.view.treeView.holder.IconTreeItemHolder;
import com.qunar.im.ui.view.treeView.holder.SDHolder;
import com.qunar.im.ui.view.treeView.holder.ULHolder;
import com.qunar.im.ui.view.treeView.model.TreeNode;
import com.qunar.im.ui.view.treeView.view.AndroidTreeView;
import com.qunar.im.utils.ConnectionUtil;

import java.util.List;

/**
 * 组织架构
 * Created by lihaibin.li on 2018/1/5.
 */

public class OrganizationTreeUtils {
    private static final String FILE_NAME = "organization.xml";
    private Context context;
    private TreeNode root;
    private AndroidTreeView tView;
    private IOrganizationView iOrganizationView;
    private String fn = QtalkNavicationService.getInstance().getXmppdomain() + "_" + FILE_NAME;
    public OrganizationTreeUtils(Context context){
        this.context = context;
        init();
    }

    public void getView(IOrganizationView iOrganizationView){
        this.iOrganizationView = iOrganizationView;
        getOrganizationData();
    }

    private void init(){
        root = TreeNode.root();
        tView = new AndroidTreeView(context, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom, true);
    }

    private void getOrganizationData(){
//        createProgressDialog();
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    handleData(GetDepartmentResult.Structured(ConnectionUtil.getInstance().getAllOrgaUsers()));
                }catch (Exception e){
//                    requestData();
                }
            }
        });

    }

//    private void requestData(){
//        createProgressDialog();
//        Protocol.getDepartment(new ProtocolCallback.UnitCallback<String>() {
//            @Override
//            public void onCompleted(String resultString) {
//                FileUtils.writeToFile(resultString,fn,context,true);
//                handleData(resultString);
//                if(dialog != null && dialog.isShowing()){
//                    dialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                iOrganizationView.getView(null);
//            }
//        });
//    }

    public void refresh(){
        init();
//        requestData();
    }

//    ProgressDialog dialog;
//    private void createProgressDialog(){
//        dialog = new ProgressDialog(context);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置水平进度条
//        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
//        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//        dialog.setTitle("正在更新数据。。。");
//        dialog.show();
//    }


    private void handleData(final List<GetDepartmentResult> results){
        CommonConfig.mainhandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    createView(results,root);
                    if(iOrganizationView != null)
                        iOrganizationView.getView(tView.getView());
                }catch (Exception e){
                    Toast.makeText(context,"json parse exception!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createView(List<GetDepartmentResult> results, TreeNode rootNode){
        if(results==null) return;
        for(GetDepartmentResult departmentResult : results){
            TreeNode treeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.atom_ui_new_arrow_right,departmentResult.D)).setViewHolder(new SDHolder(context));
            List<GetDepartmentResult.UserItem> userItems = departmentResult.UL;
            if(userItems != null && !userItems.isEmpty()){
                for(GetDepartmentResult.UserItem userItem : userItems){
                    TreeNode childNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.atom_ui_new_arrow_right,userItem)).setViewHolder(new ULHolder(context));
                    treeNode.addChild(childNode);
                }
            }
            List<GetDepartmentResult> resultList = departmentResult.SD;
            if(resultList != null && !resultList.isEmpty()){
                createView(resultList,treeNode);
            }
            rootNode.addChild(treeNode);
        }
    }
}
