package com.qunar.im.ui.util.atmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;

import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.activity.AtListActivity;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.Map;

/**
 * @消息管理
 * Created by hubo.hu on 2017/10/26.
 */

public class AtManager {

    protected Context context;

    protected String jid;

    protected AtContactsModel AtContactsModel;

    protected int curPos;

    protected boolean ignoreTextChange = false;

    protected AtTextChangeListener listener;

    public interface AtTextChangeListener {
        void onTextAdd(String content, int stat, int length);
        void onTextDelete(int start, int length);
    }

    public AtManager(Context context, String jid) {
        this.context = context;
        this.jid = jid;
        AtContactsModel = new AtContactsModel();
    }

    public void setTextChangeListener(AtTextChangeListener listener) {
        this.listener = listener;
    }

    public void reset() {
        AtContactsModel.reset();
        ignoreTextChange = false;
        curPos = 0;
    }

    public Map<String, String> getAtBlocks(){
        return AtContactsModel.getAtBlocks();
    }
    /**
     * ------------------------------ 增加@成员 --------------------------------------
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PbChatActivity.AT_MEMBER && resultCode == Activity.RESULT_OK) {
            String name = data.getStringExtra("atName");
            String account = data.getStringExtra("atJid");

            insertAitMemberInner(account, name, curPos, false);
        }
    }

    public void insertAitMemberInner(String account, String name, int start, boolean needInsertAitInText) {
        name = name + " ";
        String content = needInsertAitInText ? "@" + name : name;
        if (listener != null) {
            // 关闭监听
            ignoreTextChange = true;
            // insert 文本到editText
            listener.onTextAdd(content, start, content.length());
            // 开启监听
            ignoreTextChange = false;
        }

        // update 已有的 AtBlock
        AtContactsModel.onInsertText(start, content);

        int index = needInsertAitInText ? start : start - 1;
        // 添加当前到 AtBlock
        AtContactsModel.addAtMember(account, name, index);
    }

    /**
     * ------------------------------ editText 监听 --------------------------------------
     */

    // 当删除尾部空格时，删除一整个segment,包含界面上也删除
    private boolean deleteSegment(int start, int count) {
        if (count != 1) {
            return false;
        }
        boolean result = false;
        AtBlock.AtSegment segment = AtContactsModel.findAtSegmentByEndPos(start);
        if (segment != null) {
            int length = start - segment.start;
            if (listener != null) {
                ignoreTextChange = true;
                listener.onTextDelete(segment.start, length);
                ignoreTextChange = false;
            }
            AtContactsModel.onDeleteText(start, length);
            result = true;
        }
        return result;
    }

    /**
     * @param editable 变化后的Editable
     * @param start    text 变化区块的起始index
     * @param count    text 变化区块的大小
     * @param delete   是否是删除
     */
    private void afterTextChanged(Editable editable, int start, int count, boolean delete) {
        curPos = delete ? start : count + start;
        LogUtil.i("atmanager", "afterTextChanged  editable = " + editable.toString() + "  start = " + start + "  count = " + count + "  delete = " + delete);
        if (ignoreTextChange) {
            return;
        }
        if (delete) {
            int before = curPos;//start + count;
            if (deleteSegment(before, count)) {
                return;
            }
            AtContactsModel.onDeleteText(before, count);

        } else {
            if (count <= 0 || editable.length() < start + count) {
                return;
            }
            CharSequence s = editable.subSequence(start, start + count);
            if (s == null) {
                return;
            }
            if (s.toString().equals("@")) {
               startAtList(false);
            }
            AtContactsModel.onInsertText(start, editable.toString());
        }
    }

    public void startAtList(boolean showAt){
        // 启动@联系人界面
        Intent intent = new Intent(context, AtListActivity.class);
        intent.putExtra("jid", jid);
        ((Activity)context).startActivityForResult(intent, PbChatActivity.AT_MEMBER);
    }

    private int editTextStart;
    private int editTextCount;
    private int editTextBefore;
    private boolean delete;//是否是删除操作

    /**
     * 编辑前
     * @param s
     * @param start
     * @param count
     * @param after
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        delete = count > after;
        LogUtil.i("atmanager", "beforeTextChanged  s = " + s.toString() + "  start = " + start + "  count = " + count + "  after = " + after);
    }

    /**
     * 编辑中
     * @param s
     * @param start
     * @param before
     * @param count
     */
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.editTextStart = start;
        this.editTextCount = count;
        this.editTextBefore = before;
        LogUtil.i("atmanager", "onTextChanged  s = " + s.toString() + "  start = " + start + "  count = " + count + "  before = " + before);
    }

    /**
     * 编辑后
     * @param s
     */
    public void afterTextChanged(Editable s) {
        LogUtil.i("atmanager", "afterTextChanged  s = " + s.toString());
        afterTextChanged(s, editTextStart, delete ? editTextBefore : editTextCount, delete);
    }
}
