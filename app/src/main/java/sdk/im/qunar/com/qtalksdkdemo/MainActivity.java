package sdk.im.qunar.com.qtalksdkdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.ui.activity.TabMainActivity;
import com.qunar.im.ui.sdk.QIMSdk;



public class MainActivity extends Activity {

    private Button autoLoginButton,startPlatForm;
    private TextView logcat_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoLoginButton = (Button) findViewById(R.id.autoLoginButton);
        startPlatForm = (Button) findViewById(R.id.startPlatForm);
        logcat_text = (TextView) findViewById(R.id.logcat_text);

//        startPlatForm.setText("启动" + CommonConfig.currentPlat);
        startPlatForm.setText("启动qtalk");
    }

    /**
     * 初始化sdk
     *只在Application 里调用一次
     * @param view
     */
    public void initQIMSdk(View view) {
        toast("已初始化");
//        QIMSdk.getInstance().init(getApplication());
    }

    /**
     * 初始化导航配置
     *
     * @param view
     */
    public void configNavigation(View view) {
        String url = "";//导航URl
        if(TextUtils.isEmpty(url)){
            toast("请配置正确的导航地址");
            return;
        }
        QIMSdk.getInstance().setNavigationUrl(url);
        toast("导航配置成功");
        logcat_text.append("导航地址：" + url + "\n");
    }

    /**
     * 登录
     *
     * @param view
     */
    public void login(View view) {
        if (!QIMSdk.getInstance().isConnected()){
            final ProgressDialog pd = ProgressDialog.show(this, "提示", "正在登录中。。。");
            if(QIMSdk.getInstance().isCanAutoLogin()){
                QIMSdk.getInstance().autoLogin(new QIMSdk.LoginStatesListener() {
                    @Override
                    public void isScuess(boolean b, String s) {
                        logcat_text.append(s);
                        pd.dismiss();
                        autoLoginButton.setText(s);
                        toast(s);
                    }
                });
            }else {
                final String uid = "";//用户名
                final String password = "";//密码
                QIMSdk.getInstance().login(uid, password, new QIMSdk.LoginStatesListener() {
                    @Override
                    public void isScuess(boolean b, String s) {
                        logcat_text.append("Uid：" + uid + "\n" + "Password：" + password);
                        pd.dismiss();
                        autoLoginButton.setText(s);
                        toast(s);
                    }
                });
            }

        }
        else
            toast("已登录！");
    }

    /**
     * 普通二人会话
     * @param view
     */
    public void goToChat(View view){
        QIMSdk.getInstance().goToChatConv(this,"hubo.hu@qunar.com",0);
    }

    /**
     * 群会话
     * @param view
     */
    public void goToGroup(View view){
        QIMSdk.getInstance().goToGroupConv(this,"a9e35fe11afc4578a54bb133055042d6@conference.qunar.com",1);
    }

    public void startMainActivity(View view) {

        startActivity(new Intent(this,TabMainActivity.class));
    }

    /**
     * 会话页
     * @param view
     */
    public void startConversationActivity(View view){
        startActivity(new Intent(this,ConversationListActivity.class));
    }

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
