package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.jsonbean.HongbaoBroadcast;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.Constants;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by saber on 16-1-6.
 */
public class HongbaoPromptView extends LinearLayout {
    TextView hongbao_prompt;
    Context ctx;
    ImageView hongbao_icon;
    public HongbaoPromptView(Context context) {
        this(context, null);
    }

    public HongbaoPromptView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HongbaoPromptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ctx = context;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_hongbao_prompt, this, true);
        hongbao_prompt = findViewById(R.id.hongbao_prompt);
        hongbao_icon = findViewById(R.id.hongbao_icon);
    }

    public void bindData(final HongbaoBroadcast broadcast, final String fromJid,
                         boolean isFromGroup, final boolean isAA) {
        String username = CurrentPreference.getInstance().getUserid();
        final StringBuilder sb = new StringBuilder(broadcast.Url);
        if (isFromGroup) {
            sb.append("&username=").append(username).append("&sign=")
                    .append(BinaryUtil.MD5(username + "00d8c4642c688fd6bfa9a41b523bdb6b"))
                    .append("&company=qunar&")
                    .append("user_id=")
                    .append(fromJid)
                    .append("&ck=" + CurrentPreference.getInstance().getVerifyKey());
        } else {
            sb.append("&username=").append(username).append("&sign=")
                    .append(BinaryUtil.MD5(username + "00d8c4642c688fd6bfa9a41b523bdb6b"))
                    .append("&company=qunar&")
                    .append("group_id=")
                    .append(fromJid)
                    .append("&ck=" + CurrentPreference.getInstance().getVerifyKey());
        }
        if(broadcast.Open_User.equals(CurrentPreference.getInstance().getUserid()))
        {
            ConnectionUtil.getInstance().getUserCard(QtalkStringUtils.userId2Jid(broadcast.From_User), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    String name = nick.getName();
                    String content;
                    if (isAA) {
                        content = "你支付了"+name+"的"+ broadcast.Typestr + broadcast.Type;
                        hongbao_icon.setImageResource(R.drawable.atom_ui_ic_aa_pay);
                    } else {
                        content = "你领取了"+name+"的红包";
                        hongbao_icon.setImageResource(R.drawable.atom_ui_ic_lucky_money_red);
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    URLSpan span = new URLSpan(sb.toString()) {
                        @Override
                        public void onClick(View widget) {
                            String url = getURL();
                            Intent intent = new Intent(ctx, QunarWebActvity.class);
                            intent.setData(Uri.parse(url));
                            intent.putExtra(Constants.BundleKey.WEB_FROM,
                                    Constants.BundleValue.HONGBAO);
                            ctx.startActivity(intent);
                        }
                    };
                    SpannableString spannableString = new SpannableString(content);
                    spannableString.setSpan(span, content.length() - 2, content.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(spannableString);
                    hongbao_prompt.setMovementMethod(LinkMovementMethod.getInstance());
                    hongbao_prompt.setText(spannableStringBuilder);
                }
            }, false, true);
//            ProfileUtils.loadNickName(broadcast.From_User, false,
//                    new ProfileUtils.LoadNickNameCallback() {
//
//                        @Override
//                        public void finish(String name) {
//                            String content;
//                            if (isAA) {
//                                content = "你支付了"+name+"的"+ broadcast.Typestr + broadcast.Type;
//                                hongbao_icon.setImageResource(R.drawable.atom_ui_ic_aa_pay);
//                            } else {
//                                content = "你领取了"+name+"的红包";
//                                hongbao_icon.setImageResource(R.drawable.atom_ui_ic_lucky_money_red);
//                            }
//                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//                            URLSpan span = new URLSpan(sb.toString()) {
//                                @Override
//                                public void onClick(View widget) {
//                                    String url = getURL();
//                                    Intent intent = new Intent(ctx, QunarWebActvity.class);
//                                    intent.setData(Uri.parse(url));
//                                    intent.putExtra(Constants.BundleKey.WEB_FROM,
//                                            Constants.BundleValue.HONGBAO);
//                                    ctx.startActivity(intent);
//                                }
//                            };
//                            SpannableString spannableString = new SpannableString(content);
//                            spannableString.setSpan(span, content.length() - 2, content.length(),
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            spannableStringBuilder.append(spannableString);
//                            hongbao_prompt.setMovementMethod(LinkMovementMethod.getInstance());
//                            hongbao_prompt.setText(spannableStringBuilder);
//                        }
//                    });
        }
        else {
            if(broadcast.From_User.equals(CurrentPreference.getInstance().getUserid())){
                ConnectionUtil.getInstance().getUserCard(QtalkStringUtils.userId2Jid(broadcast.Open_User), new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(Nick nick) {
                        String name = nick.getName();
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        String content = name;
                        if (isAA) {
                            content += "支付了你的" + broadcast.Typestr + broadcast.Type;
                        } else {
                            content += "领取了你的红包";
                        }
                        URLSpan span = new URLSpan(sb.toString()) {
                            @Override
                            public void onClick(View widget) {
                                String url = getURL();
                                Intent intent = new Intent(ctx, QunarWebActvity.class);
                                intent.setData(Uri.parse(url));
                                intent.putExtra(Constants.BundleKey.WEB_FROM,
                                        Constants.BundleValue.HONGBAO);
                                ctx.startActivity(intent);
                            }
                        };
                        SpannableString spannableString = new SpannableString(content);
                        spannableString.setSpan(span, content.length() - 2, content.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableString);
                        if (isAA) {
                            if (broadcast.Balance > 0) {
                                spannableStringBuilder.append(",还剩");
                                spannableStringBuilder.append(String.valueOf(broadcast.Balance));
                                spannableStringBuilder.append("人未收齐");
                            } else {
                                spannableStringBuilder.append(",已收齐");
                            }
                            hongbao_icon.setImageResource(R.drawable.atom_ui_ic_aa_pay);
                        } else {
                            if (broadcast.Balance > 0) {
                                spannableStringBuilder.append(",还剩");
                                spannableStringBuilder.append(String.valueOf(broadcast.Balance));
                                spannableStringBuilder.append("个红包");
                            } else {
                                spannableStringBuilder.append(",你的红包已经被领完了");
                            }
                            hongbao_icon.setImageResource(R.drawable.atom_ui_ic_lucky_money_red);
                        }
                        hongbao_prompt.setMovementMethod(LinkMovementMethod.getInstance());
                        hongbao_prompt.setText(spannableStringBuilder);
                    }
                }, false, true);
            } else {
                ConnectionUtil.getInstance().getUserCard(QtalkStringUtils.userId2Jid(broadcast.Open_User), new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(Nick nick) {
                        String openname = nick.getName();
                        String fromName = ConnectionUtil.getInstance().getNickById(QtalkStringUtils.userId2Jid(broadcast.From_User)).getName();
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        String content = openname;
                        if (isAA) {
                            content += "支付了" + fromName + "的" + broadcast.Typestr + broadcast.Type;
                        } else {
                            content += "领取了" + fromName + "的红包";
                        }
                        URLSpan span = new URLSpan(sb.toString()) {
                            @Override
                            public void onClick(View widget) {
                                String url = getURL();
                                Intent intent = new Intent(ctx, QunarWebActvity.class);
                                intent.setData(Uri.parse(url));
                                intent.putExtra(Constants.BundleKey.WEB_FROM,
                                        Constants.BundleValue.HONGBAO);
                                ctx.startActivity(intent);
                            }
                        };
                        SpannableString spannableString = new SpannableString(content);
                        spannableString.setSpan(span, content.length() - 2, content.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableString);
                        if (isAA) {
                            if (broadcast.Balance > 0) {
                                spannableStringBuilder.append(",还剩");
                                spannableStringBuilder.append(String.valueOf(broadcast.Balance));
                                spannableStringBuilder.append("人未收齐");
                            } else {
                                spannableStringBuilder.append(",已收齐");
                            }
                            hongbao_icon.setImageResource(R.drawable.atom_ui_ic_aa_pay);
                        } else {
                            if (broadcast.Balance > 0) {
                                spannableStringBuilder.append(",还剩");
                                spannableStringBuilder.append(String.valueOf(broadcast.Balance));
                                spannableStringBuilder.append("个红包");
                            } else {
                                spannableStringBuilder.append(",红包已经被领完了");
                            }
                            hongbao_icon.setImageResource(R.drawable.atom_ui_ic_lucky_money_red);
                        }
                        hongbao_prompt.setMovementMethod(LinkMovementMethod.getInstance());
                        hongbao_prompt.setText(spannableStringBuilder);
                    }
                }, false, true);
            }
//            ProfileUtils.loadNickName(broadcast.Open_User, false,
//                    new ProfileUtils.LoadNickNameCallback() {
//                        @Override
//                        public void finish(String name) {
//                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//                            String content = name;
//                            if (isAA) {
//                                content += "支付了你的" + broadcast.Typestr + broadcast.Type;
//                            } else {
//                                content += "领取了你的红包";
//                            }
//                            URLSpan span = new URLSpan(sb.toString()) {
//                                @Override
//                                public void onClick(View widget) {
//                                    String url = getURL();
//                                    Intent intent = new Intent(ctx, QunarWebActvity.class);
//                                    intent.setData(Uri.parse(url));
//                                    intent.putExtra(Constants.BundleKey.WEB_FROM,
//                                            Constants.BundleValue.HONGBAO);
//                                    ctx.startActivity(intent);
//                                }
//                            };
//                            SpannableString spannableString = new SpannableString(content);
//                            spannableString.setSpan(span, content.length() - 2, content.length(),
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            spannableStringBuilder.append(spannableString);
//                            if (isAA) {
//                                if (broadcast.Balance > 0) {
//                                    spannableStringBuilder.append(",还剩");
//                                    spannableStringBuilder.append(String.valueOf(broadcast.Balance));
//                                    spannableStringBuilder.append("人未收齐");
//                                } else {
//                                    spannableStringBuilder.append(",已收齐");
//                                }
//                                hongbao_icon.setImageResource(R.drawable.atom_ui_ic_aa_pay);
//                            } else {
//                                if (broadcast.Balance > 0) {
//                                    spannableStringBuilder.append(",还剩");
//                                    spannableStringBuilder.append(String.valueOf(broadcast.Balance));
//                                    spannableStringBuilder.append("个红包");
//                                } else {
//                                    spannableStringBuilder.append(",你的红包已经被领完了");
//                                }
//                                hongbao_icon.setImageResource(R.drawable.atom_ui_ic_lucky_money_red);
//                            }
//                            hongbao_prompt.setMovementMethod(LinkMovementMethod.getInstance());
//                            hongbao_prompt.setText(spannableStringBuilder);
//                        }
//                    });
        }
    }
}
