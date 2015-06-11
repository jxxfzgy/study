package com.crashhandler.post;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 11:25
 */
public interface IPost {

    void postData(String data) ;

    void postData(String data, Runnable postError) ;

    void postData(String data, Runnable postError, Runnable postSuccess) ;
}
