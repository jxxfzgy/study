package com.crashhandler.post;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 11:50
 */
public class PostManager {

    public IPost getPostHandler(PostEnum postEnum){
        IPost post ;
        switch (postEnum){
            case ANALYSIS:
                post = new AnalysisPost(new Handler(Looper.getMainLooper())) ;
                break;
            case APP:
                post = new AppPost(new Handler(Looper.getMainLooper())) ;
                break;
            default:
                post = new AnalysisPost(new Handler(Looper.getMainLooper())) ;
        }
        return post ;
    }
}
