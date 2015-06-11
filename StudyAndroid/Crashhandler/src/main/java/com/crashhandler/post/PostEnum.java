package com.crashhandler.post;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 11:52
 */
public enum PostEnum {

    ANALYSIS(0),

    APP(1);

    private int value = 0 ;

    private PostEnum(int value){
        this.value = value ;
    }
}
