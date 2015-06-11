package com.crashhandler;

import android.content.Context;
import android.util.Log;

import com.crashhandler.post.IPost;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 10:25
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{
    private static CrashHandler ourInstance = new CrashHandler();

    private Context mContext ;
    private IPost mPost ;

    public static CrashHandler getInstance() {
        return ourInstance;
    }

    private CrashHandler() {
    }

    public void init(Context context, IPost post){
        mContext = context ;
        mPost = post ;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.v("zgy", "===========Throwable===========" + ex.getMessage()) ;
        Log.v("zgy", "===========Throwable===========" + readErrorInfo(ex)) ;
        mPost.postData(readErrorInfo(ex));
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private String readErrorInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        pw.close();
        return writer.toString();
    }
}
