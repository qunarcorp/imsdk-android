package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindSubView;
import com.qunar.im.base.protocol.ProgressRequestListener;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.ui.imagepicker.ui.ImageGridActivity;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.RichEditor;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lihaibin.li on 2017/11/15.
 */

public class DailyNoteEditorActivity extends SwipeBackActivity implements View.OnClickListener, PermissionCallback,IDailyMindSubView {
    public static final int REQUEST_CODE = 2017;
    //权限
    protected final int SHOW_CAMERA = PermissionDispatcher.getRequestCode();
    protected final int SELECT_PIC = PermissionDispatcher.getRequestCode();

    public static final int ACTIVITY_GET_CAMERA_IMAGE = 1;//拍照
    public static final int ACTIVITY_SELECT_PHOTO = 2;//图库选图

    private EditText insert_title;

    private QtNewActionBar mNewActionBar;

    private CheckBox insert_font, insert_bold, insert_xieti, insert_shachuxian, insert_yinyong, insert_h1, insert_h2, insert_h3, insert_h4;
    private HorizontalScrollView operate_layout1;

    private ImageView insert_img, insert_line, insert_link, insert_undo, insert_redo;

    private RichEditor editor;

    private String qid;
    private DailyMindSub dailyMindSub;
    private IDailyMindPresenter evernotePresenter;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_note_editor);

        evernotePresenter = new DailyMindPresenter();
        evernotePresenter.setView(this);

        dailyMindSub = (DailyMindSub) getIntent().getSerializableExtra("data");
        qid = getIntent().getStringExtra("qid");
        isUpdate = dailyMindSub != null;

        initViews();
    }

    private void initViews() {
        insert_title = (EditText) findViewById(R.id.insert_title);
        mNewActionBar = (QtNewActionBar) findViewById(R.id.my_action_bar);
        setNewActionBar(mNewActionBar);
        setActionBarTitle(R.string.atom_ui_title_ever_note);
        setActionBarRightText(R.string.atom_ui_common_save);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNote();
            }
        });

        editor = (RichEditor) findViewById(R.id.insert_content);
        insert_font = (CheckBox) findViewById(R.id.insert_font);
        operate_layout1 = (HorizontalScrollView) findViewById(R.id.operate_layout1);
        insert_font.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                operate_layout1.setVisibility(b ? View.VISIBLE : View.GONE);
                if (b)
                    startAnimation(operate_layout1);
            }
        });
        insert_bold = (CheckBox) findViewById(R.id.insert_bold);
        insert_bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setBold();
            }
        });
        insert_xieti = (CheckBox) findViewById(R.id.insert_xieti);
        insert_xieti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setItalic();
            }
        });
        insert_shachuxian = (CheckBox) findViewById(R.id.insert_shachuxian);
        insert_shachuxian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setStrikeThrough();
            }
        });
        insert_yinyong = (CheckBox) findViewById(R.id.insert_yinyong);
        insert_yinyong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setBlockquote(b);
            }
        });
        insert_h1 = (CheckBox) findViewById(R.id.insert_h1);
        insert_h1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setHeading(1, b);
                insert_h1.setBackgroundResource(b ? R.drawable.atom_ui_note_insert_h1_s : R.drawable.atom_ui_note_insert_h1_n);
                if (b) {
                    insert_h2.setBackgroundResource(R.drawable.atom_ui_note_insert_h2_n);
                    insert_h3.setBackgroundResource(R.drawable.atom_ui_note_insert_h3_n);
                    insert_h4.setBackgroundResource(R.drawable.atom_ui_note_insert_h4_n);
                }
            }
        });
        insert_h2 = (CheckBox) findViewById(R.id.insert_h2);
        insert_h2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setHeading(2, b);
                insert_h2.setBackgroundResource(b ? R.drawable.atom_ui_note_insert_h2_s : R.drawable.atom_ui_note_insert_h2_n);
                if (b) {
                    insert_h1.setBackgroundResource(R.drawable.atom_ui_note_insert_h1_n);
                    insert_h3.setBackgroundResource(R.drawable.atom_ui_note_insert_h3_n);
                    insert_h4.setBackgroundResource(R.drawable.atom_ui_note_insert_h4_n);
                }
            }
        });
        insert_h3 = (CheckBox) findViewById(R.id.insert_h3);
        insert_h3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setHeading(3, b);
                insert_h3.setBackgroundResource(b ? R.drawable.atom_ui_note_insert_h3_s : R.drawable.atom_ui_note_insert_h3_n);
                if (b) {
                    insert_h1.setBackgroundResource(R.drawable.atom_ui_note_insert_h1_n);
                    insert_h2.setBackgroundResource(R.drawable.atom_ui_note_insert_h2_n);
                    insert_h4.setBackgroundResource(R.drawable.atom_ui_note_insert_h4_n);
                }
            }
        });
        insert_h4 = (CheckBox) findViewById(R.id.insert_h4);
        insert_h4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.setHeading(4, b);
                insert_h4.setBackgroundResource(b ? R.drawable.atom_ui_note_insert_h4_s : R.drawable.atom_ui_note_insert_h4_n);
                if (b) {
                    insert_h1.setBackgroundResource(R.drawable.atom_ui_note_insert_h1_n);
                    insert_h3.setBackgroundResource(R.drawable.atom_ui_note_insert_h3_n);
                    insert_h2.setBackgroundResource(R.drawable.atom_ui_note_insert_h2_n);
                }
            }
        });
        insert_img = (ImageView) findViewById(R.id.insert_img);
        insert_img.setOnClickListener(this);
        insert_line = (ImageView) findViewById(R.id.insert_line);
        insert_line.setOnClickListener(this);
        insert_link = (ImageView) findViewById(R.id.insert_link);
        insert_link.setOnClickListener(this);
        insert_undo = (ImageView) findViewById(R.id.insert_undo);
        insert_undo.setOnClickListener(this);
        insert_redo = (ImageView) findViewById(R.id.insert_redo);
        insert_redo.setOnClickListener(this);

        editor.setEditorHeight(200);
        editor.setEditorFontSize(15);
        editor.setPadding(10, 10, 10, 50);
        editor.setPlaceholder((String) getText(R.string.atom_ui_tip_note_input_content));

        if (dailyMindSub != null) {
            insert_title.setText(dailyMindSub.title);
            editor.setHtml(dailyMindSub.content);
        }
    }

    private AlertDialog linkDialog;

    /**
     * 插入链接Dialog
     */
    private void showInsertLinkDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        linkDialog = adb.create();

        View view = getLayoutInflater().inflate(R.layout.atom_ui_dialog_note_insertlink, null);

        final EditText et_link_address = (EditText) view.findViewById(R.id.et_link_address);
        final EditText et_link_title = (EditText) view.findViewById(R.id.et_link_title);

        Editable etext = et_link_address.getText();
        Selection.setSelection(etext, etext.length());

        //点击确实的监听
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String linkAddress = et_link_address.getText().toString();
                String linkTitle = et_link_title.getText().toString();

                if (linkAddress.endsWith("http://") || TextUtils.isEmpty(linkAddress)) {
                    Toast.makeText(DailyNoteEditorActivity.this, R.string.atom_ui_tip_note_input_url_addr, Toast.LENGTH_SHORT);
                } else if (TextUtils.isEmpty(linkTitle)) {
                    Toast.makeText(DailyNoteEditorActivity.this, R.string.atom_ui_tip_note_input_url, Toast.LENGTH_SHORT);
                } else {
                    editor.insertLink(linkAddress, linkTitle);
                    linkDialog.dismiss();
                }
            }
        });
        //点击取消的监听
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkDialog.dismiss();
            }
        });
        linkDialog.setCancelable(false);
        linkDialog.setView(view, 0, 0, 0, 0); // 设置 view
        linkDialog.show();
    }

    private AlertDialog mDialog;

    public void choosePictrueSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_choose_picture, (ViewGroup) this.getWindow().getDecorView(), false);
        TextView tv_change_gravtar_photos = (TextView) view.findViewById(R.id.tv_change_gravtar_photos);
        TextView tv_change_gravtar_camera = (TextView) view.findViewById(R.id.tv_change_gravtar_camera);
        tv_change_gravtar_photos.setText(R.string.atom_ui_btn_sel_gravantar_pic);
        tv_change_gravtar_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(DailyNoteEditorActivity.this, new int[]{PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE}, DailyNoteEditorActivity.this, SELECT_PIC);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        tv_change_gravtar_camera.setText(R.string.atom_ui_user_camera);
        tv_change_gravtar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(DailyNoteEditorActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA,
                                        PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE, PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, DailyNoteEditorActivity.this,
                                SHOW_CAMERA);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        builder.setView(view);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) return;
        if (requestCode == SHOW_CAMERA) {
            showCamera();
        } else if (requestCode == SELECT_PIC) {
            selectPic();
        }
    }

    void showCamera() {
        Logger.i("相机拍照");
        //新版图片选择器
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, ACTIVITY_GET_CAMERA_IMAGE);
    }

    void selectPic() {
        //新版图片选择器
        ImagePicker.getInstance().setSelectLimit(6);
        Intent intent1 = new Intent(this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
        startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO);
    }

    String imageUrl;

    public void getCameraImageResult(Intent data) {
        if (data != null) {
            //新版
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null && images.size() > 0) {
                imageUrl = images.get(0).path;
                uploadImage(imageUrl);
            }
        }
    }

    public void selectPhotoResult(Intent data) {
        try {
            if (data != null) {
                //新版图片选择器
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    for (ImageItem image : images) {
                        imageUrl = image.path;
                        uploadImage(imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("DailyNoteEditorActivity", "ERROR", e);
        }
    }

    public void startAnimation(View mView) {

        AlphaAnimation aa = new AlphaAnimation(0.4f, 1.0f); // 0完全透明 1 完全不透明
        // 以(0%,0.5%)为基准点，从0.5缩放至1
        ScaleAnimation sa = new ScaleAnimation(0.5f, 1, 0.5f, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // 添加至动画集合
        AnimationSet as = new AnimationSet(false);
        as.addAnimation(aa);
        as.addAnimation(sa);
        as.setDuration(500);
        // 执行动画
        mView.startAnimation(as);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.insert_line) {
            editor.insertHr();
        } else if (view.getId() == R.id.insert_redo) {
            editor.redo();
        } else if (view.getId() == R.id.insert_undo) {
            editor.undo();
        } else if (view.getId() == R.id.insert_link) {
            showInsertLinkDialog();
        } else if (view.getId() == R.id.insert_img) {
            choosePictrueSource();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_GET_CAMERA_IMAGE:
                getCameraImageResult(data);
                break;
            case ACTIVITY_SELECT_PHOTO:
                selectPhotoResult(data);
                break;
            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 单纯上传并图片
     *
     * @param filePath
     */
    public void uploadImage(String filePath) {
        final File origalFile = new File(filePath);
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = origalFile.getPath();
        request.FileType = UploadImageRequest.IMAGE;
//        request.id = message.getId();
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//                callback.updataProgress((int) (bytesWritten * 100 / contentLength), done);
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, final UploadImageResult result) {

                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传图片成功  msg url = " + result.httpUrl);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editor.insertImage(QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + result.httpUrl, "图片");
                        }
                    });
                } else {
                    Logger.i("上传图片失败 ");

                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传图片失败  msg url = " + msg);
            }

        };

        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    private void editNote() {
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (isUpdate) {
            params.put("qsid", String.valueOf(dailyMindSub.qsid));
            params.put("state",String.valueOf(DailyMindConstants.UPDATE));
        }else {
            params.put("state",String.valueOf(DailyMindConstants.CREATE));
        }
        params.put("qid",qid);
        params.put("type", DailyMindConstants.EVERNOTE + "");
        params.put("title", insert_title.getText().toString());
        params.put("desc", insert_title.getText().toString());
        params.put("content", editor.getHtml());
        evernotePresenter.operateDailyMindFromHttp(isUpdate ? DailyMindConstants.UPDATE_SUB : DailyMindConstants.SAVE_TO_SUB, params);
    }

    @Override
    public void setCloudSub() {

    }

    @Override
    public void addDailySub(final DailyMindSub dailyMindSub) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast((String) getText(R.string.atom_ui_tip_save_success));
                Intent intent = new Intent();
                intent.putExtra("data", dailyMindSub);
                setResult(-1, intent);
                finish();
            }
        });
    }


    @Override
    public void showErrMsg(final String error) {
        toast(error);
    }


}
