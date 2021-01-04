package com.taptorestart.blog.module.xml;


import com.taptorestart.blog.model.RSSItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class XMLParserRSSMO {

    public static ArrayList<RSSItem> rssToItem(String xml) {
        ArrayList<RSSItem> rssItemList = new ArrayList<>();
        RSSItem rssItem = new RSSItem();

        XmlPullParserFactory factory = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            String sTag = "";
            String text = "";
            boolean isItem = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) { // 문서의 시작
                } else if (eventType == XmlPullParser.START_TAG) {
                    sTag = xpp.getName();
                    if (sTag.equals("item")) {
                        isItem = true;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (xpp.getText() != null) {
                        text = xpp.getText().trim();
                    }
                } else if (eventType == XmlPullParser.END_TAG) { //END_TAG
                    sTag = xpp.getName();
                    if(isItem) {
                        if (sTag.equals("title")) {
                            rssItem.title = text;
                        }else if(sTag.equals("link")){
                            rssItem.link = text;
                        }else if(sTag.equals("pubDate")){
                            rssItem.pubDate = text;
                        }else if(sTag.equals("guid")){
                            rssItem.guid = text;
                        }
                    }
                    if(sTag.equals("item")) {
                        isItem = false;
                        rssItemList.add(rssItem);
                        rssItem = new RSSItem();
                    }
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return rssItemList;
    }
}
