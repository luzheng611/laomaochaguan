package com.laomachaguan.Utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/1/4.
 */
public class AliPayUtil_Old {
    /**
     * 支付宝支付业务：入参app_id
     */

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID = "2088422387874890";
    // 商户PID
    public static final String PARTNER = "2088422387874890";
    // 商户收款账号
    public static final String SELLER = "2088422387874890";

    public static final String RSA2_PRIVATE ="";
    //废弃私钥
//    public static final String RSA_PRIVATE ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCJuZbuonQKOxCDiZo6dB+CY9oKL13ZPXJ7tID8N6x6lbXvPZ0p5rzTvcIPO92Bx6tcFA+AYUj8S+PpHEETQLwjjPNBauY3ZFDsBcwPKzpwjRjiAfJ4Nz6FDMAf9o4WMssREysSPY4IFERkJs9XssrczFEEq8W84kIqNBDHQkSjA8UrOjnQ4fSX8Mi61r5SjiCCF2RyZTuVz8MECk0/NJrKTcY+FexrxVrCQiNME/Vd0+lXE9KjK+PoTpjGegZVROGwnzpsbHbAVFW5/Chw8FIxpPGViKRgn5MMVeM3fWsgKUDnELEJsuigApcdiBjQaGqKnMKKDFRwOV7Q72gBCqwHAgMBAAECggEAIzEC+hMUp5CUFahRemI1svGkwzl7N4lV+XoUA16OefxLMsiBojVNRqWUMqRPY1wL00lk9J7nMWCK6gPINQ0zhbn8ZljUXo8JhmWs0KtxEVVcaFOrC0DCfujVJtWvVMjBzqkhNlX4NuRG3Xh1Pql24Jhsk075Bdyin3oSV3f9ZdGsOdQn6LF3MlCNDjvbv9pCyJhDSydWZU/+l1qJgkG6x0iDWzuiiu750oUGZU92qRnOADS2RAfwmMGRqcHzflM8SxtDkWTu+5mXnLWiwgmsSOi2L2NT0/WLM6dUlrkK5HuJkDeVbGE7ckqjsp3sJ33wZK0hGsUHopRG+GsOIU/F4QKBgQDtEJolYOiwW5E6b3Hsp8PDOfzWuiTQctG1L2ia8zX5qnynvoJQAiFmhzbDDROyGoMu5J2+VUdfb3E1H2JVuQbymyCD+5gCwQiJIz4uDCYSQ85K/FF+iSjXj6+h7URonvZdNziDKlT6PTJrD0HmU9mD5OtgWqljzp1YAGg07JYDKQKBgQCUubqfWtNi75rJx8mP5+iflae3tRfC5/J3wCEl8qruZqsyb9DLPnoGXnegS2LGLJdmTleHOOpY/A6rPOH7ZCg/RosN708NC3yMAs3DPguslPF6el+MdeE0msmoFJnIqkDz9M2LdiZw8jyW3R3EF1MhMBxd79HzHSG0f1o/JrXLrwKBgBCxlZlCPMCGgjCSPnyCx9dMkxBv5T3EiB3xK7WVoNxm9AY/9R87NyzoIqQTnKpzEX/Q24bWrIL87wTlo+ATenjFEcIZinfPLhGyKKPHeTraCYHgSMDXWcfsN1r4wVN4tjKUO+eIqJlCi/VcCrD0gKG4Ehcj6z+7Aft7c9seOeYZAoGAAQ4kTdcmItCSZ5YcTwIVO3SjYQFO7toYdVMOqSdEoTZnRo9WiuqhkQQAk0mVpNRSKq6pnlMADquTKxHehJscUf6dI/crt2r6cj/e1+DL0mioIfdWUDfR5j6m6aNjx/G1TkXRY4juEuUVGvjsqiSEOXsO6He4JKJzR8WTjVkn9w8CgYEAmWxsINaV7XTWmaVsKBdNfvBPLi1RMIkqQj2NQ3cYXNGLsDrJlAZ4fsdPOYkPHBjTDzBezDJkytTD7G1Q2EMVH+To49dbLdcC1NnkwAF8nioz7vBVuWsbOK49fIvB3ESBqg1D3Uz2VW7zZ6kcn4s+7KUeDpgVq/6rmr2YkbvbH48=";
//    public static final String RSA_PRIVATE ="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAItuJgUffZC3beCN9YJ/IksBSZCgRhn5Tn6yVZvb8u3epuOwbbo5u+pnrUXWYQ43CB03Gqdal3I4IxB+TWUYBpS8Q5mQ4d92z5h5V+80BfW6ywEI2heM574aL6EFC2rG/aoTfANf3hcdR3kCQjmARw8tQZuhe7qelz1fV3URmiQnAgMBAAECgYAU/Fcsnji/91vKWJODOK9oqqDv66+haSyvVRgdhVVjCAgGbR+Wz+nAtioWQNYK1a2a+BhC6BezUU+1w8zSUnNq5BmOYsfdbxZXnMIhfQGzNFq2eMpHDJZOPvjSBWQX29TpYe6Ak88bpdXUuq6vAMEEW5gQK6ah+Y2Z7NEOuqLQgQJBAP7sALKf1kOhma+zoVNk6fNMJZg1a/Rml85ixCy9cx248kI8wDb7j9BOfU87knCQQ3eIiAU5kzdaD7TUdLo2SPkCQQCMBRsksBInqZCqqSAD16VDgDy5f+zRq25pg8Dctq5fOwgyoChQ9uPFxW0MbFgt0ad2ssAMnSebJ+kWZgyz8z4fAkAIRwF+y1HZwFWNLvf+DTjLfZ1648EtnmcvfGWCTmR+gpLbM73KC0EQMnw/JmAK56RJLVW9VbFCCtqXtI5VvY2BAkBxKgNJAIB7y8CfHXdBg70OtArgBXL59iqTXqe1+nMthkFW4TgUT6XBBpHe04Xk6igFd+xdhXooHPZ2Yt3rZjETAkBsU5VbZdHc69uT5bfLJ1+EdCVPHzYUHUcTGQ6LX8B78x6+IbxYteA6j0JAOlRU/D2l21EQSyPq9pCrAjRC7dBu";
//    public static final String RSA_PRIVATE="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAITtvqWq1IejHA4LFd9yAHKFiNt+MZ+Zo7BV2l44ZLNBt4mLhEUrnPklL3rcOIqM57ViUVgNGC5kOHvexJS94pqkBWcbPhDsNHztiZirSKXs0ipW41oTosMuoxpQYrdaFbt5FNmBszRZDc0QRNnBXL6ypWdyiPcscf4piWa1DnWpAgMBAAECgYA/h0P69wa1gC2TRJcCgABYuxrqE4hxx0KkrpM7PmZaCUlHEgd3610M8UmcxQy8opTGaiOIGlH5MeqQwKlgkNNv9hqwgu8RRy00CBZjgLj3xaYhhTRI0b9Nj6EESrWLfEwPTTznilYN0lHV52FImKRllFD4+vee7ew5OHuQ1aIcAQJBAL1j3093ciiawXNgDbVAwzGA/6jQ8zjFECIivGQHITnQ/Gih0LpP/9Es097ReeGVYGwDTD1Veyma8J6ASgMTaYECQQCzrkBfGOJOquDkjWi87WhrYbdujsjrZEW86XpUA7hwfH4O/BBL4+tJHiqCh5I9SoJW5NLVJF1KeIJ5f0XUmpApAkBVSYmB1s+A+5gMZgAmVKDSRT5cfqRZN105khz2isNqrvNMBzrg/C++ugo7eGgDr2o5mg6WPE13gf/D0RADbJWBAkAHVuXQPJ754acADvqpRPVP9ZTdkj2Ix/bFSbAygFhnV956VDeCMhQpT28jF9CUale6nuwxwqOA6D1EIzvB/HJJAkBD7Yu8Y/7j+hs+rZQROfRtPYWQtgPtGOcbdOLYF/+RsKk9k7gNnLQ31DmxOfwIw3o93lydZzS8ae+CgdAatcl2";
    public static final String RSA_PRIVATE="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAITtvqWq1IejHA4LFd9yAHKFiNt+MZ+Zo7BV2l44ZLNBt4mLhEUrnPklL3rcOIqM57ViUVgNGC5kOHvexJS94pqkBWcbPhDsNHztiZirSKXs0ipW41oTosMuoxpQYrdaFbt5FNmBszRZDc0QRNnBXL6ypWdyiPcscf4piWa1DnWpAgMBAAECgYA/h0P69wa1gC2TRJcCgABYuxrqE4hxx0KkrpM7PmZaCUlHEgd3610M8UmcxQy8opTGaiOIGlH5MeqQwKlgkNNv9hqwgu8RRy00CBZjgLj3xaYhhTRI0b9Nj6EESrWLfEwPTTznilYN0lHV52FImKRllFD4+vee7ew5OHuQ1aIcAQJBAL1j3093ciiawXNgDbVAwzGA/6jQ8zjFECIivGQHITnQ/Gih0LpP/9Es097ReeGVYGwDTD1Veyma8J6ASgMTaYECQQCzrkBfGOJOquDkjWi87WhrYbdujsjrZEW86XpUA7hwfH4O/BBL4+tJHiqCh5I9SoJW5NLVJF1KeIJ5f0XUmpApAkBVSYmB1s+A+5gMZgAmVKDSRT5cfqRZN105khz2isNqrvNMBzrg/C++ugo7eGgDr2o5mg6WPE13gf/D0RADbJWBAkAHVuXQPJ754acADvqpRPVP9ZTdkj2Ix/bFSbAygFhnV956VDeCMhQpT28jF9CUale6nuwxwqOA6D1EIzvB/HJJAkBD7Yu8Y/7j+hs+rZQROfRtPYWQtgPtGOcbdOLYF/+RsKk9k7gNnLQ31DmxOfwIw3o93lydZzS8ae+CgdAatcl2";
    private static final int SDK_PAY_FLAG = 1;


    /**
     * 支付宝支付业务
     */
    public static void openAliPay(final Activity context, final String allmoney, final String attachId, final String title, final String num, final String adress, final String type, final String number, final String spec_id) {
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
                        js.put("shop_id","1");
                        js.put("msg",attachId);
                        js.put("address",adress);
                    }else{
                        if(type.equals("11")){
                            url=Constants.getPingTuan_Pay_ip;
                            js.put("number",number.equals("0")?System.currentTimeMillis()/1000+RandomUtil.generateNumber(5):number);
                            js.put("shop_id",attachId);
                            if(spec_id!=null&&!spec_id.equals("")){
                                js.put("spec_id",spec_id);
                            }
                            js.put("address",adress);
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
                            if(map!=null) {
                                final String payInfo = map.get("x");
                                LogUtil.e("最后的请求参数：" + payInfo);

                                Runnable payRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        // 构造PayTask 对象
                                        PayTask alipay = new PayTask(context);
                                        // 调用支付接口，获取支付结果
                                        final String result = alipay.pay(payInfo, true);

                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                PayResult_Old payResult = new PayResult_Old(result);
                                                /**
                                                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                                                 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                                                 * docType=1) 建议商户依赖异步通知
                                                 */
//                                      String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                                                LogUtil.e(payResult.toString());
                                                String resultStatus = payResult.getResultStatus();
                                                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                                if (TextUtils.equals(resultStatus, "9000")) {
                                                    Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent("car");
                                                    intent.putExtra("removeId",true);
                                                    context.sendBroadcast(intent);

                                                    context.finish();
                                                } else {
                                                    // 判断resultStatus 为非"9000"则代表可能支付失败
                                                    // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                                    if (TextUtils.equals(resultStatus, "8000")) {
                                                        Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                                              Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }
                                        });
                                    }
                                };

                                // 必须异步调用
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                                ProgressUtil.dismiss();

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

//                        String orderInfo = getOrderInfo(title, stu_id, allmoney);
//                        /**
//                         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
//                         */
//                        String sign = sign(orderInfo);
//                        try {
//                            /**
//                             * 仅需对sign 做URL编码
//                             */
//                            sign = URLEncoder.encode(sign, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//
//                        /**
//                         * 完整的符合支付宝参数规范的订单信息
//                         */
//                        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//                        LogUtil.e("本地请求最后参数：："+payInfo);
//
//                        Runnable payRunnable = new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // 构造PayTask 对象
//                                PayTask alipay = new PayTask(context);
//                                // 调用支付接口，获取支付结果
//                                final String result = alipay.pay(payInfo, true);
//
//                              context.runOnUiThread(new Runnable() {
//                                  @Override
//                                  public void run() {
//
//                                      PayResult_Old payResult = new PayResult_Old(result);
//                                      /**
//                                       * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
//                                       * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
//                                       * docType=1) 建议商户依赖异步通知
//                                       */
////                                      String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                                      LogUtil.e(payResult.toString());
//                                      String resultStatus = payResult.getResultStatus();
//                                      // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//                                      if (TextUtils.equals(resultStatus, "9000")) {
//                                          Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
//                                          Intent intent=new Intent("car");
//                                          intent.putExtra("removeId",true);
//                                          context.sendBroadcast(intent);
//
//                                          context.finish();
//                                      } else {
//                                          // 判断resultStatus 为非"9000"则代表可能支付失败
//                                          // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//                                          if (TextUtils.equals(resultStatus, "8000")) {
//                                              Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();
//
//                                          } else {
//                                              // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
////                                              Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
//
//                                          }
//                                      }
//                                  }
//                              });
//                            }
//                        };
//
//                        // 必须异步调用
//                        Thread payThread = new Thread(payRunnable);
//                        payThread.start();
//                        ProgressUtil.dismiss();





                    }
                } catch (Exception e) {

                }
            }
        }).start();

    }

    /**
     * create the order info. 创建订单信息
     *
     */
    private static String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + System.currentTimeMillis()+""+ "\"";
//        // 商户网站唯一订单号
//        orderInfo += "&out_trade_no=" + "\"" +"123456"+ "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" +Constants.host_Ip+"/api.php/Api/Aliypay_url" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private static String sign(String content) {
        return SignUtils_Old.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
