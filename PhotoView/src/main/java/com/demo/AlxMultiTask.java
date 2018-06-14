package com.demo;

/**
 * 作者：因陀罗网 on 2017/6/20 14:25
 * 公司：成都因陀罗网络科技有限公司
 */
/**
 *
 * 用于替换系统自带的AsynTask，使用自己的多线程池，执行一些比较复杂的工作，比如select photos，这里用的是缓存线程池，也可以用和cpu数相等的定长线程池以提高性能
 */

import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class AlxMultiTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static ExecutorService photosThreadPool;//用于加载大图的线程池
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private final int CORE_POOL_SIZE = CPU_COUNT + 1;
    public void executeDependSDK(Params...params){
        if(photosThreadPool==null)photosThreadPool = Executors.newFixedThreadPool(CORE_POOL_SIZE);
        if(Build.VERSION.SDK_INT<11) super.execute(params);
        else super.executeOnExecutor(photosThreadPool,params);
    }


}