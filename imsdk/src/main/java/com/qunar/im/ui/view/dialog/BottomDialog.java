/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Raphaël Bussa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.qunar.im.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raphaelbussa on 19/01/16.
 */
public class BottomDialog {

    private CustomDialog customDialog;
    private boolean TOP;
    private Context context;

    public BottomDialog(Context context, boolean gravityTop) {
        TOP = gravityTop;
        this.context = context;
        customDialog = new CustomDialog(context);
    }

    public void title(String title) {
        customDialog.title(title);
    }

    public void title(int title) {
        customDialog.title(title);
    }

    public void inflateMenu(int menu) {
        customDialog.inflateMenu(menu);
    }

    public void addItems(List<Item> items) {
        customDialog.addItems(items);
    }

    /*public void addItem(Item item) {
        customDialog.addItem(item);
    }*/

    public void cancelable(boolean value) {
        customDialog.setCancelable(value);
    }

    public void canceledOnTouchOutside(boolean value) {
        customDialog.setCanceledOnTouchOutside(value);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        customDialog.setOnItemSelectedListener(onItemSelectedListener);
    }

    public void show() {
        customDialog.show();
    }

    public void dismiss() {
        customDialog.dismiss();
    }

    public interface OnItemSelectedListener {
        boolean onItemSelected(int id);
    }

    private class CustomDialog extends Dialog implements View.OnClickListener {

        private final String TAG = CustomDialog.class.getName();

        private int padding;
        private int itemWidth;
        private Context context;
        private int icon;
        private LinearLayout container;
        private OnItemSelectedListener onItemSelectedListener;
        private List<Item> items;
        LayoutInflater inflater = null;

        public CustomDialog(Context context) {
            super(context);
            this.context = context;
            inflater = LayoutInflater.from(context);
            items = new ArrayList<>();
            icon = getContext().getResources().getDimensionPixelSize(R.dimen.atom_ui_share_icon);
            padding = getContext().getResources().getDimensionPixelSize(R.dimen.atom_ui_item_padding);
            itemWidth = (com.qunar.im.base.util.Utils.getScreenWidth(getContext()) - 2 * padding) / 4;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container = new LinearLayout(getContext());
            container.setLayoutParams(params);
            container.setBackgroundColor(Color.WHITE);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(padding, padding, padding, padding*2);
            //ScrollView scrollView = new ScrollView(getContext());
            //scrollView.addView(container);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(container, params);
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            getWindow().setGravity(TOP ? Gravity.TOP : Gravity.BOTTOM);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getWindow().getAttributes().windowAnimations = R.style.atom_ui_DialogAnimation;
        }

        public void cancelable(boolean value) {
            setCancelable(value);
        }

        public void canceledOnTouchOutside(boolean value) {
            setCanceledOnTouchOutside(value);
        }

        public void addItems(List<Item> itemList) {
            items.clear();
            items.addAll(itemList);
            int i = 0;
            LinearLayout row = null;
            int margin = com.qunar.im.base.util.Utils.dipToPixels(getContext(), 8);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, margin, 0, 0);
            for (Item item : items) {
                if (i % 4 == 0) {
                    row = new LinearLayout(getContext());
                    row.setLayoutParams(lp);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    container.addView(row);
                }
                addItem(item, row);
                i++;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void addItem(Item item, LinearLayout row) {
            //int size = icon + padding + padding;
            View view =  inflater.inflate(R.layout.atom_ui_dialog_item_share_item,null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
//            view.setBackgroundColor(context.getColor(R.color.atom_rtc_red_67));
            ((TextView)view.findViewById(R.id.share_text)).setText(item.getTitle());
            ((ImageView)view.findViewById(R.id.share_icon)).setBackground(item.getIcon());
            view.setOnClickListener(this);
            view.setId(item.getId());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//            TextView cell = new TextView(getContext());
//            cell.setId(item.getId());
//            cell.setLayoutParams(params);
//            cell.setMaxLines(1);
//            cell.setEllipsize(TextUtils.TruncateAt.END);
//            cell.setGravity(Gravity.CENTER_HORIZONTAL);
//            cell.setText(item.getTitle());
//            cell.setTypeface(Typeface.DEFAULT_BOLD);
//            cell.setOnClickListener(this);
//            cell.setTextColor(Utils.colorStateListText(getContext()));
//            TypedValue typedValue = new TypedValue();
//            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
//            cell.setBackgroundResource(typedValue.resourceId);
//            if (item.getIcon() != null) {
//                cell.setCompoundDrawablesWithIntrinsicBounds(null, icon(item.getIcon()), null, null);
//                cell.setCompoundDrawablePadding(padding);
//                cell.setPadding(0, padding, 0, padding);
//            }
            row.addView(view);
        }

        public void inflateMenu(int menu) {
            MenuInflater menuInflater = new SupportMenuInflater(getContext());
            MenuBuilder menuBuilder = new MenuBuilder(getContext());
            menuInflater.inflate(menu, menuBuilder);
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < menuBuilder.size(); i++) {
                MenuItem menuItem = menuBuilder.getItem(i);
                Item item = new Item();
                item.setId(menuItem.getItemId());
                item.setIcon(menuItem.getIcon());
                item.setTitle(menuItem.getTitle().toString());
                items.add(item);
            }
            addItems(items);
        }

        /**
         * @param drawable Drawable from menu item
         * @return Drawable resized 32dp x 32dp and colored with color textColorSecondary
         */
        private Drawable icon(Drawable drawable) {
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Drawable resizeIcon = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, icon, icon, true));
//                Drawable.ConstantState state = resizeIcon.getConstantState();
//                resizeIcon = DrawableCompat.wrap(state == null ? resizeIcon : state.newDrawable()).mutate();
                //DrawableCompat.setTintList(resizeIcon, Utils.colorStateListIcon(getContext()));
                return resizeIcon;
            }
            return null;
        }

        public void title(int title) {
            title(getContext().getString(title));
        }

        public void title(String title) {
            int size = getContext().getResources().getDimensionPixelSize(R.dimen.atom_ui_container_margin) + padding;
            TextView item = new TextView(getContext());
            item.setText(title);
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
            item.setPadding(size, padding, padding, padding);
            container.addView(item);
        }

        @Override
        public void onClick(View v) {
            if (onItemSelectedListener != null) {
                if (onItemSelectedListener.onItemSelected(v.getId())) {
                    dismiss();
                }
            }
        }

        public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
        }

    }

}
