package com.crashhandler.post;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 11:32
 */
public class AppPost implements IPost {

    private Executor mExecutor ;

    public AppPost(final Handler handler) {
        mExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command) ;
            }
        } ;
    }

    @Override
    public void postData(String data) {
        mExecutor.execute(new ExecutorRunnable(data));
    }

    @Override
    public void postData(String data, Runnable postError) {
        mExecutor.execute(new ExecutorRunnable(data,postError));
    }

    @Override
    public void postData(String data, Runnable postError, Runnable postSuccess) {
        mExecutor.execute(new ExecutorRunnable(data,postError,postSuccess));
    }

    private static class ExecutorRunnable implements Runnable{
        private Runnable mErrorRunnable ;
        private Runnable mSuccessRunnable ;
        private String mData ;

        public ExecutorRunnable(String mData) {
            this(mData,null) ;
        }

        public ExecutorRunnable(String mData, Runnable mErrorRunnable) {
            this(mData,mErrorRunnable,null) ;
        }

        public ExecutorRunnable(String mData, Runnable mErrorRunnable, Runnable mSuccessRunnable) {
            this.mData = mData;
            this.mErrorRunnable = mErrorRunnable;
            this.mSuccessRunnable = mSuccessRunnable;
        }

        @Override
        public void run() {

        }
    }
}
