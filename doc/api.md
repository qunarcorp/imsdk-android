如何使用（相关API）
---
```init
  1.初始化SDK
  
  QIMSdk.getInstance().init(Application application)
  ```
 ```config
  2.配置导航地址
  
  QIMSdk.getInstance().setNavigationUrl(String url)
  ```  
 ```login
  3.用户名密码登录
  
  QIMSdk.getInstance().login(String uid,String password,LoginStatesListener loginStatesListener)
  ```   
 ```Autologin
  4.自动登录(本地缓存用户之前登录的用户名、token后可自动登录)
  
  QIMSdk.getInstance().autoLogin(LoginStatesListener loginStatesListener)
  ```   
 ```logout
  5.登出
  
  QIMSdk.getInstance().signOut()
  ```   
 ```state
  6.检查连接状态（是否已连接）
  
  QIMSdk.getInstance().isConnected()
  ```   
 ```isAutoLogin
  7.是否可以自动登录
  
  booelan b = QIMSdk.getInstance().isCanAutoLogin()
  ```   
 ```chat
  8.唤起聊天
  
  QIMSdk.getInstance().goToChatConv(Context context,String jid,int chatType)
  
  jid:消息的接收方
  chatType:会话类型(0 单聊 1 群聊 4 consult客服 5 consult客人)
  ```  
 ```unread
  9.获取未读消息数
  
  int count = QIMSdk.getInstance().selectUnreadCount()
  ```    
 ```convs
  10.获取本地会话列表数据
  
  LIst<RecentConversation> list = QIMSdk.getInstance().getRecentConversationList();
  ``` 
 ```clearcache
  11.清除缓存
  
  QIMSDK.getInstance().clearMemoryCache()
  ``` 
 ```getConversations
  12.获取消息列表Fragment
  
  QIMSDK.getInstance().getConversationListFragment()
  ```     
 ```getContacts
  13.获取通讯录Fragment
  
  QIMSDK.getInstance().getContactsFragment()
  ```   
 ```getUserCard
  14.获取个人名片
       /**
       * 名片获取逻辑 内存缓存->DB->网络
       * @param jid 
       * @param callBack
       * @param enforce 是否强制获取 true会走网络
       * @param todb 是否更新DB
       */
  QIMSDK.getInstance().getUserCard(String jid, IMLogicManager.NickCallBack callBack,boolean enforce,boolean todb)
  ```  
 ```getMucCard
  15.获取群名片
       /**
       * 群名片获取逻辑 同个人
       */
  QIMSDK.getInstance().getMucCard(String jid, IMLogicManager.NickCallBack callBack,boolean enforce,boolean todb)
  ```
 ```getUserId
  16.获取无domain的userid
  
  QIMSDK.getInstance().getUserIDNoDomain();
  ```  
 ```getUserIdwithdomain
  17.获取带domain的userid
  
  QIMSDK.getInstance().getUserIDWithDomain();
  ```    
 ```getnavurl
  16.获取当前导航地址
  
  QIMSDK.getInstance().getCurrentNavUrl();
  ```    
 ```getdomain
  17.获取当前域 domain
  
  QIMSDK.getInstance().getCurrentDomain();
  ```   
 ```loginByQvt
  18.使用qvt登录(仅限使用去哪儿账号登录的用户)
       * @param qvt 去哪儿用户登录凭证
       * @param plat 平台
       * @param loginStatesListener
  QIMSDK.getInstance().loginByQvt(String qvt,String plat,LoginStatesListener loginStatesListener);
  ```    