package com.laomachaguan.Utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;
import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.constants.OpenConstants;
import com.tencent.mobileqq.openpay.data.pay.PayApi;

import org.apache.http.conn.util.InetAddressUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2016/12/19.
 */
public class QpayUtil {

    private static String getOrderNoUrl = "https://qpay.qq.com/cgi-bin/pay/qpay_unified_order.cgi";
    private static String notify = Constants.host_Ip + Constants.oooooo + "Qpay_url";
    private static String tokenId;
    private static String callbackScheme = "qwallet1105643311";

    private static String API_Key = "ztdjBHJkREDaOuIs6UbslxRsjDcj7kDk";
    private static int paySerial = 1;

    public static final String APP_ID = "1105643311";
    // 签名步骤建议不要在app上执行，要放在服务器上执行
    // appkey建议不要保存app
    private static final String APP_KEY = "QPle8NDkjehWHPx8";
    private static final String Mch_ID = "1418221501";
    private static IOpenApi openApi;

    public static void registQPay(Context context) {
        openApi = OpenApiFactory.getInstance(context, APP_ID);
    }

    public static boolean IsMqqInstalled() {
        boolean isInstalled = openApi.isMobileQQInstalled();
        if (!isInstalled) {
            Toast.makeText(mApplication.getInstance(), "请安装手机QQ", Toast.LENGTH_SHORT).show();
        }
        return isInstalled;
    }

    public static boolean IsMqqSupportPay() {
        boolean isSupport = openApi.isMobileQQSupportApi(OpenConstants.API_NAME_PAY);
        if (!isSupport) {
            Toast.makeText(mApplication.getInstance(), "当前手机QQ版本暂不支持QQ钱包支付，请升级手机QQ版本", Toast.LENGTH_SHORT).show();
        }
        return isSupport;
    }


    public static void openQQPay(final Activity context, final String attach, final String title, final String allmoney, final String num, final String type,String spec_id) {
        registQPay(context);
        if (!IsMqqInstalled()) {
            return;
        }
        if (!IsMqqSupportPay()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String stu_id = "";
                String money = "";
                HttpParams httpParams=new HttpParams("type",type);
//                if(type.equals("4")){
//                    httpParams.put("mark",extra);
//                }
                try {
                    String attachIdData = OkHttpUtils.post(Constants.getAttachId_ip).tag(context)
                            .params("money", allmoney)
                            .params("title", title)
                            .params("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""))
                            .params("receiveid", Constants.M_id)
                            .params("key", Constants.safeKey)
                            .params("num", num)
                            .params("shop_id", attach)
                            .params(httpParams)
                            .params("pay_type", "3").execute().body().string();
                    if (!attachIdData.equals("")) {
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(attachIdData);
                        if (m != null && ("000").equals(m.get("code"))) {
                            stu_id = m.get("sut_id");
                            money = m.get("money");
                            LogUtil.e("换回的订单值：" + stu_id + "   总金额：" + money);
                            String Ip = getLocalHostIp();
                            String nonce_str = System.currentTimeMillis() + "";
                            StringBuilder a = new StringBuilder();
                            a
                                    .append("appid=").append(APP_ID)
                                    .append("&attach=").append(stu_id)
                                    .append("&body=").append(title)
                                    .append("&fee_type=").append("CNY")
                                    .append("&mch_id=").append(Mch_ID)
                                    .append("&nonce_str=").append(nonce_str)
                                    .append("&notify_url=").append(notify)
                                    .append("&out_trade_no=").append(Long.valueOf(nonce_str) / 1000 + "")
                                    .append("&spbill_create_ip=").append(Ip)
                                    .append("&total_fee=").append(money)
                                    .append("&trade_type=").append("APP")
                                    .append("&key=").append(API_Key);

                            String sign = MD5Utls.stringToMD5(a.toString()).toUpperCase();
                            LogUtil.e("拼接字符串"+a.toString()+"预订单签名：" + sign);

                            final Document document = DocumentHelper.createDocument();
                            Element root = document.addElement("xml");
                            root.addText("\n").addElement("appid").addText(APP_ID);
                            root.addText("\n").addElement("attach").addText(stu_id);
                            root.addText("\n").addElement("body").addText(title);
                            root.addText("\n").addElement("fee_type").addText("CNY");
                            root.addText("\n").addElement("mch_id").addText(Mch_ID);
                            root.addText("\n").addElement("nonce_str").addText(nonce_str);
                            root.addText("\n").addElement("notify_url").addText(notify);
                            root.addText("\n").addElement("out_trade_no").addText(Long.valueOf(nonce_str) / 1000 + "");
                            root.addText("\n").addElement("sign").addText(sign);
                            root.addText("\n").addElement("spbill_create_ip").addText(Ip);
                            root.addText("\n").addElement("total_fee").addText(money);
                            root.addText("\n").addElement("trade_type").addText("APP");
                            root.addText("\n");
                            LogUtil.w("QQ钱包预支付订单:\n" + document.getRootElement().asXML());
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.show(context, "", "正在调起QQ钱包，请稍等");
                                }
                            });
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String data = OkHttpUtils.post(getOrderNoUrl).upString(document.getRootElement().asXML())
                                                .execute().body().string();
                                        if (!data.equals("")) {
                                            HashMap<String, String> m = (HashMap<String, String>) WXPayUtils.readStringXmlOut(data);
                                            LogUtil.e("返回的数据：" + m);
                                            tokenId = m.get("prepay_id");
                                            beginQQPay(context);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }
                            }).start();
                        } else {
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
                    ProgressUtil.dismiss();
                    Toast.makeText(mApplication.getInstance(), "获取订单号失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }).start();
    }


    public static void beginQQPay(Context context) {
        if (TextUtils.isEmpty(tokenId)) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mApplication.getInstance(), "订单号生成失败，请检查网络并稍后重试", Toast.LENGTH_LONG).show();
                }
            });

        }

        PayApi api = new PayApi();
        api.appId = APP_ID;

        api.serialNumber = "" + paySerial++;
        api.callbackScheme = callbackScheme;

        api.tokenId = tokenId;
        api.pubAcc = "";
        api.pubAccHint = "";
        api.timeStamp = System.currentTimeMillis() / 1000;
        api.nonce = String.valueOf(System.currentTimeMillis());
        api.bargainorId = Mch_ID;

        try {
            signApi(api);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        LogUtil.e("数据是否完整" + api.checkParams());
        if (api.checkParams()) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressUtil.dismiss();
                }
            });
//            if (openApi == null) {
//                openApi = OpenApiFactory.getInstance(context, APP_ID);
//            }
            openApi.execApi(api);
        }
    }

    /**
     * 签名步骤建议不要在app上执行，要放在服务器上执行.
     */
    public static void signApi(PayApi api) throws Exception {
        // 按key排序
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appId=").append(api.appId);
        stringBuilder.append("&bargainorId=").append(api.bargainorId);
//        stringBuilder.append("&callbackScheme=").append(api.callbackScheme);
        stringBuilder.append("&nonce=").append(api.nonce);
        stringBuilder.append("&pubAcc=").append("");
        stringBuilder.append("&tokenId=").append(api.tokenId);

        byte[] byteKey = (APP_KEY + "&").getBytes("UTF-8");
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(byteKey, "HmacSHA1");
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] byteSrc = stringBuilder.toString().getBytes("UTF-8");
        // 完成 Mac 操作
        byte[] dst = mac.doFinal(byteSrc);
        // Base64
        api.sig = Base64.encodeToString(dst, Base64.NO_WRAP);
        api.sigType = "HMAC-SHA1";
        LogUtil.w("QQ二次签名：" + api.sig);
    }

    // 得到本机ip地址
    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            Log.e("feige", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;

    }

}
