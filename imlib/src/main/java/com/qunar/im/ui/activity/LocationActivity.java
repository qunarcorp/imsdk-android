package com.qunar.im.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationData.Builder;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.platform.comapi.location.CoordinateType;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.PoiItemAdapter;
import com.qunar.im.ui.view.QtNewActionBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends IMBaseActivity implements BDLocationListener, OnGetPoiSearchResultListener, AdapterView.OnItemClickListener{
    private static final String TAG = "LocationActivity";
    private int CLIP_WIDTH;
    private int CLIP_HEIGHT;
    public static final int TYPE_SEND = 0;
    public static final int TYPE_RECEIVE = 1;
    private String street;
    private int ORIGIN_HEIGHT;
    private int MAX_HEIGHT;
    private int pageNum = 0;
    private boolean refresh = true;
    MapView mapView;
    LinearLayout bottom_container;
    TextView position, locName;
    ImageView navigation;
    ImageView img_reset_location;
    private String address = "", name = "";
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private Bundle bundle;
    private ListView list;
    private PoiSearch poiSearch;
    private PoiItemAdapter adapter;
    private double latitude, longitude;
    private int firstVisible;
    private boolean needChange;
    boolean changeHeight;
    private GeoCoder geoCoder;
    List<PoiInfo> infoss = new ArrayList<PoiInfo>();
    private static int poiNum = 4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.atom_ui_activity_location);
        bundle = getIntent().getExtras();
        bindViews();
        initView();
        ORIGIN_HEIGHT = Utils.dipToPixels(this, 256);
        MAX_HEIGHT = Utils.dipToPixels(this, 360);
        CLIP_WIDTH = (int) (Utils.getScreenWidth(this) * 0.8);
        CLIP_HEIGHT = CLIP_WIDTH * 9 / 16;
    }

    private void bindViews() {
        mapView = (MapView) findViewById(R.id.mapView);
        bottom_container = (LinearLayout) findViewById(R.id.bottom_container);
        position = (TextView) findViewById(R.id.position);
        navigation = (ImageView) findViewById(R.id.navigation);
        img_reset_location = (ImageView) findViewById(R.id.img_reset_location);
        list = (ListView) findViewById(R.id.poi_list);
        locName = (TextView) findViewById(R.id.name);
        img_reset_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude > 0 && latitude > 0) {
                    MyLocationData data = baiduMap.getLocationData();
                    LatLng latLng = new LatLng(data.latitude, data.longitude);
                    baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.6f));
                    baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                }
            }
        });
        adapter = new PoiItemAdapter(this);
        list.setAdapter(adapter);
        if (bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_SEND) {
            bottom_container.setVisibility(View.GONE);
            //发送位置
            list.setVisibility(View.VISIBLE);
            list.setOnItemClickListener(this);
            list.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    switch (scrollState) {
//                        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                            if (refresh && view.getLastVisiblePosition() == (view.getCount() - 1)) {
//                                refresh = false;
//                                PoiInfo info=new PoiInfo();
//                                info.location=new LatLng(latitude, longitude);
//                                searchMutiNearByPoi(info,++pageNum);
//                            }
//                            break;
//                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    firstVisible = firstVisibleItem;
                    needChange = totalItemCount > visibleItemCount;
                }
            });
            list.setOnTouchListener(new View.OnTouchListener() {
                boolean down;
                float y;


                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (needChange) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                y = event.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                if (down) {
                                    down = false;
                                    return true;
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (down) return true;
                                if (firstVisible == 0) {
                                    int direct = 0;
                                    if (y - event.getY() < -32) {
                                        direct = -1;
                                    } else if (y - event.getY() > 32) {
                                        direct = 1;
                                    }
                                    if (direct == 1 && !changeHeight) {
                                        down = true;
                                        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                MAX_HEIGHT));
                                        changeHeight = true;
                                        return true;
                                    } else if (direct == -1 && changeHeight) {
                                        down = true;
                                        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ORIGIN_HEIGHT));
                                        changeHeight = false;
                                        return true;
                                    }
                                }
                                break;
                        }
                    }
                    return false;
                }
            });
        } else if (bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_RECEIVE) {
            list.setVisibility(View.GONE);
        }
        //去掉　ZoomControls 控件　
        for (int index = 0; index < mapView.getChildCount(); index++) {
            View v = mapView.getChildAt(index);
            if (v instanceof ZoomControls) {
                mapView.removeView(v);
                break;
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocationClient();
        mapView.onResume();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    LogUtil.d(TAG, "未找到结果");
                } else {
                    if (poiSearch != null && bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_SEND) {
                        if(result.getAddress() != null && result.getAddress().equals(address)){//防止没必要的刷新试图
                            return;
                        }
                        final PoiInfo info = new PoiInfo();
                        info.address = result.getAddress();
                        address = info.address;
                        int index = -1;
                        if ((index = info.address.indexOf("中国")) != -1) {
                            info.address = info.address.substring(index + "中国".length());
                        }
                        info.name = info.address;
                        name = info.name;
                        latitude = result.getLocation().latitude;
                        longitude = result.getLocation().longitude;
                        info.location = new LatLng(latitude, longitude);
                        street = result.getAddressDetail().street;
                        List<PoiInfo> infos = new ArrayList<>(1);
                        infos.add(info);
                        adapter.setPoiInfo(infos);
                        adapter.setCheckPosition(0);
                        adapter.notifyDataSetChanged();
                        infoss.clear();
                        getPoiResultCount=1;
                        searchMutiNearByPoi(info,pageNum);
                    }
                }
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (poiSearch != null)
            poiSearch.destroy();
        locationClient.unRegisterLocationListener(this);
        locationClient.stop();
        geoCoder.destroy();
        mapView.onPause();
        super.onPause();
    }

    protected void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        initBaiduMap();
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            LatLng finishLng;

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                changeHeight = false;
                list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ORIGIN_HEIGHT));
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_SEND) {
                    finishLng = mapStatus.target;
                    latitude = finishLng.latitude;
                    longitude = finishLng.longitude;
                    startLocationAndSetIcon();
                }
            }
        });
    }

    protected void startLocationAndSetIcon()
    {
        LatLng point = new LatLng(latitude, longitude);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        OverlayOptions options = new MarkerOptions().draggable(false)
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.atom_ui_map_mark));
        if (overlay != null) {
            overlay.remove();
        }
        overlay = baiduMap.addOverlay(options);
//        adapter.notifyDataSetChanged();
    }

    private void initLocationClient() {
        geoCoder = GeoCoder.newInstance();
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
        locationClient = new LocationClient(getApplication());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType(CoordinateType.BD09LL);
//        option.setScanSpan(5000);//
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(true);
        option.SetIgnoreCacheException(true);
        option.setEnableSimulateGps(true);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(this);
        locationClient.start();
    }

    private void initBaiduMap() {
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setTrafficEnabled(false);
        baiduMap.getUiSettings().setZoomGesturesEnabled(true);
        baiduMap.getUiSettings().setCompassEnabled(false);
        if (bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_RECEIVE) {
            //别人发送过来的位置
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
            baiduMap.setMyLocationConfigeration(configuration);
            final String latitude = bundle.getString("latitude");
            final String longitude = bundle.getString("longitude");
            if (latitude != null && longitude != null) {
                LatLng point = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                OverlayOptions options = new MarkerOptions().draggable(false)
                        .position(point)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.atom_ui_map_mark));
                baiduMap.addOverlay(options);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.6f));
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                setActionBarTitle(R.string.atom_ui_title_location_ta);
                position.setText(bundle.getString("address"));
                locName.setText(bundle.getString("name"));
                navigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyLocationData data = baiduMap.getLocationData();
                        if (data != null) {
                            final NaviParaOption option = new NaviParaOption();
                            option.startPoint(new LatLng(data.latitude, data.longitude));
                            option.endPoint(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));
                            try {

                                BaiduMapNavigation.openBaiduMapNavi(option, LocationActivity.this);

                            } catch (BaiduMapAppNotSupportNaviException e) {
                                LogUtil.e(TAG,"ERROR",e);
//                                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                                commonDialog.setMessage(getString(R.string.atom_ui_tip_no_baidumap));
                                commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
                                commonDialog.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //下载最新版本的百度地图
                                        OpenClientUtil.getLatestBaiduMapApp(LocationActivity.this);
                                        dialog.dismiss();
                                    }
                                });

                                commonDialog.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                commonDialog.show();
                            }
                        }
                    }
                });
                setActionBarRightText(0);
            }
        } else if (bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_SEND) {
            //自己的位置
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
//            MapStatus.Builder builder = new MapStatus.Builder();
//            builder.overlook(0);
//            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            baiduMap.setMyLocationConfigeration(configuration);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.6f));//250
            setActionBarTitle(R.string.atom_ui_title_my_location);
            setActionBarRightIcon(R.string.atom_ui_new_send);
            setActionBarRightIconClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendLocation();
                }
            });

        }
    }

    private void sendLocation() {
        int centerX = mapView.getWidth() / 2;
        int centerY = mapView.getHeight() / 2;
        Rect rect = new Rect(centerX - CLIP_WIDTH / 2, centerY - CLIP_HEIGHT / 2, centerX + CLIP_WIDTH / 2, centerY + CLIP_HEIGHT / 2);
        baiduMap.setMyLocationEnabled(false);
        baiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                File file = onMapScreenShot(bitmap);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.BundleKey.LATITUDE, latitude);
                resultIntent.putExtra(Constants.BundleKey.LONGITUDE, longitude);
                resultIntent.putExtra(Constants.BundleKey.ADDRESS, address);
                resultIntent.putExtra(Constants.BundleKey.LOCATION_NAME, name);
                resultIntent.putExtra(Constants.BundleKey.FILE_NAME, file.getAbsolutePath());
                setResult(Activity.RESULT_OK, resultIntent);
                LocationActivity.this.finish();
            }
        });
    }

    public File onMapScreenShot(Bitmap bitmap) {
        File file = MyDiskCache.getTempFile("amp_shot.jpg");
        if (null != bitmap) {
            try {
                if (file.exists()) file.delete();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                bitmap.recycle();
                try {
                    fos.flush();
                } catch (IOException e) {
                    LogUtil.e(TAG,"ERROR",e);
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    LogUtil.e(TAG,"ERROR",e);
                }
            } catch (FileNotFoundException e) {
                LogUtil.e(TAG,"ERROR",e);
            } catch (IOException e) {
                LogUtil.e(TAG,"ERROR",e);
            }
        }
        return file;
    }
    private void searchMutiNearByPoi(final PoiInfo info, final int pageNum){
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                searchNearByPoi(info, "写字楼",pageNum);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG,"ERROR",e);
                }
                searchNearByPoi(info, "大厦",pageNum);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG,"ERROR",e);
                }
                searchNearByPoi(info, "美食",pageNum);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG,"ERROR",e);
                }
                searchNearByPoi(info, street,pageNum);
            }
        });
    }
    private void searchNearByPoi(PoiInfo info, String keyword,int pageNum) {
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword(keyword).
                radius(1000).pageNum(pageNum).pageCapacity(5).
                sortType(PoiSortType.distance_from_near_to_far).
                location(new LatLng(info.location.latitude, info.location.longitude));
        poiSearch.searchNearby(option);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        MyLocationData data;
        Builder builder = new Builder();
        Logger.i(TAG + ":" + "addstr:" + bdLocation.getAddrStr()
                + "\n" + "latitude:" + bdLocation.getLatitude()
                + "\n" + "longitude:" + bdLocation.getLongitude()
                + "\n" + "coortype:" + bdLocation.getCoorType());
        if (baiduMap != null) {
            data = builder.direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            baiduMap.setMyLocationData(data);
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            address = bdLocation.getAddress().address;
            mNewActionBar.getRightText().setClickable(true);
        }
        if (bundle.getInt(Constants.BundleKey.LOCATION_TYPE) == TYPE_SEND) {
            startLocationAndSetIcon();
        }

    }
    int getPoiResultCount=1;
    boolean receiveAll=false;
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (getPoiResultCount<poiNum){
            getPoiResultCount++;
        }else {
            getPoiResultCount=1;
            receiveAll=true;
        }
        if (poiResult == null || poiResult.getAllPoi() == null) {
            return;
        }
        if (poiResult.getAllPoi().size() > 0) {
            List<PoiInfo> info = poiResult.getAllPoi();
            infoss.addAll(info);
            adapter.addPoiInfo(infoss);
            adapter.notifyDataSetChanged();
            infoss.clear();
            list.setSelection(firstVisible);
        }
        if (receiveAll){
            refresh = true;
            receiveAll=false;
        }else {
            refresh=false;
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    Overlay overlay;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo info = (PoiInfo) adapter.getItem(position);
        if (info == null) {
            return;
        }
        name = info.name;
        address = info.address;
        latitude = info.location.latitude;
        longitude = info.location.longitude;
        LatLng point = new LatLng(latitude, longitude);
        OverlayOptions options = new MarkerOptions().draggable(false)
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.atom_ui_map_mark));
        if (overlay != null) {
            overlay.remove();
        }
        overlay = baiduMap.addOverlay(options);
        adapter.setCheckPosition(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}