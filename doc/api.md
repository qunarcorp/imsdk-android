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