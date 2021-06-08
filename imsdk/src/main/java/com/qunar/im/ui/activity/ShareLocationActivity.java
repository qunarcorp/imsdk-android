package com.qunar.im.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.platform.comapi.location.CoordinateType;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.jsonbean.ShareLocationData;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.UserVCard;
import com.qunar.im.ui.presenter.IShareLocationPresenter;
import com.qunar.im.ui.presenter.impl.ShareLocationPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.IShareLocationView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 16-2-17.
 */
public class ShareLocationActivity extends IMBaseActivity implements BDLocationListener,IShareLocationView{

    public static final String SHARE_ID = "share_id";
    public static final String FROM_ID = "from_id";

    private static final String EXTRA_LATITUDE = "latitude";
    private static final  String EXTRA_LONGITUDE = "longitude";
    private static final String EXTRA_BITMAP = "bitmap";
    private static final String TAG = "ShareLocationActivity";

    //维护共享位置成员列表
    private List<String> members = new ArrayList<>();
    private Map<String,Overlay> overlayMap = new HashMap<>();
    private Map<String,Overlay> headMap = new HashMap<>();
    private Map<String,Bitmap> userGravatar = new WeakHashMap<>();
    private MapView mapView;
    private ImageView img_reset_location;
    private TextView hint;
    private LinearLayout topContainer;
    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private String shareId,fromId,myId;
    private boolean inited = false;
    private Projection projection;
    private final HandleLocation handleLocation = new HandleLocation();
    private final IShareLocationPresenter presenter = new ShareLocationPresenter();
    private final String HINT = "%d 人正在共享位置";

    private QtNewActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        shareId = getIntent().getStringExtra(SHARE_ID);
        fromId = getIntent().getStringExtra(FROM_ID);
        myId = QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid());
        presenter.setShareLocationView(this);
        setContentView(R.layout.atom_ui_activity_share_location);
        mapView = (MapView) findViewById(R.id.mapView);
        img_reset_location = (ImageView) findViewById(R.id.img_reset_location);
        hint = (TextView) findViewById(R.id.hint);
        topContainer = (LinearLayout) findViewById(R.id.location_top_container);
        actionBar = (QtNewActionBar) findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        initBaiduMap();
        img_reset_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomMap();
            }
        });
        addMember(myId);
    }


    private void zoomMap(){
        MyLocationData data = baiduMap.getLocationData();
        LatLng latLng = new LatLng(data.latitude, data.longitude);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.6f));
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
    }

    private void initBaiduMap() {
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(false);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setTrafficEnabled(false);
        baiduMap.getUiSettings().setZoomGesturesEnabled(true);
        baiduMap.getUiSettings().setCompassEnabled(false);
        for (int index = 0;index < mapView.getChildCount();index++){
            View v = mapView.getChildAt(index);
            if (v instanceof ZoomControls){
                mapView.removeView(v);
                break;
            }
        }
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                projection = baiduMap.getProjection();
            }
        });
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                synchronized (this) {
                    for (String key : headMap.keySet()) {
                        Overlay head = headMap.get(key);
                        if (head.getExtraInfo() != null && projection != null) {
                            double latitude = head.getExtraInfo().getDouble(EXTRA_LATITUDE);
                            double longitude = head.getExtraInfo().getDouble(EXTRA_LONGITUDE);
                            Bitmap bmp = head.getExtraInfo().getParcelable(EXTRA_BITMAP);
                            if (projection != null && bmp != null) {
                                //从地图上移除原来的Overlay
                                head.remove();
                                headMap.remove(head);
                                LatLng origin = new LatLng(latitude, longitude);
                                Point point = projection.toScreenLocation(origin);
                                point.offset(0, -30);
                                LatLng latLng = projection.fromScreenLocation(point);
                                OverlayOptions dest = new MarkerOptions().draggable(false)
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                Overlay headOverlay = baiduMap.addOverlay(dest);
                                Bundle extra = new Bundle();
                                //放入的坐标始终是精准的原始坐标
                                extra.putDouble(EXTRA_LATITUDE, latitude);
                                extra.putDouble(EXTRA_LONGITUDE, longitude);
                                extra.putParcelable(EXTRA_BITMAP, bmp);
                                headOverlay.setExtraInfo(extra);
                                headMap.put(key, headOverlay);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(handleLocation);
        presenter.joinShareLocation();
        initLocationClient();
//      yActionBar.getTitleTextview().setText("共享位置");
        setActionBarTitle(R.string.atom_ui_title_share_location);
        mapView.onResume();
    }

    private void initLocationClient() {
        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType(CoordinateType.BD09LL);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setNeedDeviceDirect(true);
        option.setIsNeedLocationDescribe(false);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(true);
        option.SetIgnoreCacheException(true);
        option.setEnableSimulateGps(true);
        //5s发送一次自己的位置
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(this);
        locationClient.start();
    }

    @Override
    protected void onPause() {
        presenter.quitShareLocation();
        EventBus.getDefault().unregister(handleLocation);
        locationClient.unRegisterLocationListener(this);
        locationClient.stop();
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        MyLocationData data;
        MyLocationData.Builder builder = new MyLocationData.Builder();
        if (baiduMap != null) {
            data = builder.direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .direction(bdLocation.getDirection())
                    .build();
            baiduMap.setMyLocationData(data);
            addOverLay(myId,new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()),bdLocation.getDirection());

            ShareLocationData locationData = new ShareLocationData();
            locationData.direction = Double.toString( bdLocation.getDirection());
            locationData.latitude = Double.toString(bdLocation.getLatitude());
            locationData.longitude = Double.toString(bdLocation.getLongitude());
            if (!inited){
                //第一次收到消息自动缩放地图
                inited = true;
                zoomMap();
            }
            presenter.sendLocationData(locationData);
        }
    }

    @Override
    public List<String> getMembers() {
        return members;
    }

    @Override
    public String getShareId() {
        return shareId;
    }

    @Override
    public String getFromId() {
        return fromId;
    }

    private synchronized void addOverLay(final String id, final LatLng point, float rotate) {
        if (overlayMap.get(id) != null) {
            overlayMap.get(id).remove();
        }
        if (headMap.get(id) != null) {
            headMap.get(id).remove();
        }

        //添加头像Overlay
        LatLng latLng = new LatLng(point.latitude,point.longitude);
        if (projection != null) {
            Point screenPoint = projection.toScreenLocation(point);
            screenPoint.offset(0, -30);
            latLng = projection.fromScreenLocation(screenPoint);
        }
        Bitmap bmp = getBitmapWithId(id);
        if (bmp != null) {
            OverlayOptions head = new MarkerOptions().draggable(false)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp));
            Overlay headOverlay = baiduMap.addOverlay(head);
            Bundle extra = new Bundle();
            extra.putDouble(EXTRA_LATITUDE, point.latitude);
            extra.putDouble(EXTRA_LONGITUDE, point.longitude);
            extra.putParcelable(EXTRA_BITMAP, bmp);
            headOverlay.setExtraInfo(extra);
            headMap.put(id, headOverlay);
        }
        //添加图标Overlay
        OverlayOptions options = new MarkerOptions().draggable(false)
                .position(point)
                .rotate(rotate)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.atom_ui_loc));
        Overlay overlay = baiduMap.addOverlay(options);
        overlayMap.put(id, overlay);
//
//        ProfileUtils.loadNickName(id, false, new ProfileUtils.LoadNickNameCallback() {
//            @Override
//            public void finish(String name) {
//                OverlayOptions head = new TextOptions()
//                        .position(point)
//                        .text(name)
//                        .fontSize(50);
//                Overlay headOverlay = baiduMap.addOverlay(head);
//                headMap.put(id, headOverlay);
//            }
//        });
    }

    private SimpleDraweeView createDraweeView() {
        SimpleDraweeView view = new SimpleDraweeView(this);
        RoundingParams params = RoundingParams.asCircle();
        view.setLayoutParams(new ViewGroup.LayoutParams(Utils.dpToPx(this, 48), Utils.dpToPx(this, 48)));
        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setPlaceholderImage(getResources().getDrawable(R.drawable.atom_ui_default_gravatar), ScalingUtils.ScaleType.CENTER_CROP)
                .setRoundingParams(params)
                .build();
        view.setHierarchy(hierarchy);
        return view;
    }

    private void addMember(String id) {
        if (!members.contains(id)) {
            //当前列表没有该用户
            members.add(id);
            hint.setText(String.format(HINT, members.size()));
            SimpleDraweeView view = createDraweeView();
            ProfileUtils.displayGravatarByUserId(id, view);
            view.setTag(id);
            topContainer.addView(view);
        }
    }

    private void delMember(String id) {
        if (members.contains(id)) {
            //当前列表没有该用户
            if (overlayMap.get(id) != null) {
                overlayMap.get(id).remove();
            }
            if (headMap.get(id) != null) {
                headMap.get(id).remove();
            }
            members.remove(id);
            for (int i = 0;i < topContainer.getChildCount();i++){
                View view = topContainer.getChildAt(i);
                if (view.getTag().equals(id)){
                    topContainer.removeView(view);
                    break;
                }
            }
            hint.setText(String.format(HINT, members.size()));
        }
    }

    final class HandleLocation {
        public void onEventMainThread(EventBusEvent.ShareLocationMessage msg) {
            final IMMessage message = msg.getMessage();
            LogUtil.d("ShareLocation", message.getBody());
            String id = QtalkStringUtils.parseBareJid(message.getFromID());
            if (message.getType() != ConversitionType.MSG_TYPE_SHARE_LOCATION) {
                return;
            }
        }
    }

    Bitmap getBitmapWithId(String id) {
        if (userGravatar.containsKey(id) && userGravatar.get(id) != null){
            return userGravatar.get(id);
        }
        UserVCard vCard = ProfileUtils.getLocalVCard(id);
        if (!TextUtils.isEmpty(vCard.gravantarUrl)) {
            String filePath = MyDiskCache.getSmallFile(QtalkStringUtils.getGravatar(vCard.gravantarUrl,
                    true)).getAbsolutePath();
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            if(bmp!=null) {
                int width = bmp.getWidth();
                Bitmap resultBmp = Bitmap.createBitmap(width, width,
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(resultBmp);
                Paint paint = new Paint();
                canvas.drawCircle(width / 2.0f, width / 2.0f, width / 2.0f, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bmp, 0, 0, paint);
                bmp.recycle();
                userGravatar.put(id, resultBmp);
                return resultBmp;
            }
        }
        return null;
    }
}