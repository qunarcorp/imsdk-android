package com.qunar.im.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;


/**
 * Created by saber on 15-11-14.
 */
public class MySearchView extends LinearLayout {
    EditText search_src_text;
    IconView search_close_btn;
    private CharSequence mUserQuery;
    private String mOldQueryText;
    private OnQueryTextListener mOnQueryChangeListener;

    public void setOnQueryChangeListener(OnQueryTextListener l)
    {
        mOnQueryChangeListener = l;
    }

    public MySearchView(Context context) {
        this(context, null);
    }

    public MySearchView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.searchViewStyle);
    }

    public MySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSearch(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MySearchView(Context context,AttributeSet attrs,int defStyllAttr,int l)
    {
        super(context,attrs,defStyllAttr,l);
        initSearch(context);
    }

    void initSearch(Context context)
    {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_my_search_view,this,true);
        search_src_text = (EditText) findViewById(R.id.search_src_text);
        search_close_btn = (IconView) findViewById(R.id.search_close_btn);
        search_src_text.addTextChangedListener(mTextWatcher);
        search_src_text.setOnEditorActionListener(mOnEditorActionListener);
        search_close_btn.setOnClickListener(mOnClickListener);
        updateCloseButton(false);
    }

    public void changeQueryHint(String text)
    {
        search_src_text.setHint(text);
    }

    private final TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {

        /**
         * Called when the input method default action key is pressed.
         */
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            onSubmitQuery();
            return true;
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int before, int after) { }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int after) {
            MySearchView.this.onTextChanged(s);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void updateCloseButton(boolean hasText) {
        search_close_btn.setVisibility(hasText ? VISIBLE : GONE);
//        final Drawable closeButtonImg = search_close_btn.getDrawable();
//        if (closeButtonImg != null){
//            closeButtonImg.setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
//        }
    }

    private void onSubmitQuery() {
        CharSequence query = search_src_text.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryChangeListener !=null) {
                mOnQueryChangeListener.onQueryTextSubmit(query.toString());
            }
        }
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {

        public void onClick(View v) {
            if (v == search_close_btn) {
                onCloseClicked();
            }
        }
    };

    private void onCloseClicked() {
        CharSequence text = search_src_text.getText();
        if (!TextUtils.isEmpty(text)) {
            search_src_text.setText("");
            search_src_text.requestFocus();
            setImeVisibility(true);
        }

    }

    public void getEditFocus()
    {
        search_src_text.requestFocus();
    }

    private Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(search_src_text.getWindowToken(),0);
            }
        }
    };

    private void setImeVisibility(final boolean visible) {
        if (visible) {
            post(mShowImeRunnable);
        } else {
            removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    private void onTextChanged(CharSequence newText) {
        CharSequence text = search_src_text.getText();
        mUserQuery = text;
        boolean hasText = !TextUtils.isEmpty(text);
        updateCloseButton(hasText);
        if (mOnQueryChangeListener != null && !TextUtils.equals(newText, mOldQueryText)) {
            mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        mOldQueryText = newText.toString();
    }

    public CharSequence getQuery() {
        return search_src_text.getText();
    }

    public void setQuery(CharSequence sequence,boolean b) {
        search_src_text.setText(sequence);
        boolean isEmpty = TextUtils.isEmpty(sequence);
        if(!isEmpty)
        {
            mUserQuery = sequence;
        }
        if(b&&!isEmpty)
        {
            onSubmitQuery();
        }
    }

    /**
     * Callbacks for changes to the query text.
     */
    public interface OnQueryTextListener {

        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         *
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         *
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        boolean onQueryTextChange(String newText);
    }
}
