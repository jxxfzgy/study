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
package com.wbtech.ums.utils;

import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.wbtech.ums.pojo.MyMessage;

public class NetworkUitls {

    public static boolean DebugMode = false;
    public static long kContinueSessionMillis = 30000L;
    public static final Object saveOnlineConfigMutex = new Object();
    public static final String eventUrl = "/ums/postEvent";
    public static final String errorUrl = "/ums/postErrorLog";
    public static final String clientDataUrl = "/ums/postClientData";
    public static final String updataUrl = "/ums/getApplicationUpdate";
    public static final String activityUrl = "/ums/postActivityLog";
    public static final String onlineConfigUrl = "/ums/getOnlineConfiguration";
    public static final String uploadUrl = "/ums/uploadLog";
    public static final String tagUser = "/ums/postTag";
    public static String preUrl = "";
    public static long paramleng = 256L;
    public static String DEFAULT_CHARSET = HTTP.UTF_8;

    public static MyMessage post(String url, String data) {
        // TODO Auto-generated method stub
        CommonUtils.printLog("ums", url, Log.DEBUG);
        String returnContent = "";
        MyMessage message = new MyMessage();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            StringEntity se = new StringEntity("content=" + data, DEFAULT_CHARSET);
            CommonUtils.printLog("postdata", "content=" + data, Log.DEBUG);
            se.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            int status = response.getStatusLine().getStatusCode();
            CommonUtils.printLog("ums", status + "", Log.DEBUG);
            String returnXML = EntityUtils.toString(response.getEntity());
            returnContent = URLDecoder.decode(returnXML);
            switch (status) {
                case 200:
                    message.setFlag(true);
                    message.setMsg(returnContent);
                    break;

                default:
                    Log.e("error", status + returnContent);
                    message.setFlag(false);
                    message.setMsg(returnContent);
                    break;
            }
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("err", e.toString());
                returnContent = jsonObject.toString();
                message.setFlag(false);
                message.setMsg(returnContent);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }
        CommonUtils.printLog("UMSAGENT", message.getMsg(), Log.DEBUG);
        return message;
    }
}
