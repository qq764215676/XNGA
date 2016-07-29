package com.zbar.lib.decode;

import java.util.concurrent.CountDownLatch;

import ys.oa.fragment.ScanCodeFragment;

import android.os.Handler;
import android.os.Looper;


/**
 * 作者: 陈涛(1076559197@qq.com)
 * <p/>
 * 时间: 2014年5月9日 下午12:24:34
 * <p/>
 * 版本: V_1.0.0
 * <p/>
 * 描述: 解码线程
 */
final class DecodeThread extends Thread {
	ScanCodeFragment fragment = null;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;
    
    DecodeThread(ScanCodeFragment fragment) {
    	this.fragment = fragment;
    	handlerInitLatch = new CountDownLatch(1);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(fragment);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
