package com.qunar.im.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.qunar.im.ui.R;


public class CommonDialog extends Dialog {


    public CommonDialog(Context context) {
        super(context);
    }

    public CommonDialog(Context context, int theme) {
        super(context, theme);

    }

    public static class Builder {
        private Context context; // 上下文对象
        private String title; // 对话框标题
        private String message; // 对话框内容
        private Spanned htmlMessage;//网页形式
        private String confirm_btnText; // 按钮名称“确定”
        private String cancel_btnText; // 按钮名称“取消”
        private String neutral_btnText; // 按钮名称“隐藏”
        private View contentView; // 对话框中间加载的其他布局界面
        /* 按钮坚挺事件 */
        private OnClickListener confirm_btnClickListener;
        private OnClickListener cancel_btnClickListener;
        private OnClickListener neutral_btnClickListener;

        private CommonDialog dialog;
        private String[] items;
        private OnItemClickListener itemClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /* 设置对话框信息 */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessageHtml(Spanned message){
            this.htmlMessage = message;
            return this;
        }


        /**
         * Set the Dialog message from resource
         *
         * @param
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置对话框界面
         *
         * @param v View
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(int confirm_btnText,
                                         OnClickListener listener) {
            this.confirm_btnText = (String) context.getText(confirm_btnText);
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the positive button and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(String confirm_btnText,
                                         OnClickListener listener) {
            this.confirm_btnText = confirm_btnText;
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param
         * @return
         */
        public Builder setNegativeButton(int cancel_btnText,
                                         OnClickListener listener) {
            this.cancel_btnText = (String) context.getText(cancel_btnText);
            this.cancel_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button and it's listener
         *
         * @param
         * @return
         */
        public Builder setNegativeButton(String cancel_btnText,
                                         OnClickListener listener) {
            this.cancel_btnText = cancel_btnText;
            this.cancel_btnClickListener = listener;
            return this;
        }

        /**
         * Set the netural button resource and it's listener
         *
         * @param
         * @return
         */
        public Builder setNeutralButton(int neutral_btnText,
                                        OnClickListener listener) {
            this.neutral_btnText = (String) context.getText(neutral_btnText);
            this.neutral_btnClickListener = listener;
            return this;
        }


        /**
         * Set the netural button and it's listener
         *
         * @param
         * @return
         */
        public Builder setNeutralButton(String neutral_btnText,
                                        OnClickListener listener) {
            this.neutral_btnText = neutral_btnText;
            this.neutral_btnClickListener = listener;
            return this;
        }

        public boolean isShowing() {
            if (dialog != null) {
                return dialog.isShowing();
            } else {
                return false;
            }
        }

        public void dismiss() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        public CommonDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
//			final CommonDialog dialog = new CommonDialog(context,
//					R.style.myiosstyle);
            dialog = new CommonDialog(context,
                    R.style.myiosstyle);
            if(items != null){
                View listLayout = inflater.inflate(R.layout.atom_ui_customdialog_list, null);
                dialog.setContentView(listLayout);
                ListView listview = (ListView) listLayout.findViewById(R.id.atom_ui_customdialog_list);
                ArrayAdapter arr_adapter = new ArrayAdapter<String>(context, R.layout.atom_ui_customdialog_list_item, R.id.customdialog_list_item, items);
                listview.setAdapter(arr_adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(itemClickListener != null){
                            itemClickListener.OnItemClickListener(dialog, position);
                            dialog.dismiss();
                        }
                    }
                });
                return dialog;
            }
            View layout = inflater.inflate(R.layout.atom_ui_customdialog, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            ((TextView) layout.findViewById(R.id.title)).getPaint()
                    .setFakeBoldText(true);

            if (title == null || title.trim().length() == 0) {
                ((TextView) layout.findViewById(R.id.message))
                        .setGravity(Gravity.CENTER);
            }

            if (neutral_btnText != null && confirm_btnText != null
                    && cancel_btnText != null) {
                ((Button) layout.findViewById(R.id.neutral_btn))
                        .setText(neutral_btnText);
                if (neutral_btnClickListener != null) {
                    ((Button) layout.findViewById(R.id.neutral_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    neutral_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEUTRAL);
                                }
                            });
                } else {
                    ((Button) layout.findViewById(R.id.neutral_btn))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button or cancle button or neutral just set the
                // visibility to GONE
                layout.findViewById(R.id.neutral_btn).setVisibility(View.GONE);
                layout.findViewById(R.id.single_line).setVisibility(View.GONE);
            }
            // set the confirm button
            if (confirm_btnText != null) {
                ((Button) layout.findViewById(R.id.confirm_btn))
                        .setText(confirm_btnText);
                if (confirm_btnClickListener != null) {
                    ((Button) layout.findViewById(R.id.confirm_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    confirm_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                } else {
                    ((Button) layout.findViewById(R.id.confirm_btn))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.confirm_btn).setVisibility(View.GONE);
                layout.findViewById(R.id.second_line).setVisibility(View.GONE);
                layout.findViewById(R.id.cancel_btn).setBackgroundResource(
                        R.drawable.atom_ui_single_btn_select);
            }
            // set the cancel button
            if (cancel_btnText != null) {
                ((Button) layout.findViewById(R.id.cancel_btn))
                        .setText(cancel_btnText);
                if (cancel_btnClickListener != null) {
                    ((Button) layout.findViewById(R.id.cancel_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    cancel_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                } else {
                    ((Button) layout.findViewById(R.id.cancel_btn))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no cancel button just set the visibility to GONE
                layout.findViewById(R.id.cancel_btn).setVisibility(View.GONE);
                layout.findViewById(R.id.second_line).setVisibility(View.GONE);
                layout.findViewById(R.id.confirm_btn).setBackgroundResource(
                        R.drawable.atom_ui_single_btn_select);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            }else if(htmlMessage!=null){
                ((TextView) layout.findViewById(R.id.message)).setText(htmlMessage);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
//                 ((LinearLayout) layout.findViewById(R.id.message))
//                 .removeAllViews();
//                 ((LinearLayout) layout.findViewById(R.id.message)).addView(
//                 contentView, new LayoutParams(
//                 LayoutParams.WRAP_CONTENT,
//                 LayoutParams.WRAP_CONTENT));
//                 ((LinearLayout) layout.findViewById(R.id.layout))
//                 .removeAllViews();
//                 ((LinearLayout) layout.findViewById(R.id.layout)).addView(
//                 contentView, new LayoutParams(
//                 LayoutParams.WRAP_CONTENT,
//                 LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

        public Builder setCancelable(boolean b) {
            if (dialog != null) {
                dialog.setCancelable(b);
            }
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean b) {
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(b);
            }
            return this;
        }


        public void show() {
//			if(dialog==null){
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
            create().show();
//			}
        }

        public Builder setItems(String[] items) {
            this.items = items;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
            return this;
        }



        public interface OnItemClickListener{
            void OnItemClickListener(Dialog dialog, int postion);
        }
    }
}
