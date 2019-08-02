package com.qunar.im.base.view.multilLevelTreeView;

/**
 * modify by xinbo.wang
 */
public class Node implements Comparable<Node> {
    private int id;
    private int pId = 0;
    private String name;
    private String key;
    private String headerSrc;
    private String xmppId;
    private int level;
    private boolean isExpand = false;
    private int icon;
    private boolean isRoot;

    public String getXmppId() {
        return xmppId;
    }

    public void setXmppId(String xmppId) {
        this.xmppId = xmppId;
    }

    public String getHeaderSrc() {
        return headerSrc;
    }

    public void setHeaderSrc(String headerSrc) {
        this.headerSrc = headerSrc;
    }

    public Node() {
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    @Override
    public int compareTo(Node another) {
        if (getId() == another.getId())
            return 0;
        if (getId() < another.getId())
            return -1;
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        return !(o == null || key == null) && o instanceof Node && key.equals(((Node) o).getKey());
    }
}
