package com.crashhandler;

import android.content.Context;

import com.crashhandler.post.IPost;
import com.crashhandler.post.PostEnum;
import com.crashhandler.post.PostManager;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 10:49
 */
public class Analysis {

    private Context mContext ;

    public static PostEnum postEnum = PostEnum.ANALYSIS ;

    private IPost mPost ;

    public Analysis(Context mContext) {
        this.mContext = mContext;
        initAnalysis() ;
    }

    private void initAnalysis() {
        initPost() ;
        initCrashHandler() ;
    }

    private void initPost(){
        PostManager postManager = new PostManager() ;
        mPost = postManager.getPostHandler(postEnum) ;
    }

    private void initCrashHandler(){
        CrashHandler crashHandler = CrashHandler.getInstance() ;
        crashHandler.init(mContext,mPost);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }
}
