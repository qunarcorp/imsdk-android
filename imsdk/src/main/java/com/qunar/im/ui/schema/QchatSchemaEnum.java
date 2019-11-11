/**
 * Copyright © 2013 Qunar.com Inc. All Rights Reserved.
 */
package com.qunar.im.ui.schema;

/**
 * jerry.li
 * 度假scheme跳转的枚举类
 */
public enum QchatSchemaEnum {
    select_user(QSelectUserImpl.instance,"/select_user"),
    chat(QchatSchemaImpl.instance,"/chat"),
    group_member(QGroupMemberSchemaImpl.instance,"/group_member"),
    personal_info(QPersonalSchemaImpl.instance,"/personal_info"),
    robot(QRobotSchemaImpl.instance,"/robot"),
    search(QSearchSchemaImpl.instance,"/search"),
    ad(QAdSchemaImpl.instance,"/adshow"),
    searchDetails(SearchDetailsImpl.instance,"/search_details"),
    qrcode(QRSchemeImpl.instance,"/qrcode"),
    hongbao(QHongBaoSchemaImpl.instance,"/hongbao"),
    hongbao_balance(QHongBaoBalanceSchemaImpl.instance,"/hongbao_balance"),
    developer_chat(QOpenDeveloperChat.instance,"/developer_chat"),
    dress_up_vc(QDressUpVcImpl.instance,"/dress_up_vc"),
    mc_config(QMcConfigImpl.instance,"/mc_config"),
    about(QAboutImpl.instance,"/about"),
    logout(QLogoutImpl.instance,"/logout"),
    openGroupChat(QOpenGroupCaht.instance,"/openGroupChat"),
    openSingleChat(QOpenSingleChat.instance,"/openSingleChat"),
    openUserWorkWorld(QOpenUserWorkWorld.instance,"/openUserWorkWorld"),
    openChatForSearch(QOpenChatForSearch.instance,"/openChatForSearch"),
    openChatForNetSearch(QopenChatForNetSearch.instance,"/openChatForNetSearch"),
    openSearchChatImage(QOpenSearchChatImage.instance,"/openSearchChatImage"),
    unreadList(QOpenUnReadListImpl.instance,"/unreadList"),
    publicNumber(QPublicImpl.instance,"/publicNumber"),
    addFriend(QAddFriend.instance,"/addFriend"),
    openUserCard(QOpenUserCard.instance,"/openUserCard"),
    openUserMedalPage(QOpenFlutterView.instance,"/openMedalPage"),
    openWebView(QOpenWebView.instance,"/openWebView"),
    openSearchActivity(QopenSearchActivity.instance,"/openSearchActivity"),
    openBigImage(QOpenBigImage.instance,"/openBigImage"),
    openPictureSelector(QOpenPictureSelector.instance,"/openPictureSelector"),
    openCamerSelecter(QOpenCamerSelecter.instance,"/openCamerSelecter"),
    openPhoneNumber(QOpenPhoneNumber.instance,"/openPhoneNumber"),
    openOrganizational(QOpenOrganizationalImpl.instance,"/openOrganizational"),
    openEmail(QOpenEmailImpl.instance,"/openEmail"),
    account_info(QAccountInfoImpl.instance,"/account_info"),
    myfile(QMyFileImpl.instance,"/myfile"),
    openSingleChatInfo(QOpenSingleChatInfoSchemaImpl.getInstance(),"/openSingleChatInfo"),
    openGroupChatInfo(QOpenGroupChatInfoSchemaImpl.getInstance(),"/openGroupChatInfo"),
    openAccountSwitch(QAccountSwitchSchemaImpl.getInstance(),"/accountSwitch"),
    openDownLoad(QOpenFileDownLoadImpl.instance,"/openDownLoad"),
    openScan(QOpenScanImpl.getInstance(),"/openScan"),
    openNoteBook(QOpenNoteBook.getInstance(),"/openNoteBook"),
    openTravelCalendar(QOpenTravelCalendarImpl.getInstance(),"/openTravelCalendar"),
    openExternalRN(QopenExternalRNImpl.getInstance(),"/openExternalRN"),
    openMySetting(QOpenRnMySetting.getInstance(),"/openMyRnSetting"),
    openDomainSearch(QOpenDomainSearchImpl.getInstance(),"/openDomainSearch"),
    openHeadLine(QOpenHeadLineSchemaImpl.getInstance(),"/headLine"),
    openNavConfig(QOpenNavConfigImpl.getInstance(),"/openNavConfig");






    private String path;
    private QChatSchemaService service;
    /**
     * 如果是startActivityForResult启动的，需要backToActivity的scheme需要在这个集合里
     */

    QchatSchemaEnum(QChatSchemaService service, String path) {
        this.service = service;
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }

    public QChatSchemaService getService() {
        return service;
    }


    public static QchatSchemaEnum getSchemeEnumByPath(String path){
        if(path == null)
            return null;
        for(QchatSchemaEnum e:QchatSchemaEnum.values()){
            if(e.path.equalsIgnoreCase(path)){
                return e;
            }
        }
        return null;
    }

}
