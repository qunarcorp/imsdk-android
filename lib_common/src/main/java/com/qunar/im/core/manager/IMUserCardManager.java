package com.qunar.im.core.manager;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.CollectionCardData;
import com.qunar.im.base.jsonbean.CollectionMucCardData;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkHttpService;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.protobuf.entity.XMPPJID;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.utils.UrlCheckUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by may on 2017/7/13.
 */

public class IMUserCardManager {
    private static IMUserCardManager instance = new IMUserCardManager();

    public static IMUserCardManager getInstance() {
        return instance;
    }


    /**
     * 单条代收群名片
     *
     * @param targeId
     * @param enforce
     * @param insertDataBaseCallBack
     */
    public void updateCollectionMucCard(String targeId, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) {
        List<String> list = new ArrayList<>();
        list.add(targeId);
        updateCollectionMucCard(list, enforce, insertDataBaseCallBack);
    }

    /**
     * 多条代收群名片
     *
     * @param muclist
     * @param enforce
     * @param insertdataBaseCallBack
     */
    public void updateCollectionMucCard(List<String> muclist, boolean enforce, final InsertDataBaseCallBack insertdataBaseCallBack) {
        try {
            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            final Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
            final JSONArray params = new JSONArray();
            for (int i = 0; i < muclist.size(); i++) {
                JSONObject jb = new JSONObject();
                jb.put("m", muclist.get(i));
                jb.put("v", 0);
                params.put(jb);
            }
            Logger.i("请求参数:" + params);
            final String requestUrl = String.format("%s/qtapi/common/collection/getmucvcard.qunar",
                    QtalkNavicationService.getInstance().getJavaUrl()
            );
            final JSONObject[] response = new JSONObject[1];
            DispatchHelper.Async("collectionUserCard", new Runnable() {
                @Override
                public void run() {
                    response[0] = QtalkHttpService.postJson(requestUrl, params, cookie, 1);

                    if (response[0] == null) {
                        insertdataBaseCallBack.onComplate("error");
                        return;
                    }
                    Logger.i("代收群名片返回:" + response[0].toString());
                    CollectionMucCardData data = new Gson().fromJson(response[0].toString(), CollectionMucCardData.class);
                    if (data.isRet() && data.getData().size() > 0) {
                        IMDatabaseManager.getInstance().InsertCollectionMucCard(data.getData());
                        insertdataBaseCallBack.onComplate("success");
                    } else {
                        insertdataBaseCallBack.onComplate("error");
                    }

                    Logger.i("代收名片返回:" + response[0]);
                }
            });

        } catch (Exception e) {
            Logger.i("代收错误" + String.valueOf(e));
            e.printStackTrace();
        }
    }

    /**
     * 单条代收名片
     *
     * @param targeId
     * @param enforce
     * @param insertDataBaseCallBack
     */
    public void updateCollectionUserCard(String targeId, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) {
        List<String> list = new ArrayList<>();
        list.add(targeId);
        updateCollectionUserCard(list, enforce, insertDataBaseCallBack);

    }

    /**
     * 多条代收名片
     *
     * @param userList
     * @param enforce
     * @param insertDataBaseCallBack
     */
    public void updateCollectionUserCard(List<String> userList, boolean enforce, final InsertDataBaseCallBack insertDataBaseCallBack) {
        Logger.i("代收网络获取名片:" + JsonUtils.getGson().toJson(userList));
        try {
            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            final Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
            final JSONArray params = new JSONArray();
            for (int i = 0; i < userList.size(); i++) {
                JSONObject jb = new JSONObject();
                jb.put("u", QtalkStringUtils.parseId(userList.get(i)));
                jb.put("d", QtalkStringUtils.parseDomain(userList.get(i)));
                jb.put("v", 0);
                params.put(jb);
            }
            Logger.i("请求参数:" + params);

            final String requestUrl = String.format("%s/qtapi/common/collection/getvcard.qunar",
                    QtalkNavicationService.getInstance().getJavaUrl()
            );
            final JSONObject[] response = new JSONObject[1];
            DispatchHelper.Async("collectionUserCard", new Runnable() {
                @Override
                public void run() {
                    response[0] = QtalkHttpService.postJson(requestUrl, params, cookie, 1);
                    if (response[0] == null) {
                        insertDataBaseCallBack.onComplate("error");
                        return;
                    }
                    CollectionCardData data = JsonUtils.getGson().fromJson(response[0].toString(), CollectionCardData.class);
                    if (data.isRet() && data.getData().size() > 0) {
                        IMDatabaseManager.getInstance().InsertCollectionCard(data.getData());
                        insertDataBaseCallBack.onComplate("success");
                    } else {
                        insertDataBaseCallBack.onComplate("error");
                    }

                    Logger.i("代收名片返回:" + response[0]);
                }
            });


        } catch (Exception e) {
            Logger.i("代收错误" + String.valueOf(e));
            e.printStackTrace();
        }
    }

    public interface InsertDataBaseCallBack {
        void onComplate(String stat);
    }


    public List<JSONObject> updateMucCard(String mucId, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        List mucIds = new ArrayList();
        mucIds.add(mucId);
        return updateMucCard(mucIds, enforce, insertDataBaseCallBack);
    }

    public List<JSONObject> updateMucCardSync(String mucId, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        List mucIds = new ArrayList();
        mucIds.add(mucId);
        return updateMucCardSync(mucIds, enforce, insertDataBaseCallBack);
    }

    public List<JSONObject> updateUserCardByMemberList(List<GroupMember> memberList, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        List userIDs = new ArrayList();
        for (int i = 0; i < memberList.size(); i++) {
            userIDs.add(memberList.get(i).getMemberId());
        }
        return updateUserCard(userIDs, enforce, insertDataBaseCallBack);
    }

    public List<JSONObject> updateMucCardSync(List<String> mucIds, boolean enforce, final InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        if (mucIds == null || mucIds.size() <= 0) {
            return null;
        }
        JSONObject mucDic = IMDatabaseManager.getInstance().getMucInfos(mucIds);
        HashMap<String, JSONArray> domainDic = new HashMap<>();
        for (String mid : mucIds) {
            XMPPJID perJid = XMPPJID.parseJID(mid);
            boolean exists = mucDic.has(mid);
            if (!exists) {
                String domain = perJid.getDomain();
                if (domain != null) {
                    JSONArray muc = domainDic.get(domain);
                    if (muc == null) {
                        muc = new JSONArray();
                        JSONObject itemMap = new JSONObject();
                        itemMap.put("muc_name", perJid.fullname());
                        itemMap.put("version", "0");
                        muc.put(itemMap);
                        domainDic.put(domain, muc);
                    }

                }
            } else {
                JSONObject mDic = mucDic.getJSONObject(perJid.fullname());
                String groupId = mDic != null ? mDic.getString("GroupId") : mid;
                XMPPJID jid = XMPPJID.parseJID(groupId);

                String mucId = jid != null ? jid.fullname() : null;
                String domain = jid != null ? jid.getDomain() : null;

                if (domain != null && mucId != null) {
                    JSONArray users = domainDic.get(domain);

                    if (users == null) {
                        users = new JSONArray();
                    }

                    JSONObject itemMap = new JSONObject();
                    itemMap.put("muc_name", mucId);
                    if (enforce) {
                        itemMap.put("version", "0");
                    } else {
                        //先暂时都设置成0
//                        itemMap.put("version", mDic != null ? mDic.getString("LastUpdateTime") : Long.valueOf(0));
                        itemMap.put("version", "0");
                    }

                    users.put(itemMap);
                    domainDic.put(domain, users);
                }
            }
        }

        final JSONArray items = new JSONArray();

        for (String domain : domainDic.keySet()) {
            JSONArray muc = domainDic.get(domain);
            JSONObject item = new JSONObject();
            item.put("domain", domain);
            item.put("mucs", muc);
            items.put(item);
        }

        final String requestUrl = String.format("%s/muc/get_muc_vcard.qunar?u=%s&k=%s&p=android&v=%s&d=%s",
                QtalkNavicationService.getInstance().getHttpUrl(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppVersion(),
                QtalkNavicationService.getInstance().getXmppdomain()
        );


        Logger.i("传入的post参数:" + items);
        JSONObject result = QtalkHttpService.postJson(requestUrl, items);
        Logger.i("获取的名片--网络:" + result);
        JSONArray insertDatas = new JSONArray();
        if (result != null) {
            boolean ret = result.getBoolean("ret");
            if (ret) {
                JSONArray list = result.getJSONArray("data");

                for (int i = 0; i < list.length(); ++i) {
                    JSONObject userDic = list.getJSONObject(i);
                    String domain = userDic.getString("domain");
                    JSONArray userList = userDic.getJSONArray("mucs");
                    for (int j = 0; j < userList.length(); ++j) {
                        JSONObject dataDic = userList.getJSONObject(j);

                        String mucId = dataDic.getString("MN");
//
                        String mucName = dataDic.getString("SN");
//
                        String mucIntr = dataDic.getString("MD");

                        String mucTopic = dataDic.getString("MT");

                        String mucUrl = UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), dataDic.getString("MP"));

                        String mucV = dataDic.getString("UT");

                        JSONObject jb = new JSONObject();
                        jb.put("MN", mucId);
                        jb.put("SN", mucName);
                        jb.put("MD", mucIntr);
                        jb.put("MT", mucTopic);
                        jb.put("MP", mucUrl);
                        jb.put("VS", mucV);
                        insertDatas.put(jb);
                    }
                }

            }
        }
        if (insertDatas.length() > 0) {
            IMDatabaseManager.getInstance().updateMucCard(insertDatas);
            insertDataBaseCallBack.onComplate("success");
        } else {
            insertDataBaseCallBack.onComplate("filed");
        }


        return null;
    }

    public List<JSONObject> updateMucCard(List<String> mucIds, boolean enforce, final InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        if (mucIds == null || mucIds.size() <= 0) {
            return null;
        }
        JSONObject mucDic = IMDatabaseManager.getInstance().getMucInfos(mucIds);
        HashMap<String, JSONArray> domainDic = new HashMap<>();
        for (String mid : mucIds) {
            XMPPJID perJid = XMPPJID.parseJID(mid);
            boolean exists = mucDic.has(mid);
            if (!exists) {
                String domain = perJid.getDomain();
                if (domain != null) {
                    JSONArray muc = domainDic.get(domain);
                    if (muc == null) {
                        muc = new JSONArray();
                        JSONObject itemMap = new JSONObject();
                        itemMap.put("muc_name", perJid.fullname());
                        itemMap.put("version", "0");
                        muc.put(itemMap);
                        domainDic.put(domain, muc);
                    }

                }
            } else {
                JSONObject mDic = mucDic.getJSONObject(perJid.fullname());
                String groupId = mDic != null ? mDic.getString("GroupId") : mid;
                XMPPJID jid = XMPPJID.parseJID(groupId);

                String mucId = jid != null ? jid.fullname() : null;
                String domain = jid != null ? jid.getDomain() : null;

                if (domain != null && mucId != null) {
                    JSONArray users = domainDic.get(domain);

                    if (users == null) {
                        users = new JSONArray();
                    }

                    JSONObject itemMap = new JSONObject();
                    itemMap.put("muc_name", mucId);
                    if (enforce) {
                        itemMap.put("version", "0");
                    } else {
                        //先暂时都设置成0
//                        itemMap.put("version", mDic != null ? mDic.getString("LastUpdateTime") : Long.valueOf(0));
                        itemMap.put("version", "0");
                    }

                    users.put(itemMap);
                    domainDic.put(domain, users);
                }
            }
        }

        final JSONArray items = new JSONArray();

        for (String domain : domainDic.keySet()) {
            JSONArray muc = domainDic.get(domain);
            JSONObject item = new JSONObject();
            item.put("domain", domain);
            item.put("mucs", muc);
            items.put(item);
        }

        final String requestUrl = String.format("%s/muc/get_muc_vcard.qunar?u=%s&k=%s&p=android&v=%s&d=%s",
                QtalkNavicationService.getInstance().getHttpUrl(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppVersion(),
                QtalkNavicationService.getInstance().getXmppdomain()
        );


        Logger.i("传入的post参数:" + items);

        QtalkHttpService.asyncPostJson(requestUrl, items, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject result) throws JSONException {
                Logger.i("获取的名片--网络:" + result);
                JSONArray insertDatas = new JSONArray();
                if (result != null) {
                    boolean ret = result.getBoolean("ret");
                    if (ret) {
                        JSONArray dataList = new JSONArray();
                        JSONArray list = result.getJSONArray("data");

                        for (int i = 0; i < list.length(); ++i) {
                            JSONObject userDic = list.getJSONObject(i);
                            String domain = userDic.getString("domain");
                            JSONArray userList = userDic.getJSONArray("mucs");
                            for (int j = 0; j < userList.length(); ++j) {
                                JSONObject dataDic = userList.getJSONObject(j);

                                String mucId = dataDic.getString("MN");
//
                                String mucName = dataDic.getString("SN");
//
                                String mucIntr = dataDic.getString("MD");

                                String mucTopic = dataDic.getString("MT");

                                String mucUrl = UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), dataDic.getString("MP"));

                                String mucV = dataDic.getString("UT");

                                JSONObject jb = new JSONObject();
                                jb.put("MN", mucId);
                                jb.put("SN", mucName);
                                jb.put("MD", mucIntr);
                                jb.put("MT", mucTopic);
                                jb.put("MP", mucUrl);
                                jb.put("VS", mucV);
                                insertDatas.put(jb);
                            }
                        }

                    }
                }
                if (insertDatas.length() > 0) {
                    IMDatabaseManager.getInstance().updateMucCard(insertDatas);
                    insertDataBaseCallBack.onComplate("success");
                } else {
                    insertDataBaseCallBack.onComplate("filed");
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });


        return null;
    }

    public List<JSONObject> updateMucCardSync(long time) throws JSONException {

        final String requestUrl = String.format("%s/muc/get_user_increment_muc_vcard.qunar?u=%s&k=%s&p=android&v=%s&d=%s",
                QtalkNavicationService.getInstance().getHttpUrl(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppVersion(),
                QtalkNavicationService.getInstance().getXmppdomain()
        );

        final JSONObject params = new JSONObject();
        params.put("userid",CurrentPreference.getInstance().getUserid());
        params.put("lastupdtime",String.valueOf(time));

        JSONObject result = new JSONObject();
        Logger.i("updateMucCardSync:" + params);
        try {
            result = QtalkHttpService.postJson(requestUrl, params);
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONArray insertDatas = new JSONArray();
        if (result != null) {
            boolean ret = result.getBoolean("ret");
            if (ret) {
                JSONArray list = result.getJSONArray("data");

                for (int i = 0; i < list.length(); ++i) {
                    JSONObject dataDic = list.getJSONObject(i);

                    String mucId = dataDic.getString("MN");
//
                    String mucName = dataDic.getString("SN");
//
                    String mucIntr = dataDic.getString("MD");

                    String mucTopic = dataDic.getString("MT");

                    String mucUrl = UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), dataDic.getString("MP"));

                    String mucV = dataDic.getString("UT");

                    JSONObject jb = new JSONObject();
                    jb.put("MN", mucId);
                    jb.put("SN", mucName);
                    jb.put("MD", mucIntr);
                    jb.put("MT", mucTopic);
                    jb.put("MP", mucUrl);
                    jb.put("VS", mucV);
                    insertDatas.put(jb);
                }

            }
        }
        if (insertDatas.length() > 0) {
            IMDatabaseManager.getInstance().updateMucCard(insertDatas);
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Update_Muc_Vcard, "success");
        }

        return null;
    }


    public List<JSONObject> updateUserCard(String jid, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        List userIDs = new ArrayList();
        userIDs.add(jid);
        return updateUserCard(userIDs, enforce, insertDataBaseCallBack);
    }

    /**
     * 同步更新名片 需在非UI线程调用
     * @param jid
     * @param enforce
     * @param insertDataBaseCallBack
     * @return
     * @throws JSONException
     */
    public List<JSONObject> updateUserCardSync(String jid, boolean enforce, InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        List userIDs = new ArrayList();
        userIDs.add(jid);
        return updateUserCardSync(userIDs, enforce, insertDataBaseCallBack);
    }

    public List<JSONObject> updateUserCardSync(List<String> userIDs, boolean enforce, final InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        if (userIDs == null || userIDs.size() <= 0)
            return null;
        //先从数据库im_user表中获取对象 类似 {"hubin.hu@ejabhost1":{"UserId":"hubin.hu","XmppId":"hubin.hu@ejabhost1","Name":"胡滨hubin","HeaderSrc":"hubin.hu@ejabhost1.jpg","LastUpdateTime":1}}
        JSONObject usersDic = IMDatabaseManager.getInstance().getUserInfos(userIDs);

        HashMap<String, JSONArray> domainDic = new HashMap<>();
        //循环userIDs
        for (String uid : userIDs) {
            // 通过uid生成xmppjid  uid类似 hubin.hu@ejabhost1
            XMPPJID perJid = XMPPJID.parseJID(uid);
            //判断从数据库查出来的jb里面有没有这个uid对象
            boolean exists = usersDic.has(uid);
            if (!exists) {
                //如果没有
                String domain = perJid.getDomain();
                if (domain != null) {
                    JSONArray users = domainDic.get(domain);
                    if (users == null)
                        users = new JSONArray();
                    JSONObject itemMap = new JSONObject();
                    itemMap.put("user", perJid.getUser());
                    itemMap.put("version", "0");
                    users.put(itemMap);
                    domainDic.put(domain, users);
                }

            } else {
                //如果有
                JSONObject userDic = usersDic.getJSONObject(perJid.bareJID().fullname());
                String xmppId = userDic != null ? userDic.getString("XmppId") : uid;
                XMPPJID jid = XMPPJID.parseJID(xmppId);

                String userId = jid != null ? jid.getUser() : null;
                String domain = jid != null ? jid.getDomain() : null;

                if (domain != null && userId != null) {
                    JSONArray users = domainDic.get(domain);

                    if (users == null) {
                        users = new JSONArray();
                    }

                    JSONObject itemMap = new JSONObject();
                    itemMap.put("user", userId);
                    if (enforce) {
                        itemMap.put("version", "0");
                    } else {
                        itemMap.put("version", userDic != null ? (TextUtils.isEmpty(userDic.optString("HeaderSrc")) ? "0" : userDic.optString("LastUpdateTime")) : "0");
                    }

                    users.put(itemMap);
                    domainDic.put(domain, users);
                }
            }
        }

        final JSONArray items = new JSONArray();

        for (String domain : domainDic.keySet()) {
            JSONArray users = domainDic.get(domain);
            JSONObject item = new JSONObject();
            item.put("domain", domain);
            item.put("users", users);
            items.put(item);
        }

//        StringBuilder queryString = new StringBuilder("domain/get_msgs?");
//        Protocol.addBasicParamsOnHead(queryString);
        final String requestUrl = String.format("%s/domain/get_vcard_info.qunar?u=%s&k=%s&platform=android&version=%s&d=%s",
                QtalkNavicationService.getInstance().getHttpUrl(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppVersion(),
                QtalkNavicationService.getInstance().getXmppdomain()
        );

        Logger.i("requestUrl--网络:" + requestUrl);


        Logger.i("传入的post参数:" + items);
        JSONObject result = QtalkHttpService.postJson(requestUrl, items);
        Logger.i("获取的名片--网络:" + result);
        JSONArray insertDatas = new JSONArray();
        if (result != null) {
            boolean ret = result.getBoolean("ret");
            if (ret) {
                JSONArray list = result.getJSONArray("data");
                for (int i = 0; i < list.length(); ++i) {
                    JSONObject userDic = list.getJSONObject(i);
                    String domain = userDic.getString("domain");
                    JSONArray userList = userDic.getJSONArray("users");
                    for (int j = 0; j < userList.length(); ++j) {
                        JSONObject dataDic = userList.getJSONObject(j);
                        //"type": "qunar_emp",
                        String type = dataDic.getString("type");
//                        "username": "hubin.hu",
                        String userId = dataDic.getString("username");
//                        "domain": "ejabhost1",  "username": "hubin.hu",
                        String xmppId = String.format("%s@%s", userId, domain);

                        String webName = dataDic.has("webname") ? dataDic.getString("webname") : "";
//                          "nickname": "胡滨hubin",
                        String nickName = dataDic.has("nickname") ? dataDic.getString("nickname") : "";
                        String name = (StringUtils.isEmpty(webName) || "null".equals(webName)) ? nickName : webName;
                        // "imageurl": "file/v2/download/perm/ff1a003aa731b0d4e2dd3d39687c8a54.png",
//                        String headUrl =QtalkNavicationService.getInstance().getInnerFiltHttpHost()+"/"+ dataDic.getString("imageurl");
                        String headUrl = UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), dataDic.optString("imageurl"));
                        String mood = dataDic.optString("mood");
                        // "V": "1",
                        String version;
                        if (!dataDic.has("V")) {
                            version = "1";
                        } else {
                            version = dataDic.getString("V");
                        }

//
                        JSONObject jb = new JSONObject();
                        jb.put("U", userId);
                        jb.put("X", xmppId);
                        jb.put("N", name);
                        jb.put("H", headUrl);
                        jb.put("V", version);
                        jb.put("M", mood);
                        jb.put("type", type);
                        insertDatas.put(jb);
                    }
                }

            }
        }
        if (insertDatas.length() > 0) {
            IMDatabaseManager.getInstance().updateUserCard(insertDatas);
            insertDataBaseCallBack.onComplate("success");
        } else {
            insertDataBaseCallBack.onComplate("filed");
        }


        return null;
    }

    public List<JSONObject> updateUserCard(List<String> userIDs, boolean enforce, final InsertDataBaseCallBack insertDataBaseCallBack) throws JSONException {
        if (userIDs == null || userIDs.size() <= 0)
            return null;
        //先从数据库im_user表中获取对象 类似 {"hubin.hu@ejabhost1":{"UserId":"hubin.hu","XmppId":"hubin.hu@ejabhost1","Name":"胡滨hubin","HeaderSrc":"hubin.hu@ejabhost1.jpg","LastUpdateTime":1}}
        JSONObject usersDic = IMDatabaseManager.getInstance().getUserInfos(userIDs);

        HashMap<String, JSONArray> domainDic = new HashMap<>();
        //循环userIDs
        for (String uid : userIDs) {
            // 通过uid生成xmppjid  uid类似 hubin.hu@ejabhost1
            XMPPJID perJid = XMPPJID.parseJID(uid);
            //判断从数据库查出来的jb里面有没有这个uid对象
            boolean exists = usersDic.has(uid);
            if (!exists) {
                //如果没有
                String domain = perJid.getDomain();
                if (domain != null) {
                    JSONArray users = domainDic.get(domain);
                    if (users == null)
                        users = new JSONArray();
                    JSONObject itemMap = new JSONObject();
                    itemMap.put("user", perJid.getUser());
                    itemMap.put("version", "0");
                    users.put(itemMap);
                    domainDic.put(domain, users);
                }

            } else {
                //如果有
                JSONObject userDic = usersDic.getJSONObject(perJid.bareJID().fullname());
                String xmppId = userDic != null ? userDic.getString("XmppId") : uid;
                XMPPJID jid = XMPPJID.parseJID(xmppId);

                String userId = jid != null ? jid.getUser() : null;
                String domain = jid != null ? jid.getDomain() : null;

                if (domain != null && userId != null) {
                    JSONArray users = domainDic.get(domain);

                    if (users == null) {
                        users = new JSONArray();
                    }

                    JSONObject itemMap = new JSONObject();
                    itemMap.put("user", userId);
                    if (enforce) {
                        itemMap.put("version", "0");
                    } else {
                        itemMap.put("version", userDic != null ? (TextUtils.isEmpty(userDic.optString("HeaderSrc")) ? "0" : userDic.optString("LastUpdateTime")) : "0");
                    }

                    users.put(itemMap);
                    domainDic.put(domain, users);
                }
            }
        }

        final JSONArray items = new JSONArray();

        for (String domain : domainDic.keySet()) {
            JSONArray users = domainDic.get(domain);
            JSONObject item = new JSONObject();
            item.put("domain", domain);
            item.put("users", users);
            items.put(item);
        }

//        StringBuilder queryString = new StringBuilder("domain/get_msgs?");
//        Protocol.addBasicParamsOnHead(queryString);
        final String requestUrl = String.format("%s/domain/get_vcard_info.qunar?u=%s&k=%s&platform=android&version=%s&d=%s",
                QtalkNavicationService.getInstance().getHttpUrl(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppVersion(),
                QtalkNavicationService.getInstance().getXmppdomain()
        );

        Logger.i("requestUrl--网络:" + requestUrl);


        Logger.i("传入的post参数:" + items);
        QtalkHttpService.asyncPostJson(requestUrl, items, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject result) throws JSONException {
                Logger.i("获取的名片--网络:" + result);
                JSONArray insertDatas = new JSONArray();
                if (result != null) {
                    boolean ret = result.getBoolean("ret");
                    if (ret) {
                        JSONArray list = result.getJSONArray("data");
                        for (int i = 0; i < list.length(); ++i) {
                            JSONObject userDic = list.getJSONObject(i);
                            String domain = userDic.getString("domain");
                            JSONArray userList = userDic.getJSONArray("users");
                            for (int j = 0; j < userList.length(); ++j) {
                                JSONObject dataDic = userList.getJSONObject(j);
                                //"type": "qunar_emp",
                                String type = dataDic.getString("type");
//                        "username": "hubin.hu",
                                String userId = dataDic.getString("username");
//                        "domain": "ejabhost1",  "username": "hubin.hu",
                                String xmppId = String.format("%s@%s", userId, domain);

                                String webName = dataDic.has("webname") ? dataDic.getString("webname") : "";
//                          "nickname": "胡滨hubin",
                                String nickName = dataDic.has("nickname") ? dataDic.getString("nickname") : "";
                                String name = StringUtils.isEmpty(webName) ? nickName : webName;
                                String headUrl = UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), dataDic.optString("imageurl"));
                                String mood = dataDic.optString("mood");
                                // "V": "1",
                                String version;
                                if (!dataDic.has("V")) {
                                    version = "1";
                                } else {
                                    version = dataDic.getString("V");
                                }

//
                                JSONObject jb = new JSONObject();
                                jb.put("U", userId);
                                jb.put("X", xmppId);
                                jb.put("N", name);
                                jb.put("H", headUrl);
                                jb.put("V", version);
                                jb.put("M", mood);
                                jb.put("type", type);
                                insertDatas.put(jb);
                            }
                        }

                    }
                }
                if (insertDatas.length() > 0) {
                    IMDatabaseManager.getInstance().updateUserCard(insertDatas);
                    insertDataBaseCallBack.onComplate("success");
                } else {
                    insertDataBaseCallBack.onComplate("filed");
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });


        return null;
    }

    public List<JSONObject> updateUserCard(String jid, boolean enforce) throws JSONException {
        List userIDs = new ArrayList();
        userIDs.add(jid);
        return updateUserCard(userIDs, enforce);
    }

    public List<JSONObject> updateUserCardByMemberList(List<GroupMember> memberList, boolean enforce) throws JSONException {
        List userIDs = new ArrayList();
        for (int i = 0; i < memberList.size(); i++) {
            userIDs.add(memberList.get(i).getMemberId());
        }
        return updateUserCard(userIDs, enforce);
    }

    public List<JSONObject> updateUserCard(List<String> userIDs, boolean enforce) throws JSONException {
        if (userIDs == null || userIDs.size() <= 0)
            return null;
        JSONObject usersDic = IMDatabaseManager.getInstance().getUserInfos(userIDs);

        HashMap<String, JSONArray> domainDic = new HashMap<>();
        //循环userIDs
        for (String uid : userIDs) {
            // 通过uid生成xmppjid  uid类似 hubin.hu@ejabhost1
            XMPPJID perJid = XMPPJID.parseJID(uid);
            //判断从数据库查出来的jb里面有没有这个uid对象
            boolean exists = usersDic.has(uid);
            if (!exists) {
                //如果没有
                String domain = perJid.getDomain();
                if (domain != null) {
                    JSONArray users = domainDic.get(domain);
                    if (users == null)
                        users = new JSONArray();
                    JSONObject itemMap = new JSONObject();
                    itemMap.put("user", perJid.getUser());
                    itemMap.put("version", "0");
                    users.put(itemMap);
                    domainDic.put(domain, users);
                }

            } else {
                //如果有
                JSONObject userDic = usersDic.getJSONObject(perJid.bareJID().fullname());
                String xmppId = userDic != null ? userDic.getString("XmppId") : uid;
                XMPPJID jid = XMPPJID.parseJID(xmppId);

                String userId = jid != null ? jid.getUser() : null;
                String domain = jid != null ? jid.getDomain() : null;

                if (domain != null && userId != null) {
                    JSONArray users = domainDic.get(domain);

                    if (users == null) {
                        users = new JSONArray();
                    }

                    JSONObject itemMap = new JSONObject();
                    itemMap.put("user", userId);
                    if (enforce) {
                        itemMap.put("version", "0");
                    } else {
                        itemMap.put("version", userDic != null ? userDic.getString("LastUpdateTime") : Long.valueOf(0));
                    }

                    users.put(itemMap);
                    domainDic.put(domain, users);
                }
            }
        }

        final JSONArray items = new JSONArray();

        for (String domain : domainDic.keySet()) {
            JSONArray users = domainDic.get(domain);
            JSONObject item = new JSONObject();
            item.put("domain", domain);
            item.put("users", users);
            items.put(item);
        }
        final String requestUrl = String.format("%s/domain/get_vcard_info.qunar?u=%s&k=%s&platform=android&version=%s&d=%s",
                QtalkNavicationService.getInstance().getHttpUrl(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppVersion(),
                QtalkNavicationService.getInstance().getXmppdomain()
        );

        Logger.i("requestUrl--网络:" + requestUrl);
        final JSONObject[] result = new JSONObject[1];
        DispatchHelper.sync("getUserCard", new Runnable() {
            @Override
            public void run() {
                Logger.i("传入的post参数:" + items);
                result[0] = QtalkHttpService.postJson(requestUrl, items);
            }
        });

        Logger.i("获取的名片--网络:" + result[0]);
        if (result[0] != null) {
            JSONArray insertDatas = new JSONArray();
            boolean ret = result[0].getBoolean("ret");
            if (ret) {
                JSONArray dataList = new JSONArray();
                JSONArray list = result[0].getJSONArray("data");

                for (int i = 0; i < list.length(); ++i) {
                    JSONObject userDic = list.getJSONObject(i);
                    String domain = userDic.getString("domain");
                    JSONArray userList = userDic.getJSONArray("users");
                    for (int j = 0; j < userList.length(); ++j) {
                        JSONObject dataDic = userList.getJSONObject(j);
                        //"type": "qunar_emp",
                        String type = dataDic.getString("type");
//                        "username": "hubin.hu",
                        String userId = dataDic.getString("username");
                        if(CurrentPreference.getInstance().getUserid().equals(userId)){
                            //如果是登录用户 判断是不是客服
                            boolean isMerchant = type.equals("merchant");
                            CurrentPreference.getInstance().setMerchants(isMerchant);
                            DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.isAdminFlag + "_" + QtalkNavicationService.getInstance().getXmppdomain(),dataDic.optBoolean("adminFlag"));
                            DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.qchat_is_merchant,isMerchant);
                        }
//                        "domain": "ejabhost1",  "username": "hubin.hu",
                        String xmppId = String.format("%s@%s", userId, domain);

                        String webName = dataDic.has("webname") ? dataDic.getString("webname") : "";
//                          "nickname": "胡滨hubin",
                        String nickName = dataDic.has("nickname")?dataDic.getString("nickname"):xmppId;
                        String name = StringUtils.isEmpty(webName) ? nickName : webName;
                        // "imageurl": "file/v2/download/perm/ff1a003aa731b0d4e2dd3d39687c8a54.png",
//                        String headUrl =QtalkNavicationService.getInstance().getInnerFiltHttpHost()+"/"+ dataDic.getString("imageurl");
                        String headUrl = UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), dataDic.getString("imageurl"));
                        // "V": "1",
                        String version = dataDic.has("V") ? dataDic.getString("V") : "";
//                        Logger.i("图片1:"+QtalkNavicationService.getInstance().getInnerFiltHttpHost());
//                        Logger.i("图片2:"+headUrl);
//                        Logger.i("图片3:"+dataDic.getString("imageurl"));
                        JSONObject jb = new JSONObject();
                        jb.put("U", userId);
                        jb.put("X", xmppId);
                        jb.put("N", name);
                        jb.put("H", headUrl);
                        jb.put("V", version);
                        jb.put("type", type);
                        insertDatas.put(jb);
                    }
                }

            }
            if (insertDatas.length() > 0) {
                IMDatabaseManager.getInstance().updateUserCard(insertDatas);
//                for (int i = 0; i <userIDs.size() ; i++) {
//                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(insertDatas.getJSONObject(i).getString("N"),"HaveCard");
//                }
            }
        }
        return null;
    }


    public void updateGroupCard() {

    }
}
