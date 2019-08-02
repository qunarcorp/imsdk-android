package com.qunar.im.ui.util;

import com.qunar.im.ui.R;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class FileTypeUtil {

    private static FileTypeUtil instance = new FileTypeUtil();

    String imgFile = "jpg|jpeg|png|gif|bmp";
    String wordFile = "doc|docx";
    String excelFile = "xls|xlsx";
    String pptFile = "ppt|pptx";
    String pdfFile = "pdf";
    String videoFile = "mp4|avi|mov|mpeg|wmv|3gp";
    String voiceFile = "mp3|mid|wav|rm|ape|flac|amr";
    String zipFile = "zip|rar|7z|jar";
    String txtFile = "txt|java";
    String apkFile = "apk";
    String htmlFile = "html";

    public static FileTypeUtil getInstance(){
        return instance;
    }

    public int getFileTypeBySuffix(String fExt) {
        int fileType = R.drawable.atom_ui_icon_zip_video;

        if (imgFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_pic_video;
        } else if (wordFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_word_video;
        } else if (excelFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_execl_video;
        } else if (pptFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_ppt_video;
        } else if (videoFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_file_video;
        } else if (voiceFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_audio_video;
        } else if (zipFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_zip_video;
        } else if (pdfFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_pdf_video;
        } else if (htmlFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_html_video;
        } else if (txtFile.contains(fExt)) {
            fileType = R.drawable.atom_ui_icon_txt_video;
        }
        return fileType;
    }
}
