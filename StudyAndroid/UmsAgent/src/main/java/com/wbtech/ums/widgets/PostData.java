package com.wbtech.ums.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.wbtech.ums.main.UmsAgent;
import com.wbtech.ums.pojo.MyMessage;
import com.wbtech.ums.pojo.PostObjEvent;
import com.wbtech.ums.utils.CommonUtils;
import com.wbtech.ums.utils.JsonUtils;
import com.wbtech.ums.utils.NetworkUitls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by dtgrsty on 2014/6/13.
 */
public class PostData {
    private static final String TAG_URL = NetworkUitls.preUrl + NetworkUitls.tagUser;
    private static final String EVENT_URL = NetworkUitls.preUrl + NetworkUitls.eventUrl;
    private static final String ERROR_URL = NetworkUitls.preUrl + NetworkUitls.errorUrl;
    private static final String PAUSE_URL = NetworkUitls.preUrl + NetworkUitls.activityUrl;
    private static final String CLIENT_DATA_URL = NetworkUitls.preUrl + NetworkUitls.clientDataUrl;
    private static final String UPDATE_LOG_URL = NetworkUitls.preUrl + NetworkUitls.uploadUrl;

    public static void postAllLog(Context context) {

        File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/mobclick_agent_cached_" + context.getPackageName());
        if (file1.exists()) {
            try {
                FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/mobclick_agent_cached_" + context.getPackageName());
                StringBuffer sb = new StringBuffer();
                int i = 0;
                byte[] s = new byte[1024 * 4];
                while ((i = in.read(s)) != -1) {
                    sb.append(new String(s, 0, i));
                }
                if (CommonUtils.isNetworkAvailable(context)) {
                    MyMessage message = NetworkUitls.post(UPDATE_LOG_URL, sb + "");
                    if (message.isFlag()) {
                        File file = new File(Environment.getExternalStorageDirectory()
                                .getAbsolutePath()
                                + "/mobclick_agent_cached_"
                                + context.getPackageName());
                        file.delete();
                    } else {
                        CommonUtils.printLog("uploadError", "uploadLog Error", Log.ERROR);
                    }
                } else {
                    CommonUtils.printLog("NetworkError", "Network, not work", Log.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void postClientDatas(Context context) {
        if (UmsAgent.isFirst()) {
            Intent intent = new Intent();
            intent.setAction("cobub.razor.message");
            intent.putExtra("deviceid", CommonUtils.getDeviceID(context));
            context.sendBroadcast(intent);
            JSONObject clientData = JsonUtils.getClientDataJSONObject(context);

            if (1 == CommonUtils.getReportPolicyMode(context)
                    & CommonUtils.isNetworkAvailable(context)) {
                MyMessage message = NetworkUitls.post(CLIENT_DATA_URL, clientData.toString());
                if (!message.isFlag()) {
                    UmsAgent.saveInfoToFile("clientData", clientData, context);
                    CommonUtils.printLog("Errorinfo", message.getMsg(), Log.ERROR);
                }
            } else {
                UmsAgent.saveInfoToFile("clientData", clientData, context);
            }
            UmsAgent.setFirst(false);
        }
    }


    public static void postOnPauseInfo(Context context, JSONObject info) {

        CommonUtils.printLog("UmsAgent", info + "", Log.DEBUG);
        if (1 == CommonUtils.getReportPolicyMode(context) && CommonUtils.isNetworkAvailable(context)) {
            MyMessage message = NetworkUitls.post(PAUSE_URL, info.toString());
            if (!message.isFlag()) {
                UmsAgent.saveInfoToFile("activityInfo", info, context);
                CommonUtils.printLog("postOnPauseInfo error", message.getMsg(), Log.DEBUG);
            }
        } else {
            UmsAgent.saveInfoToFile("activityInfo", info, context);
        }
    }

    public static void postErrorInfo(Context context, String error) {
        try {
            JSONObject errorInfo = JsonUtils.getErrorInfoJSONObj(error, context);
            try {
                errorInfo.put("userid", CommonUtils.getUserIdentifier(context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CommonUtils.printLog("UmsAgent", errorInfo.toString(), Log.ERROR);

            if (1 == CommonUtils.getReportPolicyMode(context)
                    && CommonUtils.isNetworkAvailable(context)) {
                if (!error.equals("")) {
                    MyMessage message = NetworkUitls.post(ERROR_URL, errorInfo.toString());
                    CommonUtils.printLog("UmsAgent", message.getMsg(), Log.ERROR);
                    if (!message.isFlag()) {
                        UmsAgent.saveInfoToFile("errorInfo", errorInfo, context);
                        CommonUtils.printLog("error", message.getMsg(), Log.ERROR);
                    }
                }
            } else {
                UmsAgent.saveInfoToFile("errorInfo", errorInfo, context);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            CommonUtils.printLog("UMSAgent", "Exception occurred in postEventInfo()", Log.ERROR);
            e.printStackTrace();
        }
    }

    public static boolean postEventInfo(Context context, PostObjEvent event) {
        try {
            if (!event.verification()) {
                CommonUtils.printLog("UMSAgent", "Illegal value of acc in postEventInfo", Log.ERROR);
                return false;
            }

            JSONObject localJSONObject = JsonUtils.getEventJOSNobj(JsonUtils.GenerateEventObj(context, event));

            if (1 == CommonUtils.getReportPolicyMode(context) && CommonUtils.isNetworkAvailable(context)) {
                try {
                    MyMessage info = NetworkUitls.post(EVENT_URL, localJSONObject.toString());
                    if (!info.isFlag()) {
                        UmsAgent.saveInfoToFile("eventInfo", localJSONObject, context);
                        return false;
                    }
                } catch (Exception e) {
                    CommonUtils.printLog("UmsAgent", "fail to post eventContent", Log.ERROR);
                }
            } else {
                UmsAgent.saveInfoToFile("eventInfo", localJSONObject, context);
                return false;
            }
        } catch (Exception e) {
            CommonUtils.printLog("UMSAgent", "Exception occurred in postEventInfo()", Log.ERROR);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void postTag(Context context, String tags) {
        JSONObject object = JsonUtils.getPostTagsJSONObj(JsonUtils.GenerateTagObj(context, tags));

        if (1 == CommonUtils.getReportPolicyMode(context) && CommonUtils.isNetworkAvailable(context)) {

            MyMessage message = NetworkUitls.post(TAG_URL, object.toString());

            if (!message.isFlag()) {
                UmsAgent.saveInfoToFile("tags", object, context);
            }
        } else {
            UmsAgent.saveInfoToFile("tags", object, context);
        }
    }
}
