package com.laomachaguan.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.mApplication;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.laomachaguan.Model_Order.Order_Tab.carIdlist;

/**
 * Created by Administrator on 2016/7/15.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler,View.OnClickListener{
    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;
    private TextView msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_result);
        Log.w(TAG, "-=-=进入WXPayEntryActivity:-==-= ");
        api = WXAPIFactory.createWXAPI(this, Constants.WXPay_APPID);
        api.registerApp(Constants.WXPay_APPID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.e("微信支付请求");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (msg == null) {
            msg = (TextView) findViewById(R.id.wx_result);
            ((ImageView) findViewById(R.id.wx_imageview)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.logo_weixin));
        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                msg.setText("支付成功");
                if (mApplication.good_id_list!= null) {
                    if (carIdlist != null) {
                        for(String id:mApplication.good_id_list){
                            if (carIdlist.contains(id)) {
                                carIdlist.remove(id);
                                LogUtil.e("移除购物车商品Id！！！！！！！" + id+"   购物车ID；：："+carIdlist);
                            }
                        }
                    }
                }
                Intent intent=new Intent("car");
                sendBroadcast(intent);
                Intent intent1 = new Intent("DingDan");
                sendBroadcast(intent1);
                Intent intent2 = new Intent("yaoyue");
                sendBroadcast(intent2);
            } else if (resp.errCode== -2) {
                msg.setText("您已取消本次支付");
                mApplication.good_id_list.clear();
            } else {
                msg.setText("订单获取失败");
                mApplication.good_id_list.clear();
            }
            LogUtil.e("微信支付回调参数：："+resp.errCode+"   "+resp.errStr);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wx_commit:
                finish();
                break;
        }
    }
}
