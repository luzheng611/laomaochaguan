package com.laomachaguan.Utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.laomachaguan.Model_Order.Order_Tab.carIdlist;

/**
 * Created by Administrator on 2017/1/4.
 */
public class AliPayUtil {
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2017051507247043";

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID = "2088422387874890";

    public static final String RSA2_PRIVATE ="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCsau5wVJLpK/H/pObrSxQR6VAQE8qapSLXrnaCb8HZ6QOkHSeWa/CF/xLtY2IqZimmGbnLRIR9I+iwfnm/vLPit2zGCmb9SZxN2uJ/mWwEhlH8dXiA3Hg5ayHovEFJMluDmwn5w+OmheNTLlxHV0xU9VZtm+/TmKxaIGax3eWBKi6xjSxngYNLhV5aHTwB+mvz4r477FXrgdyOspStWeGBzjeTvfjS9/ydbNaaDn6hSNtGbqXQbrGTl4f2USx6cIYHonr1Xl3t5iMJ8NJTimCYdLh8EkvrGWmcI4hiPXCTW6pUVaOZY6bjhMwu59PvwFNBMaZiJcD2JW1+zn9XL/2nAgMBAAECggEAMimOI3pHn7UcXA8i69PVC/0AQR0w9dsTjwKAEiTnljl3yLh+uwG+YY27ePtQJRvCBEQ5zMyraykWWrsBOupwv4Y0DE4cdpaC9he3i/4b8hkOI1Ad9geupq3nmKmkqOIQw/JIbbUzN1B+ucWkLVFVsOYL9inHFZ22jyYcGqV/Txt3UPRhkvG7jacPK8c7usLAwSTw3WkIwJqL+50OF1Xj6SHXKahJMdIJppljFjD56FiXhmlhfR41vx2O9WoZw8AQTuzC5ghK405rO3nAZThxpBxjjW41cx3+jBvxBcnwMHYEhJjvVGaSAxqfbKOgy6mNe1bb4X2QQvaaEUSUNJEgKQKBgQDab6R/aexS1DAQI6UOYeQEW2GDy5gUzYWp3cGiYRl51I/ZPycZgXEaawrN5FM38bMXKiSyRyceliqdB9UuDYn2WYzqCHo5KHoKi0txUtrAfvQ+NlknkMbxwU0WEW3XhzokqCS1P2A5nmibYbbk6jCZ0t6odFNFgGYlIOeedOu5kwKBgQDKEWPh1Q7WtLXRADwrSguJLSbh37qTBczgLyX1JwVFp3Jgzr/kTFdZF4eeWiLfVso5eFdTRdRnpTXXhHgceb0PRUptkCqUS7VXsaOzzaQpQXHf0XROiLRFJ9THqDeIC1YE+RB0J2yd3T+1qkFO13NhIJujjTa2lRzq5PZP+K8oHQKBgEDSEIae8DpELV8ctMZn1jpUw8NQj3jc5GVcITIGjoDQyz80tm2jM+UbDsG6l9dqKjMdlnYxFzLEbTDcOi7IZcBtIpvwmBSOJmkqnOCsLa4h/mjXx+0t0Vu8eRurYGUtA9wM6Ze/jtaKY5B4lsWnmXSMVva2Tl+5kpZCBXnISItxAoGADGnJEwR9pt7vFGnJSHeCAelOLC4MTh/bGCKNWGQPRNomgp/w1duCuixh3m53GfVA8wrbgngVtlBF4Hl4z3cDED8XdWeX1ghYmYyGG/OUFBy7HB7H4FMxj9YEkkdZMfhQTnnmctxBJemDkmtlG6zMn7jbFs8D/29lmWB7aSpDyFkCgYB/FYqfTbDtIqNpXDafGyfW1+cy/oVziq5rCSiFEvyn5gF+wEzbZJFGM3egNIcIdpGrip7EiSzfVL6HoA7IJlOwhxxEVNXTHEc4D0Xwfpr7oinH7Qpz/EJa+FLGYs6MYnqOdlwcFER5jzPUEJ0M0ZB1f52mZtf1MQsOg99TUDvBEw==";
    public static final String RSA_PRIVATE ="";
    private static final int SDK_PAY_FLAG = 1;


    /**
     * 支付宝支付业务
     */
    public static void openAliPay(final Activity context, final String allmoney, final String attachId, final String title, final String num, final String type, final String number) {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        if (!Network.HttpTest(context)) {
            Toast.makeText(mApplication.getInstance(), "网络连接不稳定，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressUtil.show(context, "", "正在调起支付宝,请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String stu_id = "";
                String money = "";
                String url;
                JSONObject js=new JSONObject();

                try {
                    if(type.equals("7")){
                        url=Constants.Goods_pay;
                        js.put("shop_id","0");
                        js.put("msg",attachId);
                        js.put("address",num);
                    }else{
                        if(type.equals("11")){
                            url=Constants.getPingTuan_Pay_ip;
                            js.put("number",number.equals("0")?System.currentTimeMillis()/1000+RandomUtil.generateNumber(5):number);
                            js.put("shop_id",attachId);
                            js.put("address",num);
                        }else{
                            url=Constants.getAttachId_ip;
                            js.put("shop_id",attachId);
                        }
                    }
                    js.put("money", allmoney);
                    js.put("title", title);
                    js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                    js.put("receiveid", Constants.M_id);
                    js.put("num",num);
                    js.put("type", type);
                    js.put("pay_type", "2");
                    LogUtil.e("js:::"+js);
                    ApisSeUtil.M m1=ApisSeUtil.i(js);
                    final String attachIdData = OkHttpUtils.post(url)
                            .params("key",m1.K())
                            .params("msg",m1.M())
                            .execute().body().string();
                    if (!attachIdData.equals("")) {
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(attachIdData);
                        if (m != null && ("000").equals(m.get("code"))) {
                            stu_id = m.get("sut_id");
                            HashMap<String,String > map=AnalyticalJSON.getHashMap(attachIdData);
//                           LogUtil.e( "run:  map-=-=-=-=-=支付宝解析得到的map：" + map);
//                            JSONObject js1 = new JSONObject();
//                            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
//                            String time = sd.format(System.currentTimeMillis())+RandomUtil.getFixLenthString(5);
//                            boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//                            js1.put("body",stu_id);
//                            js1.put("subject",title);
//                            js1.put("out_trade_no",time);
//                            js1.put("total_amount",allmoney);
//                            js1.put("product_code","QUICK_MSECURITY_PAY");
//                            Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(context, APPID, rsa2, js1.toString());
//                            String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//                            String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//                            String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//                            final String orderInfo= orderParam + "&" + sign;
                            if(map!=null) {
                                final String orderInfo = map.get("x");
                                LogUtil.w("最后的请求参数：" + orderInfo);

                                Runnable payRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressUtil.dismiss();
                                        PayTask alipay = new PayTask(context);
                                        final Map<String, String> result = alipay.payV2(orderInfo, true);
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                PayResult payResult = new PayResult(result);
                                                /**
                                                 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                                                 */
                                                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                                                String resultStatus = payResult.getResultStatus();
                                                // 判断resultStatus 为9000则代表支付成功
                                                if (TextUtils.equals(resultStatus, "9000")) {
                                                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                                                    Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                                                    if (mApplication.good_id_list != null) {
                                                        if (carIdlist != null) {
                                                            for (String id : mApplication.good_id_list) {
                                                                if (carIdlist.contains(id)) {
                                                                    carIdlist.remove(id);
                                                                    LogUtil.e("移除购物车商品Id！！！！！！！" + id + "   购物车ID；：：" + carIdlist);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Intent intent1 = new Intent("DingDan");
                                                    context.sendBroadcast(intent1);
                                                    Intent intent = new Intent("car");
                                                    context.sendBroadcast(intent);
                                                    Intent intent2 = new Intent("yaoyue");
                                                    context.sendBroadcast(intent2);
                                                } else {
                                                    mApplication.good_id_list.clear();
                                                }
//                                        else {
//                                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                                            Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
//                                        }

                                            }
                                        });
                                    }
                                };
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();

                            }


                        } else if(m!=null&&"003".equals(m.get("code"))){
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(mApplication.getInstance(), "手慢了，该团已被抢走了", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }else {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(mApplication.getInstance(), "获取订单号失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }





                    }
                } catch (Exception e) {

                }
            }
        }).start();

    }
}
