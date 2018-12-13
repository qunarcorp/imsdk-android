package com.qunar.im.ui.util.atmanager;

import com.qunar.im.base.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hzchenkang on 2017/7/7.
 *
 * @ 联系人数据
 */

public class AtContactsModel {

    // 已@ 的成员
    private Map<String, AtBlock> AtBlocks = new HashMap<>();

    // 清除所有的@块
    public void reset() {
        AtBlocks.clear();
    }

    public Map<String, String> getAtBlocks() {
        Map<String, String> map = new HashMap<>();
        Iterator<String> iterator = AtBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            AtBlock block = AtBlocks.get(account);
            if (block.valid()) {
                LogUtil.i("atmanager", "block :  account = " + account + "  text = " + block.text);
                map.put(account, block.text);
            }
        }
        return map;
    }

    public void addAtMember(String account, String name, int start) {
        AtBlock AtBlock = AtBlocks.get(account);
        if (AtBlock == null) {
            AtBlock = new AtBlock(name);
            AtBlocks.put(account, AtBlock);
        }
        AtBlock.addSegment(start);
    }

    // 查所有被@的群成员
    public List<String> getAtTeamMember() {
        List<String> teamMembers = new ArrayList<>();
        Iterator<String> iterator = AtBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            AtBlock block = AtBlocks.get(account);
            if (block.valid()) {
                teamMembers.add(account);
            }
        }
        
        return teamMembers;
    }

    public AtBlock getAtBlock(String account) {
        return AtBlocks.get(account);
    }

    // 找到 curPos 恰好命中 end 的segment
    public AtBlock.AtSegment findAtSegmentByEndPos(int start) {
        Iterator<String> iterator = AtBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            AtBlock block = AtBlocks.get(account);
            AtBlock.AtSegment segment = block.findLastSegmentByEnd(start);
            if (segment != null) {
                return segment;
            }
        }
        return null;
    }

    // 文本插入后更新@块的起止位置
    public void onInsertText(int start, String changeText) {
        Iterator<String> iterator = AtBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            AtBlock block = AtBlocks.get(account);
            block.moveRight(start, changeText);
            if (!block.valid()) {
                iterator.remove();
            }
        }
    }

    // 文本删除后更新@块的起止位置
    public void onDeleteText(int start, int length) {
        Iterator<String> iterator = AtBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            AtBlock block = AtBlocks.get(account);
            block.moveLeft(start, length);
            if (!block.valid()) {
                iterator.remove();
            }
        }
    }
}
