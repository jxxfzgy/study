/**
 * Cobub Razor
 *
 * An open source analytics android sdk for mobile applications
 *
 * @package Cobub Razor
 * @author WBTECH Dev Team
 * @copyright Copyright (c) 2011 - 2012, NanJing Western Bridge Co.,Ltd.
 * @license http://www.cobub.com/products/cobub-razor/license
 * @link http://www.cobub.com/products/cobub-razor/
 * @since Version 0.1
 * @filesource
 */

package com.wbtech.ums.main;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.wbtech.ums.utils.FileUtils;
import com.wbtech.ums.utils.CommonUtils;
import com.wbtech.ums.utils.JsonUtils;
import com.wbtech.ums.widgets.CrashHandler;
import com.wbtech.ums.widgets.PostData;
import com.wbtech.ums.widgets.UpdateManager;
import com.wbtech.ums.utils.MD5Utils;
import com.wbtech.ums.utils.NetworkUitls;
import com.wbtech.ums.pojo.LatitudeAndLongitude;
import com.wbtech.ums.pojo.MyMessage;
import com.wbtech.ums.pojo.PostObjEvent;
import com.wbtech.ums.pojo.SCell;

public class UmsAgent {
    private static boolean mUseLocationService = true;
    private static String start_millis = null;// The start time point
    private static String end_millis = null;// The end time point
    private static String session_id = null;
    private static String appkey = "";
    private static String curVersion = null;// app version
    private static UmsAgent umsAgentEntity = new UmsAgent();
    private static boolean mUpdateOnlyWifi = true;
    private static int defaultReportMode = 0;
    private static Handler handler;
    private static boolean isPostFile = true;
    private static boolean isFirst = true;

    private UmsAgent() {
        if (handler == null) {
            HandlerThread localHandlerThread = new HandlerThread("UmsAgent");
            localHandlerThread.start();
            this.handler = new Handler(localHandlerThread.getLooper());
        }
    }

    public static UmsAgent getUmsAgent() {
        return umsAgentEntity;
    }

    /**
     * 设置访问url（IP,PORT）
     *
     * @param url
     */
    public static void setBaseURL(String url) {
        NetworkUitls.preUrl = url;
    }

    public static boolean isFirst() {
        return isFirst;
    }

    public static void setFirst(boolean isFirst) {
        UmsAgent.isFirst = isFirst;
    }

    public static void setSessionContinueMillis(long interval) {
        if (interval > 0) {
            NetworkUitls.kContinueSessionMillis = interval;
        }
    }

    public static void setAutoLocation(boolean AutoLocation) {
        UmsAgent.mUseLocationService = AutoLocation;
    }

    public static boolean getAutoLocation() {
        return mUseLocationService;
    }


    /**
     * 绑定用户信息
     *
     * @param context
     * @param identifier
     * @return
     */
    public static String bindUserIdentifier(final Context context, final String identifier) {
        String packageName = context.getPackageName();
        SharedPreferences localSharedPreferences = context.getSharedPreferences("ums_agent_online_setting_" + packageName, 0);
        localSharedPreferences.edit().putString("identifier", identifier).commit();
        return localSharedPreferences.getString("identifier", "");
    }


    /**
     * 提交错误数据
     *
     * @param context
     */
    public static void onError(final Context context) {
        if (handler != null) {
            Runnable postErrorInfoRunnable = new Runnable() {
                @Override
                public void run() {
                    CrashHandler handler = CrashHandler.getInstance();
                    handler.init(context.getApplicationContext());
                    Thread.setDefaultUncaughtExceptionHandler(handler);
                }
            };
            handler.post(postErrorInfoRunnable);
        }
    }

    /**
     * 提交自定义的异常数据
     *
     * @param context 上下文
     * @param error   自定义标识
     */
    public static void onError(final Context context, final String error) {
        if (handler != null) {
            Runnable postErrorInfoRunnable = new Runnable() {
                @Override
                public void run() {
                    PostData.postErrorInfo(context, error);
                }
            };
            handler.post(postErrorInfoRunnable);
        }
    }

    /**
     * post event info with event_id
     *
     * @param context  上下文
     * @param event_id 事件ID
     */
    public static void onEvent(final Context context, final String event_id) {
        if (handler != null) {
            Runnable postEventInfo = new Runnable() {
                public void run() {
                    onEvent(context, event_id, 1);
                }
            };
            handler.post(postEventInfo);
        }
    }

    /**
     * override onEvent with more params
     *
     * @param context
     * @param event_id 事件ID
     * @param label    事件描述
     * @param acc      触发次数
     */
    public static void onEvent(final Context context, final String event_id, final String label,
                               final int acc) {
        if (handler != null) {
            Runnable postEventRunnable = new Runnable() {

                @Override
                public void run() {
                    PostData.postEventInfo(context, new PostObjEvent(event_id, label, acc + "", context));
                }
            };
            handler.post(postEventRunnable);
        }
    }

    /**
     * 上传事件点击信息
     *
     * @param context
     * @param event_id 事件编号
     * @param acc      触发次数
     */
    public static void onEvent(final Context context, final String event_id, final int acc) {
        if (handler != null) {
            Runnable postEventRunnable = new Runnable() {
                public void run() {
                    PostData.postEventInfo(context, new PostObjEvent(event_id, null, acc + "", context));
                }
            };
            handler.post(postEventRunnable);
        }
    }

    /**
     *
     * @param context
     */
    public static void onPause(final Context context) {
        if (handler != null) {
            Runnable postOnPauseinfoRunnable = new Runnable() {
                @Override
                public void run() {
                    PostData.postOnPauseInfo(context, JsonUtils.getPauseJSONObj(context));
                }
            };
            handler.post(postOnPauseinfoRunnable);
        }
    }


    public static void onResume(final Context context) {
        if (handler != null) {
            Runnable postOnResumeinfoRunnable = new Runnable() {

                @Override
                public void run() {
                    postonResume(context);
                }
            };
            handler.post(postOnResumeinfoRunnable);
        }
    }

    private static void postonResume(final Context context) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            setDefaultReportPolicy(context, 0);
        } else {
            if (UmsAgent.isPostFile) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        FileUtils.getInfoFromFile(context);
                    }
                });
                thread.run();
                UmsAgent.isPostFile = false;
            }
        }
        isCreateNewSessionID(context);
        try {
            if (session_id == null) {
                generateSeesion(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        start_millis = CommonUtils.getTime();
    }

    private static void isCreateNewSessionID(Context context) {
        // TODO Auto-generated method stub
        long currenttime = System.currentTimeMillis();

        SharedPreferences preferences = context.getSharedPreferences("UMS_session_ID_savetime",
                Context.MODE_PRIVATE);
        long session_save_time = preferences.getLong("session_save_time", currenttime);
        if (currenttime - session_save_time > NetworkUitls.kContinueSessionMillis) {
            try {
                generateSeesion(context);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Automatic Updates
     *
     * @param context
     */
    public static void update(final Context context) {
        if (handler != null) {
            Runnable isUpdateRunnable = new Runnable() {

                @Override
                public void run() {
                    isUpdate(context);
                }
            };
            handler.post(isUpdateRunnable);
        }
    }

    private static void isUpdate(Context context) {
        try {
            appkey = CommonUtils.getAppKey(context);
        } catch (Exception e) {
            String aString = end_millis.toString();
            Toast.makeText(context, aString, Toast.LENGTH_SHORT).show();
        }
        curVersion = CommonUtils.getCurVersion(context);
        JSONObject updateObject = new JSONObject();
        try {
            updateObject.put("appkey", appkey);
            updateObject.put("version_code", curVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CommonUtils.isNetworkAvailable(context) && CommonUtils.isNetworkTypeWifi(context)) {
            MyMessage message = NetworkUitls.post(NetworkUitls.preUrl + NetworkUitls.updataUrl,
                    updateObject.toString());
            if (message.isFlag()) {
                try {
                    JSONObject object = new JSONObject(message.getMsg());
                    String flag = object.getString("flag");
                    if (Integer.parseInt(flag) > 0) {
                        String fileurl = object.getString("fileurl");
                        String msg = object.getString("msg");
                        String forceupdate = object.getString("forceupdate");
                        String description = object.getString("description");
                        String time = object.getString("time");
                        String version = object.getString("version");
                        UpdateManager manager = new UpdateManager(context, version, forceupdate,
                                fileurl, description);
                        manager.showNoticeDialog(context);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                CommonUtils.printLog("error", message.getMsg(), Log.ERROR);

            }
        }
    }

    public static void updateOnlineConfig(final Context context) {
        if (handler != null) {
            Runnable updateOnlineConfigRunnable = new Runnable() {

                @Override
                public void run() {
                    updateOnlineConfigs(context);
                }
            };
            handler.post(updateOnlineConfigRunnable);
        }
    }

    /**
     * get KEY-VALUE
     *
     * @param context
     */
    private static void updateOnlineConfigs(Context context) {
        appkey = CommonUtils.getAppKey(context);
        JSONObject map = new JSONObject();
        try {
            map.put("appkey", appkey);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        String appkeyJSON = map.toString();
        SharedPreferences preferences = context.getSharedPreferences(
                "ums_agent_online_setting_"
                        + CommonUtils.getPackageName(context), 0
        );
        Editor editor = preferences.edit();

        if (CommonUtils.isNetworkAvailable(context)) {
            MyMessage message = NetworkUitls
                    .post(NetworkUitls.preUrl + NetworkUitls.onlineConfigUrl, appkeyJSON);
            try {
                CommonUtils.printLog("message", message.getMsg(), Log.ERROR);
                if (message.isFlag()) {
                    JSONObject object = new JSONObject(message.getMsg());

                    if (NetworkUitls.DebugMode) {
                        CommonUtils.printLog("uploadJSON", object.toString(), Log.ERROR);
                    }

                    Iterator<String> iterator = object.keys();

                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = object.getString(key);
                        editor.putString(key, value);
                        if (key.equals("autogetlocation") && (!value.equals("1"))) {
                            setAutoLocation(false);
                        }

                        if (key.equals("updateonlywifi") && (!value.equals("1"))) {
                            setUpdateOnlyWifi(false);
                        }
                        if (key.equals("reportpolicy") && (value.equals("1"))) {
                            setDefaultReportPolicy(context, 1);
                        }
                        if (key.equals("sessionmillis")) {
                            NetworkUitls.kContinueSessionMillis = Integer.parseInt(value) * 1000;
                        }
                    }
                    editor.commit();

                } else {
                    CommonUtils.printLog("error", message.getMsg(), Log.ERROR);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.printLog("UMSAgent", " updateOnlineConfig network error", Log.ERROR);

        }

    }

    /**
     * 获取onlineKey
     *
     * @param context
     * @param onlineKey
     * @return
     */
    public static String getConfigParams(Context context, String onlineKey) {

        appkey = CommonUtils.getAppKey(context);
        JSONObject json = new JSONObject();
        try {
            json.put("appkey", appkey);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        String appkeyJSON = json.toString();
        if (CommonUtils.isNetworkAvailable(context)) {
            MyMessage message = NetworkUitls.post(NetworkUitls.preUrl + NetworkUitls.onlineConfigUrl, appkeyJSON);
            if (message.isFlag()) {
                try {
                    JSONObject object = new JSONObject(message.getMsg());
                    return object.getString(onlineKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                CommonUtils.printLog("error", "getConfigParams error", Log.ERROR);
            }
        } else {
            CommonUtils.printLog("NetworkError", "Network, not work", Log.ERROR);
        }
        return "";
    }

    /**
     * 设置是否只在wifi环境下提示更新
     *
     * @param isUpdateonlyWifi
     */
    public static void setUpdateOnlyWifi(boolean isUpdateonlyWifi) {
        UmsAgent.mUpdateOnlyWifi = isUpdateonlyWifi;
        CommonUtils.printLog("mUpdateOnlyWifi value", UmsAgent.mUpdateOnlyWifi + "", Log.ERROR);
    }

    /**
     * 设置上传数据的模式（0代表启动时上传，1代表实时上传）
     *
     * @param context
     * @param reportModel
     */
    public static void setDefaultReportPolicy(Context context, int reportModel) {
        CommonUtils.printLog("reportType", reportModel + "", Log.DEBUG);
        if ((reportModel == 0) || (reportModel == 1)) {

            UmsAgent.defaultReportMode = reportModel;
            String packageName = context.getPackageName();
            SharedPreferences localSharedPreferences = context
                    .getSharedPreferences("ums_agent_online_setting_" + packageName, 0);
            synchronized (NetworkUitls.saveOnlineConfigMutex) {
                localSharedPreferences.edit().putInt("ums_local_report_policy",
                        reportModel).commit();
            }
        }
    }

    /**
     * 生成sessionID
     *
     * @param context
     * @return sessionId
     * @throws java.text.ParseException
     */
    private static String generateSeesion(Context context)
            throws ParseException {
        String sessionId = "";
        String str = CommonUtils.getAppKey(context);
        if (str != null) {
            String localDate = CommonUtils.getTime();
            str = str + localDate;
            sessionId = MD5Utils.md5Appkey(str);
            SharedPreferences preferences = context.getSharedPreferences("UMS_sessionID",
                    Context.MODE_PRIVATE);
            Editor edit = preferences.edit();
            edit.putString("session_id", sessionId);
            edit.commit();
            saveSessionTime(context);
            session_id = sessionId;
            return sessionId;
        }
        return sessionId;
    }

    /**
     * 保存session时间
     *
     * @param context
     */
    private static void saveSessionTime(Context context) {
        // TODO Auto-generated method stub
        SharedPreferences preferences2sessiontime = context.getSharedPreferences(
                "UMS_session_ID_savetime", Context.MODE_PRIVATE);
        Editor editor = preferences2sessiontime.edit();
        long currenttime = System.currentTimeMillis();
        editor.putLong("session_save_time", currenttime);
        editor.commit();
    }


    /**
     * 上传所有日志
     *
     * @param context
     */
    public static void uploadLog(final Context context) {
        Runnable uploadLogRunnable = new Runnable() {

            @Override
            public void run() {
                PostData.postAllLog(context);
            }
        };
        handler.post(uploadLogRunnable);
    }


    /**
     * 上传客户端数据
     *
     * @param context
     */
    public static void postClientData(final Context context) {
        if (handler != null) {
            Runnable postClientDataRunnable = new Runnable() {

                @Override
                public void run() {
                    PostData.postClientDatas(context);
                }
            };
            handler.post(postClientDataRunnable);
        }
    }

    public static void postTags(final Context context, final String tags) {
        if (handler != null) {
            Runnable tagUser = new Runnable() {

                @Override
                public void run() {
                    PostData.postTag(context, tags);
                }
            };
            handler.post(tagUser);
        }
    }

    /**
     * 保存log
     *
     * @param type
     * @param info
     * @param context
     */
    public static void saveInfoToFile(String type, JSONObject info, final Context context) {
        JSONArray newdata = new JSONArray();
        try {
            newdata.put(0, info);
            if (handler != null) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put(type, newdata);
                handler.post(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.saveInfoToFile(context, jsonObject);
                    }
                }));
            } else {
                CommonUtils.printLog(CommonUtils.getActivityName(context), "handler--null", Log.ERROR);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
