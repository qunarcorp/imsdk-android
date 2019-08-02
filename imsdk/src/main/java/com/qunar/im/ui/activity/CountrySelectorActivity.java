package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.CountryAdapter;
import com.qunar.im.ui.adapter.CountryNode;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.util.CountryUtil;
import com.qunar.im.base.util.HanziToPinyin;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.indexlistview.IndexableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saber on 16-2-25.
 */
public class CountrySelectorActivity extends IMBaseActivity {

    CountryAdapter adapter;
    IndexableListView indexableListView;

    int selectId;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_country_select);
        initViews();
        initAdapter();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBarTitle(R.string.atom_ui_login_country_or_region);
    }

    private void initViews()
    {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        indexableListView = (IndexableListView) findViewById(R.id.country_list);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null&&bundle.containsKey(Constants.BundleKey.SELECT_COUNTRY_ID))
        {
            selectId = bundle.getInt(Constants.BundleKey.SELECT_COUNTRY_ID);
        }
    }

    private void initAdapter()
    {
        String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Map<Integer,List<CountryNode>> countrySelector = new HashMap<>();
        for(Integer key : CountryUtil.countries.keySet()) {
            String name = getString(key);
            String abb = HanziToPinyin.zh2Abb(name);
            char ch = abb.charAt(0);
            int ids = mSections.indexOf(ch)+1;
            if (ids >= 0) {
                CountryNode node = new CountryNode();
                node.name = name;
                node.alias =abb;
                node.code = CountryUtil.countries.get(key);
                node.resourceId = key;
                List<CountryNode> nodes = countrySelector.get(ids);
                if (nodes == null) nodes = new ArrayList<CountryNode>();
                nodes.add(node);
                countrySelector.put(ids, nodes);
            }
        }
        adapter = new CountryAdapter(this,null,R.layout.atom_ui_item_country);
        adapter.setNodes(countrySelector);
        adapter.selectId = selectId;
        indexableListView.setAdapter(adapter);
        indexableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryNode node = (CountryNode) parent.getAdapter().getItem(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.BundleKey.SELECT_COUNTRY_ID,node.resourceId);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

}
