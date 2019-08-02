package com.qunar.im.base.view.faceGridView;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class EmoticonFileUtils {
    private  String ns = null;

    private  String facePath ;
    private  String emoticonXml ;
    private  EmoticionMap eMap;

    /**
     * 初始化
     * @param facePath 图片目录地址
     * @param emoticonXml 配置文件地址
     */
    public EmoticonFileUtils(String facePath,String emoticonXml)
    {
        this.facePath = facePath;
        this.emoticonXml = emoticonXml;
    }
    public  EmoticionMap geteMap(Context context) {

        try {
            parse(context.getResources().getAssets().open(emoticonXml));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eMap;
    }

    public  EmoticionMap geteMap() {
        try {
            File file = new File(emoticonXml);
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
            parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eMap;
    }

    private  void parse(InputStream in)
    {
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(in,"utf-8");
            parser.nextTag();
            readRoot(parser);
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  void readRoot(XmlPullParser parser) {

        try {
            parser.require(XmlPullParser.START_TAG,ns,"FACESETTING");
            while(parser.next() != XmlPullParser.END_TAG)
            {
                if(parser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }
                readDefaultFace(parser);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void readDefaultFace(XmlPullParser parser)
    {
        try {
            parser.require(XmlPullParser.START_TAG, ns, "DEFAULTFACE");

            String version = parser.getAttributeValue(ns,"version");
            int showAll = Integer.valueOf(parser.getAttributeValue(ns, "showall"));
            int count = Integer.valueOf(parser.getAttributeValue(ns, "count"));
            int line = Integer.valueOf(parser.getAttributeValue(ns,"line"));
            String packageId = parser.getAttributeValue(ns,"package");
            eMap = new EmoticionMap(version,count,showAll,line,packageId);
            while(parser.next() != XmlPullParser.END_TAG)
            {
                if(parser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }
                readFaceEntity(parser);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void readFaceEntity(XmlPullParser parser){
        try {
            parser.require(XmlPullParser.START_TAG,ns,"FACE");

            String shortcut = parser.getAttributeValue(ns, "shortcut");
            String id = parser.getAttributeValue(ns,"id");
            String tip = parser.getAttributeValue(ns,"tip");
            EmoticonEntity entity = new EmoticonEntity();

            entity.id = id;
            entity.shortCut=shortcut;
            entity.multiframe=1;
            entity.tip=tip;

            while (parser.next() != XmlPullParser.END_TAG)
            {
                if(parser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }

                String name = parser.getName();

                if(name.equals("FILE_FIXED"))
                {
                     entity.fileFiexd=facePath + readFileName(parser);
                }
                else
                {
                    entity.fileOrg=(facePath+readFileOrg(parser));
                }
            }
            eMap.pusEntity(shortcut,entity);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  String readFileName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,ns,"FILE_FIXED");
        String result = "";
        if(parser.next()== XmlPullParser.TEXT)
        {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG,ns,"FILE_FIXED");
        return result;
    }

    private  String readFileOrg(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,ns,"FILE_ORG");
        String result = "";
        if(parser.next()== XmlPullParser.TEXT)
        {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG,ns,"FILE_ORG");
        return result;
    }

    /*private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }*/
}
