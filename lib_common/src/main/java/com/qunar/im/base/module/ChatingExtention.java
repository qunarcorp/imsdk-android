package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by saber on 15-8-17.
 */
public class ChatingExtention extends BaseModel implements Serializable {
    public final static int DND = 1;
    public final static int AVAILABLE = 0;

    public static final int SHOW_NICK = 0;
    public static final int NOT_SHOW_NICK = 1;
    private String id;
    private String convBackground;

    public int getShowNick() {
        return showNick;
    }

    public void setShowNick(int showNick) {
        this.showNick = showNick;
    }

    public int showNick;
    private int dnd; // do not disturb

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConvBackground() {
        return convBackground;
    }

    public void setConvBackground(String convBackground) {
        this.convBackground = convBackground;
    }

    public int isDnd() {
        return dnd;
    }

    public void setDnd(int dnd) {
        this.dnd = dnd;
    }
}
