package com.wbtech.ums.utils;

import android.content.Context;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;

import com.wbtech.ums.pojo.MyMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import android.os.Handler;

/**
 * Created by Alysa on 2014/6/13.
 */
public class FileUtils {
    public static void getInfoFromFile(Context context) {
        File file1;
        FileInputStream in;
        try {
            file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobclick_agent_cached_" + context.getPackageName());
            if (!file1.exists()) {
                return;
            }
            in = new FileInputStream(file1);
            StringBuffer sb = new StringBuffer();

            int i = 0;
            byte[] s = new byte[1024 * 4];

            while ((i = in.read(s)) != -1) {

                sb.append(new String(s, 0, i));
            }

            MyMessage message = NetworkUitls.post(NetworkUitls.preUrl + NetworkUitls.uploadUrl, sb.toString());
            if (message.isFlag()) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobclick_agent_cached_" + context.getPackageName());
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveInfoToFile(Context context, JSONObject object) {
        JSONObject existJSON = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && CommonUtils.checkPermissions(context, "android.permission.WRITE_EXTERNAL_STORAGE")) {

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobclick_agent_cached_" + context.getPackageName());
                if (file.exists()) {
                    CommonUtils.printLog("path", file.getAbsolutePath(), Log.DEBUG);
                } else {
                    file.createNewFile();
                    CommonUtils.printLog("path", " createNewFile No path", Log.DEBUG);
                }

                FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobclick_agent_cached_" + context.getPackageName());
                StringBuffer sb = new StringBuffer();

                int i = 0;
                byte[] s = new byte[1024 * 4];

                while ((i = in.read(s)) != -1) {

                    sb.append(new String(s, 0, i));
                }
                if (sb.length() != 0) {
                    existJSON = new JSONObject(sb.toString());

                    Iterator iterator = object.keys();

                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONArray newData = object.getJSONArray(key);

                        if (existJSON.has(key)) {
                            JSONArray newDataArray = existJSON.getJSONArray(key);
                            CommonUtils.printLog("SaveInfo", newData + "", Log.DEBUG);
                            newDataArray.put(newData.get(0));
                        } else {
                            existJSON.put(key, object.getJSONArray(key));
                            CommonUtils.printLog("SaveInfo", "jsonobject" + existJSON, Log.DEBUG);
                        }

                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/mobclick_agent_cached_" + context.getPackageName(), false);
                    fileOutputStream.write(existJSON.toString().getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } else {
                    Iterator iterator = object.keys();
                    JSONObject jsonObject = new JSONObject();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONArray array = object.getJSONArray(key);

                        jsonObject.put(key, array);

                    }
                    jsonObject.put("appkey", CommonUtils.getAppKey(context));

                    FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/mobclick_agent_cached_" + context.getPackageName(), false);
                    fileOutputStream.write(jsonObject.toString().getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
