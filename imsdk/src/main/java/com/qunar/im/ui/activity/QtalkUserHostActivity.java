package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.qunar.im.base.jsonbean.DomainResult;
import com.qunar.im.base.protocol.LoginAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by froyomu on 2019/2/1
 * <p>
 * Describe:
 */
public class QtalkUserHostActivity extends IMBaseActivity{
    public static final int HOST_REQUEST_CODE = 1001;
    public static final int HOST_RESPONSE_CODE = 1002;

    private EditText keywordEditText;
    private ListView hostListView;

    private List<DomainResult.Result> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_host_qtuser);

        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_company);

        keywordEditText = (EditText) findViewById(R.id.keywordEditText);
        hostListView = (ListView) findViewById(R.id.hostListView);

        keywordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                LoginAPI.searchUserHost(text, new ProtocolCallback.UnitCallback<DomainResult>() {
                    @Override
                    public void onCompleted(final DomainResult domainResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> lists = new ArrayList<>();
                                if(domainResult != null){
                                    if(!ListUtil.isEmpty(domainResult.data)){
                                        results = domainResult.data;
                                        for(DomainResult.Result result:results){
                                            lists.add(result.name);
                                        }
                                        hostListView.setAdapter(new ArrayAdapter<>(QtalkUserHostActivity.this,android.R.layout.simple_list_item_1, lists));
                                    }
                                }else {
                                    toast("获取失败！");
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errMsg) {
                    }
                });
            }
        });
        hostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String navUrl = results.get(position).nav;

                String name = results.get(position).name;
                int domainId = results.get(position).domainId;

                Intent intent = new Intent();
                intent.putExtra(Constants.BundleKey.RESULT_HOST_NAME,name);
                intent.putExtra(Constants.BundleKey.RESULT_DOMAIN_ID,String.valueOf(domainId));
                intent.putExtra(Constants.BundleKey.NAV_ADD_URL,navUrl);
                setResult(HOST_RESPONSE_CODE,intent);
                finish();
            }
        });
    }
}
