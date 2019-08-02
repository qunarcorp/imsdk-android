package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;

/**
 * Created by saber on 15-12-31.
 */
public class ReceiveFileView extends RelativeLayout {
    TextView fileName,fileSize,fileFrom;
    ImageView fileIcon;
    CHorizontalProgressBar progressBar;


    public ReceiveFileView(Context context) {
        this(context, null);
    }

    public ReceiveFileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReceiveFileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_file_view, this, true);
        fileName = findViewById(R.id.file_name);
        fileSize = findViewById(R.id.file_size);
        fileIcon = findViewById(R.id.file_icon);
        fileFrom = findViewById(R.id.atom_ui_file_from);
        progressBar = findViewById(R.id.file_progress);

    }

    public void setProgress(float progress){
        progressBar.setVisibility(VISIBLE);
        progressBar.setProgress((int) progress);
    }

    public void finish(){
        progressBar.setVisibility(GONE);
    }


    public void setFileName(String name)
    {
        fileName.setText(name);
    }

    public void setFileSize(String size)
    {
        fileSize.setText(size);
    }

    public ImageView getFileIcon()
    {
        return fileIcon;
    }

    public TextView getFileFrom() {
        return fileFrom;
    }
}
