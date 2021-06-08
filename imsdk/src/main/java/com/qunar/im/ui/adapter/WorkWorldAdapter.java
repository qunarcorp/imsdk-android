package com.qunar.im.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldOutCommentBean;
import com.qunar.im.base.module.WorkWorldOutOpenDetails;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.structs.WorkWorldItemState;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;

import static com.qunar.im.ui.adapter.PublicWorkWorldAdapterDraw.showWorkWorld;

public class WorkWorldAdapter extends BaseQuickAdapter<WorkWorldItem, BaseViewHolder> {

    private String plusImg = "https://qim.qunar.com/file/v2/download/temp/new/f798efc14a64e9abb7a336e8de283e5e.png?name=f798efc14a64e9abb7a336e8de283e5e.png&amp;file=file/f798efc14a64e9abb7a336e8de283e5e.png&amp;FileName=file/f798efc14a64e9abb7a336e8de283e5e.png";
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    private Activity mActivity;





    private View.OnClickListener onClickListener;

    private View.OnClickListener openDetailsListener;

    private RecyclerView mRecyclerView;

    private int iconSize;
    private int defaultSize;




    public WorkWorldAdapter(Activity activity) {
        super(R.layout.atom_ui_work_world_item);
        this.mActivity = activity;

//        g3 = new GridSpacingItemDecoration(3, Utils.dp2px(mActivity, 4), false);
//
//        g2 = new GridSpacingItemDecoration(2, Utils.dp2px(mActivity, 4), false);
//
//        g1 = new GridSpacingItemDecoration(1, Utils.dp2px(mActivity, 4), false);
    }

    public WorkWorldAdapter(Activity activity, RecyclerView recyclerView) {
        super(R.layout.atom_ui_work_world_item);
        this.mActivity = activity;



        mRecyclerView = recyclerView;
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOpenDetailsListener(View.OnClickListener listener) {
        this.openDetailsListener = listener;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(final BaseViewHolder helper, final WorkWorldItem item) {
        showWorkWorld(helper, item,mActivity,mRecyclerView,openDetailsListener,onClickListener, true);

    }










//    public static String ToDBC(String input) {
//        char[] c = input.toCharArray();
//        for (int i = 0; i < c.length; i++) {
//            if (c[i] == 12288) {
//                c[i] = (char) 32;
//                continue;
//            }
//            if (c[i] > 65280 && c[i] < 65375)
//                c[i] = (char) (c[i] - 65248);
//        }
//        return new String(c);
//    }
//
//    /**
//     * @param input String类型
//     * @return String  返回的String为全角（中文）类型
//     * @Description 解决textview的问题---半角字符与全角字符混乱所致；这种情况一般就是汉字与数字、英文字母混用
//     */
//    public static String toSBC(String input) { //半角转全角：
//        char[] c = input.toCharArray();
//        for (int i = 0; i < c.length; i++) {
//            if (c[i] == 32) {
//                c[i] = (char) 12288;
//                continue;
//            }
//            if (c[i] < 127) c[i] = (char) (c[i] + 65248);
//        }
//        return new String(c);
//    }
//
//
//    /**
//     * @param str String类型
//     * @return String
//     * @Description 替换、过滤特殊字符
//     */
//    public static String StringFilter(String str) throws PatternSyntaxException {
//        str = str.replaceAll(" ", "").replaceAll(" ", "").replaceAll("：", ":").replaceAll("：", "：").replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");//替换中文标号
//        String regEx = "[『』]"; // 清除掉特殊字符
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(str);
//        return m.replaceAll("").trim();
//    }


//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
}
