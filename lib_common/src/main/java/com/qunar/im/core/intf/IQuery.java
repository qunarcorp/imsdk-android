package com.qunar.im.core.intf;

import android.database.Cursor;

/**
 * Created by froyomu on 2019/1/15
 *
 * Describe:
 */
public interface IQuery {
    Object onQuery(Cursor cursor);
}
