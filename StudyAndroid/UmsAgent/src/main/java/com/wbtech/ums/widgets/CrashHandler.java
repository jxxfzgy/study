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
package com.wbtech.ums.widgets;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import org.json.JSONObject;

import com.wbtech.ums.main.UmsAgent;
import com.wbtech.ums.utils.CommonUtils;
import com.wbtech.ums.pojo.MyMessage;
import com.wbtech.ums.utils.JsonUtils;
import com.wbtech.ums.utils.NetworkUitls;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

/**
 * 闪退处理类
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static CrashHandler errorController;
    private Context context;
    private Object stacktrace;

    private CrashHandler() {

    }

    public static synchronized CrashHandler getInstance() {
        if (errorController != null) {
            return errorController;
        } else {
            errorController = new CrashHandler();
            return errorController;
        }
    }

    public void init(Context context) {
        this.context = context;
    }

    public void uncaughtException(Thread thread, final Throwable arg1) {
        Log.d("ums-threadname", thread.getName());
        new Thread() {
            @Override
            public void run() {
                super.run();

                Looper.prepare();
                String errorinfo = readErrorInfo(arg1);

                String[] ss = errorinfo.split("\n\t");
                String headstring = ss[0] + "\n\t" + ss[1] + "\n\t" + ss[2] + "\n\t";
                String newErrorInfoString = headstring + errorinfo;
                stacktrace = newErrorInfoString;
                JSONObject errorInfo = JsonUtils.getErrorInfoJSONObj(stacktrace.toString(), context);
                CommonUtils.printLog("UmsAgent", errorInfo.toString(), Log.ERROR);

                if (1 == CommonUtils.getReportPolicyMode(context)
                        && CommonUtils.isNetworkAvailable(context)) {
                    if (!stacktrace.equals("")) {
                        MyMessage message = NetworkUitls.post(NetworkUitls.preUrl + NetworkUitls.errorUrl, errorInfo.toString());
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
                Looper.loop();
            }

        }.start();
    }

    private String readErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

}
