package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by saber on 16-4-19.
 */
public class RemoteConfig extends BaseJsonResult {
    public List<ConfigItem> data;
    public static class ConfigItem
    {
        public String key;
        public String value;
        public String version;
    }

    public static class EmotionItem
    {
        public String httpUrl;
    }
}
