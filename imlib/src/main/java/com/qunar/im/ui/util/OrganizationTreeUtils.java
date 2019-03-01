package com.qunar.im.ui.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.jsonbean.GetDepartmentResult;
import com.qunar.im.base.presenter.views.IOrganizationView;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.treeView.holder.IconTreeItemHolder;
import com.qunar.im.ui.view.treeView.holder.SDHolder;
import com.qunar.im.ui.view.treeView.holder.ULHolder;
import com.qunar.im.ui.view.treeView.model.TreeNode;
import com.qunar.im.ui.view.treeView.view.AndroidTreeView;

import java.io.File;
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
        int size = FileUtils.getFileSize(fn, FileUtils.FileSizeUnit.B);
        if(size == 1){
            requestData();
        }else {
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        byte[] data = FileUtils.readFile(context.getFilesDir() + File.separator + fn);
                        if(data!=null){
                            handleData(new String(data));
                        }

                    }catch (Exception e){
                        requestData();
                    }
                }
            });
        }

    }

    private void requestData(){
        createProgressDialog();
        Protocol.getDepartment(new ProtocolCallback.UnitCallback<String>() {
            @Override
            public void onCompleted(String resultString) {
                FileUtils.writeToFile(resultString,fn,context,true);
                handleData(resultString);
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(String errMsg) {
                iOrganizationView.getView(null);
            }
        });
    }

    public void refresh(){
        init();
        requestData();
    }

    ProgressDialog dialog;
    private void createProgressDialog(){
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("正在更新数据。。。");
        dialog.show();
    }


    private void handleData(final String resultString){
        CommonConfig.mainhandler.post(new Runnable() {
            @Override
            public void run() {
                final List<GetDepartmentResult> results = JsonUtils.getGson().fromJson(resultString, new TypeToken<List<GetDepartmentResult>>() {}.getType());
                createView(results,root);
                if(iOrganizationView != null)
                    iOrganizationView.getView(tView.getView());
            }
        });
    }

    private void createView(List<GetDepartmentResult> results,TreeNode rootNode){
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
