package com.qunar.im.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.ui.R;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.ui.view.QtNewActionBar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaokai on 15-10-23.
 */
public class CalculateCacheActivity extends IMBaseActivity {
    TextView cost, free, other;
    Button button;
    ImageView sector;
    LinearLayout info;

    Bitmap bitmap;
    Bitmap afterBitmap;

    int width,height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_calculate_cache);
        bindViews();
        width = Utils.dipToPixels(this,200);
        height = Utils.dipToPixels(this,200);
        initView();
    }
    private void bindViews() {

        info = (LinearLayout) findViewById(R.id.info);
        cost = (TextView) findViewById(R.id.cost);
        other = (TextView) findViewById(R.id.other);
        free = (TextView) findViewById(R.id.free);
        sector = (ImageView) findViewById(R.id.sector);
        button = (Button) findViewById(R.id.button);
        ((TextView)findViewById(R.id.app_name)).setText(GlobalConfigManager.getAppName());
    }


    public void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_cache);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        calculate();
    }

    @Override
    public void onPause()
    {
        if(bitmap!=null&&!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if(afterBitmap!=null&&!afterBitmap.isRecycled()) {
            afterBitmap.recycle();
            afterBitmap = null;
        }
        super.onPause();
    }

    private void calculate() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<File> files = Arrays.asList(MyDiskCache.getAllCacheDir());
                long tmpCost = 0;
                for (File dir : files) {
                    tmpCost += FileUtils.getDirSize(dir);
                }
                final long totalBytes = FileUtils.calculateDiskSize(FileUtils.getExternalFilesDir(QunarIMApp.getContext()));
                final long costBytes = tmpCost;
                final long freeBytes = FileUtils.calculateDiskFree(FileUtils.getExternalFilesDir(QunarIMApp.getContext()));
                final long otherBytes = totalBytes - freeBytes - costBytes;
                final String costStr = FileUtils.formatByteSize(costBytes);
                final String freeStr = FileUtils.formatByteSize(freeBytes);
                final String otherStr = FileUtils.formatByteSize(otherBytes);
                final float costRate = 1.0f * costBytes / totalBytes;
                final float freeRate = 1.0f * freeBytes / totalBytes;
                final float otherRate = 1.0f * otherBytes / totalBytes;
                final float costedRate = 0f;
                final float freedRate = freeRate + costRate;
                bitmap=createSector(width, height, costRate, freeRate);
                afterBitmap = createSector(width, height, costedRate, freedRate);
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (costBytes == 0) {
                            button.setClickable(false);
                            button.setBackground(getResources().getDrawable(R.drawable.atom_ui_common_button_gray_selector));
                        } else {
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (File dir : files) {
                                        FileUtils.removeDir(dir);
                                    }
                                    Toast.makeText(getBaseContext(), R.string.atom_ui_cache_clear_success, Toast.LENGTH_SHORT).show();
                                    button.setClickable(false);
                                    button.setBackground(getResources().getDrawable(R.drawable.atom_ui_common_button_gray_selector));
                                    cost.setText("0 Byte" + "\n(" + String.format("%.2f%%", costedRate * 100) + ")");
                                    free.setText(freeStr + "\n(" + String.format("%.2f%%", freedRate * 100) + ")");
                                    other.setText(otherStr + "\n(" + String.format("%.2f%%", otherRate * 100) + ")");
                                    sector.setImageBitmap(afterBitmap);
                                }
                            });
                            button.setClickable(true);
                            button.setBackground(getResources().getDrawable(R.drawable.atom_ui_common_button_blue_selector));
                        }

                        sector.setImageBitmap(bitmap);
                        sector.setVisibility(View.VISIBLE);
                        cost.setText(costStr + "\n(" + String.format("%.2f%%", costRate * 100) + ")");
                        free.setText(freeStr + "\n(" + String.format("%.2f%%", freeRate * 100) + ")");
                        other.setText(otherStr + "\n(" + String.format("%.2f%%", otherRate * 100) + ")");
                        info.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private Bitmap createSector(int width, int height, float rate, float free) {

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.atom_ui_file_sys_other));
        final RectF rectF = new RectF(0, 0, width, height);
        canvas.drawOval(rectF, p);
        p.setColor(getResources().getColor(R.color.atom_ui_file_sys_cost));
        if (rate < 0.001 && rate != 0f) {
            //占用太少画一条线
            canvas.drawLine(rectF.centerX(), rectF.centerY(), rectF.right, rectF.centerY(), p);
        }
        float degree = 360 * rate;
        canvas.drawArc(rectF, 0, -degree, true, p);
        p.setColor(getResources().getColor(R.color.atom_ui_file_sys_free));
        float degreeFree = 360 * free;
        canvas.drawArc(rectF, -degree, -degree - degreeFree, true, p);
        return bitmap;
    }
}
