package com.qunar.rn_service.rnpackage;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.views.webview.ReactWebViewManager;
import com.qunar.rn_service.plugins.Login;
import com.qunar.rn_service.plugins.QChatUserSearch;
import com.qunar.rn_service.plugins.QTalkLoaclSearch;
import com.qunar.rn_service.plugins.QTalkProjectType;
import com.qunar.rn_service.plugins.QTalkSearchCheckUpdate;
import com.qunar.rn_service.plugins.TodoEventHandler;
import com.qunar.rn_service.rnplugins.QIMRnCheckUpdate;
import com.qunar.rn_service.rnplugins.QtalkPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchReactPackage implements ReactPackage{
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        List<NativeModule> modules = new ArrayList<>();

        modules.add(new Login(reactApplicationContext));

        modules.add(new QChatUserSearch(reactApplicationContext));

        modules.add(new QTalkLoaclSearch(reactApplicationContext));

        modules.add(new QTalkProjectType(reactApplicationContext));

        modules.add(new QTalkSearchCheckUpdate(reactApplicationContext));

        modules.add(new TodoEventHandler(reactApplicationContext));

        modules.add(new QtalkPlugin(reactApplicationContext));

        modules.add(new QIMRnCheckUpdate(reactApplicationContext));

        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        return Arrays.<ViewManager>asList(
                new ReactWebViewManager());
    }
}
