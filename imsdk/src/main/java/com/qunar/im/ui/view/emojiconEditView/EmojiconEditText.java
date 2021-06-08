/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qunar.im.ui.view.emojiconEditView;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.emojiconTextView.EmojiconHandler;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconEditText extends AppCompatEditText {
    private int mEmojiconSize;
    private int mEmojiconTextSize;
    private boolean mUseSystemDefault = false;

    public EmojiconEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();
        mEmojiconTextSize = (int) getTextSize();
    }

    public EmojiconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
        mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
        mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false);
        a.recycle();
        mEmojiconTextSize = (int) getTextSize();

        //emotion普遍要下，大小暂时加3sp
        mEmojiconSize = mEmojiconSize +  + Utils.sp2px(getContext(), 3);
        mEmojiconTextSize = mEmojiconTextSize + Utils.sp2px(getContext(), 3);
        setText(getText());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        updateText();
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;

        updateText();
    }

    private void updateText() {
        EmojiconHandler.addEmojisForEdit(getContext(), getText(), mEmojiconSize, mEmojiconTextSize,0,-1, mUseSystemDefault);
    }

    /**
     * Set whether to use system default emojicon
     */
    public void setUseSystemDefault(boolean useSystemDefault) {
        mUseSystemDefault = useSystemDefault;
    }

   
}
