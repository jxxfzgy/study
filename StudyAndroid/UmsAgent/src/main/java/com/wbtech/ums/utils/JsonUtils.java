
package com.wbtech.ums.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.wbtech.ums.main.UmsAgent;
import com.wbtech.ums.pojo.LatitudeAndLongitude;
import com.wbtech.ums.pojo.MyMessage;
import com.wbtech.ums.pojo.PostObjEvent;
import com.wbtech.ums.pojo.PostObjTag;
import com.wbtech.ums.pojo.SCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsonUtils {

    public static PostObjTag GenerateTagObj(Context context, String tags) {
        PostObjTag obj = new PostObjTag();
        obj.setTags(tags);
        obj.setProductkey(CommonUtils.getAppKey(context));
        obj.setDeviceid(CommonUtils.getDeviceID(context));
        return obj;
    }

    public static PostObjEvent GenerateEventObj(Context context, PostObjEvent event) {
        PostObjEvent event2 = new PostObjEvent(event);
        event2.setActivity(CommonUtils.getActivityName(context));
        event2.setAppkey(CommonUtils.getAppKey(context));
        event2.setTime(CommonUtils.getTime());
        event2.setVersion(CommonUtils.getVersion(context));
        return event2;
    }

    public static JSONObject getClientDataJSONObject(Context context) {
        TelephonyManager tm = (TelephonyManager) (context
                .getSystemService(Context.TELEPHONY_SERVICE));
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displaysMetrics);
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        JSONObject clientData = new JSONObject();
        try {
            clientData.put("os_version", CommonUtils.getOsVersion(context));
            clientData.put("platform", "android");
            clientData.put("language", Locale.getDefault().getLanguage());
            clientData.put("deviceid", tm.getDeviceId() == null ? "" : tm.getDeviceId());//
            clientData.put("appkey", CommonUtils.getAppKey(context));
            clientData.put("resolution", displaysMetrics.widthPixels + "x"
                    + displaysMetrics.heightPixels);
            clientData.put("ismobiledevice", true);
            clientData.put("phonetype", tm.getPhoneType());//
            clientData.put("imsi", tm.getSubscriberId());
            clientData.put("network", CommonUtils.getNetworkTypeWIFI2G3G(context));
            clientData.put("time", CommonUtils.getTime());
            clientData.put("version", CommonUtils.getVersion(context));
            clientData.put("userid", CommonUtils.getUserIdentifier(context));

            SCell sCell = CommonUtils.getCellInfo(context);

            clientData.put("mccmnc", sCell != null ? "" + sCell.MCCMNC : "");
            clientData.put("cellid", sCell != null ? sCell.CID + "" : "");
            clientData.put("lac", sCell != null ? sCell.LAC + "" : "");
            clientData.put("modulename", Build.PRODUCT);
            clientData.put("devicename", CommonUtils.getDeviceName());
            clientData.put("wifimac", wifiManager.getConnectionInfo().getMacAddress());
            clientData.put("havebt", adapter == null ? false : true);
            clientData.put("havewifi", CommonUtils.isWiFiActive(context));
            clientData.put("havegps", locationManager == null ? false : true);
            clientData.put("havegravity", CommonUtils.isHaveGravity(context));//

            LatitudeAndLongitude coordinates = CommonUtils.getLatitudeAndLongitude(context,
                    UmsAgent.getAutoLocation());
            clientData.put("latitude", coordinates.latitude);
            clientData.put("longitude", coordinates.longitude);
            CommonUtils.printLog("clientData---------->", clientData.toString(), Log.ERROR);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientData;
    }

    public static JSONObject getPauseJSONObj(Context context) {
        JSONObject info = new JSONObject();
        try {
            SharedPreferences ss = context.getSharedPreferences("UMS_sessionID", Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = context.getSharedPreferences("UMS_session_ID_savetime", Context.MODE_PRIVATE);
            String end_time = "";
            Long start = sharedPreferences.getLong("session_save_time", System.currentTimeMillis());
            Date date = new Date(start);
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            end_time = CommonUtils.getTime();
            Long end = System.currentTimeMillis();
            info.put("session_id", ss.getString("session_id", ""));
            info.put("start_millis", sdf.format(date));
            info.put("end_millis", end_time);
            info.put("duration", end - start + "");
            info.put("version", CommonUtils.getVersion(context));
            info.put("activities", CommonUtils.getActivityName(context));
            info.put("appkey", CommonUtils.getAppKey(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static JSONObject getErrorInfoJSONObj(String error, Context context) {
        String stacktrace = error;
        String activities = CommonUtils.getActivityName(context);
        String time = CommonUtils.getTime();
        String appkey = CommonUtils.getAppKey(context);
        String os_version = CommonUtils.getOsVersion(context);
        String deviceID = CommonUtils.getDeviceID(context);
        String version = CommonUtils.getVersion(context);
        JSONObject errorInfo = new JSONObject();
        try {
            Build bd = new Build();
            errorInfo.put("stacktrace", stacktrace);
            errorInfo.put("time", time);
            errorInfo.put("version", version);
            errorInfo.put("activity", activities);
            errorInfo.put("appkey", appkey);
            errorInfo.put("os_version", os_version);
            errorInfo.put("deviceid", CommonUtils.getDeviceName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorInfo;
    }

    public static JSONObject getPostTagsJSONObj(PostObjTag tagobj) {
        JSONObject object = new JSONObject();
        try {
            object.put("tags", tagobj == null ? "" : tagobj.getTags());
            object.put("deviceid", tagobj == null ? "" : tagobj.getDeviceid());
            object.put("productkey", tagobj == null ? "" : tagobj.getProductkey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    public static JSONObject getEventJOSNobj(PostObjEvent event) {
        JSONObject localJSONObject = new JSONObject();
        try {

            localJSONObject.put("time", event.getTime());
            localJSONObject.put("version", event.getVersion());
            localJSONObject.put("event_identifier", event.getEvent_id());
            localJSONObject.put("appkey", event.getAppkey());
            localJSONObject.put("activity", event.getActivity());
            if (event.getLabel() != null)
                localJSONObject.put("label", event.getLabel());
            localJSONObject.put("acc", event.getAcc());

        } catch (JSONException localJSONException) {
            CommonUtils.printLog("UmsAgent", "json error in emitCustomLogReport", Log.ERROR);
            localJSONException.printStackTrace();
        }
        return localJSONObject;
    }


}
