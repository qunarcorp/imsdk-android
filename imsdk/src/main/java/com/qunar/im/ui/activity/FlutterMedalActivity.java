//package com.qunar.im.ui.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.orhanobut.logger.Logger;
//import com.qunar.im.base.jsonbean.LogInfo;
//import com.qunar.im.base.module.MedalListResponse;
//import com.qunar.im.base.module.MedalSingleUserStatusResponse;
//import com.qunar.im.base.module.MedalUserStatusResponse;
//import com.qunar.im.base.module.Nick;
//import com.qunar.im.base.module.UserHaveMedalStatus;
//import com.qunar.im.base.protocol.ProtocolCallback;
//import com.qunar.im.base.util.HttpUtils;
//import com.qunar.im.base.util.JsonUtils;
//import com.qunar.im.core.manager.IMDatabaseManager;
//import com.qunar.im.core.manager.IMLogicManager;
//import com.qunar.im.core.services.QtalkNavicationService;
//import com.qunar.im.log.LogConstans;
//import com.qunar.im.log.LogService;
//import com.qunar.im.log.QLog;
//import com.qunar.im.protobuf.common.CurrentPreference;
//import com.qunar.im.ui.R;
//import com.qunar.im.utils.ConnectionUtil;
//import com.qunar.im.utils.HttpUtil;
//import com.qunar.im.utils.QtalkStringUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.flutter.app.FlutterActivity;
//import io.flutter.plugin.common.MethodCall;
//import io.flutter.plugin.common.MethodChannel;
//import io.flutter.plugins.GeneratedPluginRegistrant;
//import io.flutter.view.FlutterMain;
//
////import io.flutter.app.FlutterActivity;
////import io.flutter.facade.Flutter;
////import io.flutter.plugin.common.MethodCall;
////import io.flutter.plugin.common.MethodChannel;
////import io.flutter.view.FlutterView;
//
////import io.flutter.facade.Flutter;
//
//public class FlutterMedalActivity extends FlutterActivity {
//
//
//
//
//    //channel的名称，由于app中可能会有多个channel，这个名称需要在app内是唯一的。
//    private static final String MEDALCHANNEL = "data.flutter.io/medal";
////    private FlutterView flutterView;
//    public static final String USERID = "userId";
//
//    private static final String GETMEDALLIST = "getMedalList";
//    private static final String UPDATEUSERMEDALSTATUS = "updateUserMedalStatus";
//    private static final String GETUSERSINMEDAL = "getUsersInMedal";
//    private static final String NATIVELOCALLOG = "nativeLocalLog";
//    private static final String GETNICKINFO="getNickInfo";
//
//    private static final String ROUTE_PAGE = "route";
//
//
//    public static Intent makeIntent(Context context, String routePage) {
//        if (routePage == null || routePage.equals("")) {
//            routePage = "/";
//        }
//        String userid =routePage;
//        Nick nick = ConnectionUtil.getInstance().getNickById(userid);
//        Map<String,String> map = new HashMap<>();
//
//        map.put("selfUserId", CurrentPreference.getInstance().getPreferenceUserId());
//        map.put("targetUserId",userid);
//        Intent intent = new Intent(context, FlutterMedalActivity.class);
//        intent.setAction(Intent.ACTION_RUN);
//        intent.putExtra(ROUTE_PAGE, JsonUtils.getGson().toJson(map));
//        return intent;
//    }
//
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_ui_flutter);
//        FlutterMain.startInitialization(this);
//        super.onCreate(savedInstanceState);
//        GeneratedPluginRegistrant.registerWith(this);
//
//        getIntent().putExtra("enable-software-rendering", true);
//
////        String userid = getIntent().getStringExtra(USERID);
////        Nick nick = ConnectionUtil.getInstance().getNickById(userid);
////        Map<String,String> map = new HashMap<>();
////
////        map.put("selfUserId", CurrentPreference.getInstance().getPreferenceUserId());
////        map.put("targetUserId",userid);
////        Logger.i("flutter0:");
//////        String json = getIntent().getStringExtra(JsonUtils.getGson().toJson(nick));
//////        flutterView = Flutter.createView(FlutterMedalActivity.this, getLifecycle(), JsonUtils.getGson().toJson(nick));
//////        flutterView = Flutter.createView(FlutterMedalActivity.this, getLifecycle(), userid);
////        flutterView = Flutter.createView(FlutterMedalActivity.this, getLifecycle(), JsonUtils.getGson().toJson(map));
////        Logger.i("flutter1:");
////        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
////
////        Logger.i("flutter2:");
////        getWindow().getDecorView().post(new Runnable() {
////            @Override
////            public void run() {
////                Logger.i("flutter3:");
////                frameLayout.addView(flutterView);
////            }
////        });
////        Logger.i("flutter4:");
////        addContentView(flutterView, params);
//
//
//        initMethod();
////        Logger.i("flutter5:");
//    }
//
//    private void initMethod() {
//        new MethodChannel(getFlutterView(), MEDALCHANNEL).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
//            @Override
//            public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
//                switch (methodCall.method) {
//                    case GETMEDALLIST:
//                        getMedalList(methodCall, result);
//                        break;
//                    case UPDATEUSERMEDALSTATUS:
//                        updateMedalStatus(methodCall, result);
//                        break;
//                    case GETUSERSINMEDAL:
//                        getUserInMedal(methodCall, result);
//                        break;
//                    case NATIVELOCALLOG:
//                        nativeLocalLog(methodCall, result);
//                        break;
//                    case GETNICKINFO:
//                        getNickInfo(methodCall,result);
//                        break;
//                    default:
//                        result.notImplemented();
//                        break;
//
//                }
//            }
//        });
//    }
//
//    private void getNickInfo(MethodCall methodCall, MethodChannel.Result result) {
//        String userid = methodCall.argument("userId");
//        Nick nick = ConnectionUtil.getInstance().getNickById(userid);
//        result.success(JsonUtils.getGson().toJson(nick));
//
//    }
//
//    private void nativeLocalLog(MethodCall methodCall, MethodChannel.Result result) {
//        String logCode = methodCall.argument("logCode");
//        String logDes = methodCall.argument("logDes");
//        LogInfo logInfo = QLog.build(LogConstans.LogType.ACT, LogConstans.LogSubType.CLICK).eventId(logCode).describtion(logDes);
//        LogService.getInstance().saveLog(logInfo);
//
//    }
//
//    private void getUserInMedal(MethodCall methodCall, MethodChannel.Result result) {
//        int medalId = methodCall.argument("medalId");
//        int limit = methodCall.argument("limit");
//        int offset = methodCall.argument("offset");
//
//        List<Nick> nicks = IMDatabaseManager.getInstance().getUsersInMedal(medalId, limit, offset);
//        if (nicks.size() > 0) {
//            result.success(JsonUtils.getGson().toJson(nicks));
//        }
//
//        //获取这个勋章下的所有用户
////        NSDictionary *callArguments = (NSDictionary *)call.arguments;
////        NSInteger medalId = [[callArguments objectForKey:@"medalId"] integerValue];
////        NSInteger limit = [[callArguments objectForKey:@"limit"] integerValue];
////        NSInteger offset = [[callArguments objectForKey:@"offset"] integerValue];
////        NSArray *userInfoList = [[QIMKit sharedInstance] getUsersInMedal:medalId withLimit:limit withOffset:offset];
////        NSString *jsonStr2 = [[QIMJSONSerializer sharedInstance] serializeObject:userInfoList];
////        result(jsonStr2);
//    }
//
//    private void updateMedalStatus(MethodCall methodCall, MethodChannel.Result result) {
//        int medalId = methodCall.argument("medalId");
//        int status = methodCall.argument("status");
//
//        //设置勋章状态
//        HttpUtil.userMedalStatusModifyWithStatus(status, medalId, new ProtocolCallback.UnitCallback<MedalSingleUserStatusResponse>() {
//            @Override
//            public void onCompleted(MedalSingleUserStatusResponse medalUserStatusResponse) {
////                                new String();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Map<String, Object> response = new HashMap<>();
//                        if (medalUserStatusResponse != null) {
//
//                            response.put("isOK", true);
//                            UserHaveMedalStatus userHaveMedalStatus = IMDatabaseManager.getInstance().getUserMedalWithMedalId(medalUserStatusResponse.getData().getMedalId(), medalUserStatusResponse.getData().getUserId(), medalUserStatusResponse.getData().getHost());
//                            response.put("medal", userHaveMedalStatus);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if ((userHaveMedalStatus.getMedalUserStatus() & 0x02) == 0x02) {
//                                        Toast.makeText(FlutterMedalActivity.this,getString(R.string.atom_ui_change_medal_wear),Toast.LENGTH_LONG).show();
//
//                                    } else {
//                                        Toast.makeText(FlutterMedalActivity.this,getString(R.string.atom_ui_change_medal_upload),Toast.LENGTH_LONG).show();
//
//                                    }
//                                }
//                                });
////                            Toast.makeText(,"勋章佩戴成功",Toast.LENGTH_LONG).show();
//
//
//
//
//                        } else {
//                            response.put("isOK", false);
////                            runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(FlutterMedalActivity.this,getString(R.string.atom_ui_change_medal_upload),Toast.LENGTH_LONG).show();
////                                }
////                            });
//
//
//                        }
//                        result.success(JsonUtils.getGson().toJson(response));
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });
//    }
//
//    public void getMedalList(MethodCall methodCall, MethodChannel.Result result) {
//        List<UserHaveMedalStatus> list = ConnectionUtil.getInstance().selectUserHaveMedalStatus(QtalkStringUtils.parseId(methodCall.argument("userId")), QtalkNavicationService.getInstance().getXmppdomain());
//        if (list != null && list.size() > 0) {
//            result.success(JsonUtils.getGson().toJson(list));
//        } else {
//            result.error("error", "出现未知错误", null);
//        }
////        String json = JsonUtils.getGson().toJson();
//
//
//    }
//
////    @Override
////    public void onBackPressed() {
////        if (this.flutterView != null) {
////            this.flutterView.popRoute();
//////            this.flutterView.popRoute();
////
////        } else {
////            super.onBackPressed();
////        }
////    }
//}
