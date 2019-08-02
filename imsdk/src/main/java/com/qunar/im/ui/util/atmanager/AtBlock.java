package com.qunar.im.ui.util.atmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hubo.hu on 2017/10/26.
 */

public class AtBlock {

    public String text;

    public List<AtSegment> segments = new ArrayList<>();

    public AtBlock(String name){
        this.text = name;
    }

    // 新增 segment
    public AtSegment addSegment(int start) {
        int end = start + text.length() - 1;
        AtSegment segment = new AtSegment(start, end);
        segments.add(segment);
        return segment;
    }

    /**
     * @param start      起始光标位置
     * @param changeText 插入文本
     */
    public void moveRight(int start, String changeText) {
        if (changeText == null) {
            return;
        }
        int length = changeText.length();
        Iterator<AtSegment> iterator = segments.iterator();
        while (iterator.hasNext()) {
            AtSegment segment = iterator.next();
            // 从已有的一个@块中插入
            if (start > segment.start && start <= segment.end) {
                segment.end += length;
                segment.broken = true;
            } else if (start <= segment.start) {
                segment.start += length;
                segment.end += length;
            }
        }
    }

    /**
     * @param start  删除前光标位置
     * @param length 删除块的长度
     */
    public void moveLeft(int start, int length) {
        int after = start - length;
        Iterator<AtSegment> iterator = segments.iterator();

        while (iterator.hasNext()) {
            AtSegment segment = iterator.next();
            // 从已有@块中删除
            if (start > segment.start) {
                // @被删除掉
                if (after <= segment.start) {
                    iterator.remove();
                } else if (after <= segment.end) {
                    segment.broken = true;
                    segment.end -= length;
                }
            } else if (start <= segment.start) {
                segment.start -= length;
                segment.end -= length;
            }
        }
    }

    // 获取该账号所有有效的@块最靠前的start
    public int getFirstSegmentStart() {
        int start = -1;
        for (AtSegment segment : segments) {
            if (segment.broken) {
                continue;
            }
            if (start == -1 || segment.start < start) {
                start = segment.start;
            }
        }
        return start;
    }

    public AtSegment findLastSegmentByEnd(int end) {
        int pos = end - 1;
        for (AtSegment segment : segments) {
            if (!segment.broken && segment.end == pos) {
                return segment;
            }
        }
        return null;
    }

    public boolean valid() {
        if (segments.size() == 0) {
            return false;
        }
        for (AtSegment segment : segments) {
            if (!segment.broken) {
                return true;
            }
        }
        return false;
    }

    public static class AtSegment {
        /**
         * 位于文本起始位置(include)
         */
        public int start;

        /**
         * 位于文本结束位置(include)
         */
        public int end;

        /**
         * 是否坏掉
         */
        public boolean broken = false;

        public AtSegment(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
