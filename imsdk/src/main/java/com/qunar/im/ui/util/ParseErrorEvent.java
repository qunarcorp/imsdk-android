package com.qunar.im.ui.util;

import android.content.Context;

import com.qunar.im.protobuf.Event.ConnectionErrorEvent;
import com.qunar.im.ui.R;

/**
 * Created by hubin on 2017/12/27.
 */

public class ParseErrorEvent {
    public static String getError(int i,Context context){
        String prompt="";
        switch (i) {
            case ConnectionErrorEvent.bad_protocol:
                prompt = context.getString(R.string.atom_ui_tip_bad_protocol);
                break;
            case ConnectionErrorEvent.not_authorized:
                prompt = context.getString(R.string.atom_ui_tip_not_authorized);
                break;
            case ConnectionErrorEvent.out_of_date:
                prompt = context.getString(R.string.atom_ui_tip_out_of_date);
                break;
            default:
                prompt = context.getString(R.string.atom_ui_tip_connection_exception)+i;
                break;
        }

        return prompt;
    }
}
