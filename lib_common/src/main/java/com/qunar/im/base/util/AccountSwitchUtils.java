package com.qunar.im.base.util;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.AccountPassword;
import com.qunar.im.base.jsonbean.AccountSwitch;

import java.util.ArrayList;
import java.util.List;

/**
 * 切换账户utils
 * Created by lihaibin.li on 2017/9/7.
 */

public class AccountSwitchUtils {
    private static final String ACCOUNTS = "accounts";
    public static final String defalt_nav_name = "default";

    public static AccountSwitch accountSwitch = new AccountSwitch();

    public static void addAccount(String userid, String password, String navname, String navurl) {
        if(TextUtils.isEmpty(navname)) navname = defalt_nav_name;
        List<AccountPassword> accountPasswords = getAccounts();
        for (AccountPassword ap : accountPasswords) {
            if ((ap.userid + ap.navname).equals(userid + navname)) {
                ap.password = password;
                accountSwitch.list = accountPasswords;
                DataUtils.getInstance(com.qunar.im.common.CommonConfig.globalContext).putPreferences(ACCOUNTS, JsonUtils.getGson().toJson(accountSwitch));
                return;
            }

        }
        AccountPassword accountPassword = new AccountPassword();
        accountPassword.userid = userid;
        accountPassword.password = password;
        accountPassword.navname = navname;
        accountPassword.navurl = navurl;
        accountPasswords.add(accountPassword);
        accountSwitch.list = accountPasswords;
        DataUtils.getInstance(com.qunar.im.common.CommonConfig.globalContext).putPreferences(ACCOUNTS, JsonUtils.getGson().toJson(accountSwitch));
    }

    public static List<AccountPassword> getAccounts() {
        String accounts = DataUtils.getInstance(com.qunar.im.common.CommonConfig.globalContext).getPreferences(ACCOUNTS, "");
        if (!TextUtils.isEmpty(accounts)) {
            accountSwitch = JsonUtils.getGson().fromJson(accounts, AccountSwitch.class);
            if (accountSwitch != null)
                return accountSwitch.list;
        }
        return new ArrayList<>();
    }
}
