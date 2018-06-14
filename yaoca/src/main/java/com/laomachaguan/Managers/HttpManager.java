package com.laomachaguan.Managers;

import android.content.Context;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.LogUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/8/27 13:50
 * 公司：成都因陀罗网络科技有限公司
 */

public class HttpManager {

    private Context context;

    public HttpManager(Context context) {
        this.context = context;
    }

    public void post(String url, HashMap<?, ?> map, final mHttpCallback httpCallback) {
        JSONObject js= JSONObject.parseObject(JSON.toJSONString(map));
        LogUtil.e("js:::"+js);
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkHttpUtils.post(url).tag(context)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return  httpCallback.parseResponse(response);
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        httpCallback.onSuccessed(o);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        httpCallback.onBefore();
                    }

                    @Override
                    public void onAfter(@Nullable Object o, @Nullable Exception e) {
                        super.onAfter(o, e);
                        httpCallback.onAfter();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        httpCallback.onFailed();
                    }
                });
    }


    public abstract class mHttpCallback<T> {
        public abstract void onSuccessed(T t);

        public void onFailed() {

        };

        public void onBefore() {

        }

        public void onAfter() {

        }

        public abstract T parseResponse(Response response);
    }




}
