package com.laomachaguan.Utils;

import android.os.Environment;

/**
 * Created by Administrator on 2016/5/30.
 */
public class Constants {

    //服务器测试域名
    public static final String host_Ip = "http://yintolo.net";
    //    //服务器域名
//    public static  final String host_Ip="https://indrah.cn";
    public static final String oooooo = "/api.php/EncryptApi/";
    public static final String pppppp = "/api.php/Api/";
    public static final String iiiiii = "/api.php/SecretApi/";
    public static final String M_id = "24";
    public  static final String NAME_LOW="yaocai";
    public static  final int  NAME_CHAR_NUM=6;

    public static final String Temples = host_Ip + oooooo + "Templexz";

    public static  final String cacheO=NAME_LOW; //图片上传
    public static final String ImageUp= host_Ip + iiiiii + "Imageup";

    //用户协议获取接口
    public static final String little_yhxy__IP = host_Ip + oooooo + "Yhxyhq";
    //用户协议同意
    public static final String yhxy_agree = host_Ip + oooooo + "Privacy";
    //外部缓存储存地址
    public static  final String ExternalCacheDir =Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+cacheO+"/";
    //启动页加载
//    public  static  final String getAD=host_Ip+oooooo+"Qdongye";
    public  static  final String getAD=host_Ip+oooooo+"Ggaoye";
    //定制更新
    public static final String Update_Ip = host_Ip + oooooo + "Dzupdate";
    //安全验证码
//    public static final String safeKey = "TGubFeLjtK8vYZFA5zYfUAsJEkJekTyG";
    //商户号
    public static final String WXPay_patnerID = "1487395832";
    //微信支付APPid
    public static final String WXPay_APPID = "wxf3ed9d569d882fe2";

    //举报反馈  key,user_id,title,contents
    public static final String Suggest_Ip = host_Ip + oooooo + "Suggest";
    //关于我们  key,m_id
    public static final String AboutUs_Ip = host_Ip + oooooo + "Ours";
    //微信支付下单地址
    public static final String WXPay_post_Url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //分享域名
    public static final String FX_host_Ip = host_Ip + "/cg"+".php/Index/";
    //分享测试域名
    public static final String FX_CS_host_Ip = "http://yintolo.zhideng.net/wap.php/Index/";
    //在服务器生成获取订单号
    public static final String getAttachId_ip = host_Ip + pppppp + "Wxpay";
    //在服务器生成拼团订单号
    public static final String getPingTuan_Pay_ip = host_Ip + pppppp + "Ptpay_one";
    //版本更新检测
    public static final String Check_Update_IP = host_Ip + oooooo + "Updatejc";
    //图片储存地址
    public static final String IMGDIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fojiao/img/";
    //头像储存地址
    public static final String HEADDIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fojiao/head_IMG/";
    //    手机验证码地址:
//    public static final String Mid_IP = host_Ip + oooooo + "Yzm";
    //新闻详情
    public static final String ZiXun_detail_Ip = host_Ip + oooooo + "newsd";
    //寺庙详情
    public static final String Simiao_detail_Ip = host_Ip + oooooo + "Templed";
    //新闻资讯评论接口
    public static final String ZiXun_detail_PL_Ip = host_Ip + oooooo + "Newspl";
    //所有新闻
    public static final String ZiXun_total_Ip = host_Ip + oooooo + "news";
    //本地注册接口
//    public static final String Regist_Ip = host_Ip + oooooo + "Dzregister";
//    //本地登陆接口
//    public static final String Login_Ip = host_Ip + oooooo + "Dzlogin";
    //授权登陆接口
    public static final String Login_3_Ip = host_Ip + oooooo + "Dzloginr";
    //推荐新闻接口
    public static final String ZiXun4Tujian_IP = host_Ip + oooooo + "Rnews";
    //直播详情接口
    public static final String Live_detail_Ip = host_Ip + oooooo + "Channelsd";
    //直播供养清单接口
    public static final String Live_gongyang_Ip = host_Ip + oooooo + "Channelsgyqd";
    //视频详情接口
    public static final String Video_detail_Ip = host_Ip + oooooo + "Videod";
    //   视频列表接口
    public static final String Video_list_Ip = host_Ip + oooooo + "Video";

    //直播列表接口
    public static final String Live_list_IP = host_Ip + oooooo + "Live";
    //视频评论接口
    public static final String Video_PL_IP = host_Ip + oooooo + "Videopl";
    //直播评论接口
    public static final String Live_PL_IP = host_Ip + oooooo + "Channelspl";
    //上传头像
    public static final String uploadHead_IP = host_Ip + oooooo + "Dzhead";
    //新闻收藏信息展示接口
    public static final String news_sc_list_Ip = host_Ip + oooooo + "Keepnews";
    //新闻收藏信息展示接口
    public static final String video_sc_list_Ip = host_Ip + oooooo + "Keepvideo";

    //新闻收藏信息展示接口
    public static final String temple_sc_list_Ip = host_Ip + oooooo + "Keeptemple";


    //忘记密码接口
    public static final String WJMM_IP = host_Ip + oooooo + "Mpwd";
    //寺庙列表接口
    public static final String simiao_IP = host_Ip + oooooo + "Temple";
    //个人信息完善接口
//    public static final String Mine_Grzl_IP = host_Ip + oooooo + "Dzuser";
    //用户认证须知接口
    public static final String User_Need_Know_IP = host_Ip + oooooo + "Yhxz";

    /**
     * 添加评论
     */
    //资讯
    public static final String News_PL_add_IP = host_Ip + oooooo + "Newstjpl";
    //视频
    public static final String Video_PL_add_IP = host_Ip + oooooo + "Videotjpl";
    /**
     * 添加评论
     */
    //评论点赞
    public static final String PL_DZ_IP = host_Ip + oooooo + "Pllike";
    //新闻点赞
    public static final String ZX_DZ_IP = host_Ip + oooooo + "Newslike";
    //直播点赞
    public static final String MDEIA_DZ_IP = host_Ip + oooooo + "Channelslike";

    //视频点赞
    public static final String Video_DZ_IP = host_Ip + oooooo + "Videolike";
    //资讯收藏
    public static final String News_SC_Ip = host_Ip + oooooo + "Newskeep";
    //视频收藏
    public static final String Video_SC_Ip = host_Ip + oooooo + "Videokeep";
    //关注用户接口
    public static final String Guanzhu_IP = host_Ip + oooooo + "Userkeep";
    //关注寺庙接口
    public static final String Temple_SC_IP = host_Ip + oooooo + "Templekeep";
    //nianfo——首页——nianfo
    public static final String nianfo_home_nianfo_Get_Ip = host_Ip + oooooo + "Buddha";
    //nianfo——首页——念佛提交
    public static final String nianfo_home_nianfo_Commit_Ip = host_Ip + oooooo + "Buddhad";
    //nianfo——首页——诵经
    public static final String nianfo_home_songjing_Get_Ip = host_Ip + oooooo + "Reading";
    //nianfo——首页——诵经提交
    public static final String nianfo_home_songjing_Commit_Ip = host_Ip + oooooo + "Readingd";
    //nianfo——首页——持咒
    public static final String nianfo_home_chizhou_Get_Ip = host_Ip + oooooo + "Japa";
    //nianfo——首页——持咒提交
    public static final String nianfo_home_chizhou_Commit_Ip = host_Ip + oooooo + "Japad";
    //nianfo——首页——忏悔
    public static final String nianfo_home_chanhui_Get_Ip = host_Ip + oooooo + "Confess";
    //nianfo——首页——忏悔提交
    public static final String nianfo_home_chanhui_Commit_Ip = host_Ip + oooooo + "Confesstj";
    //nianfo——首页——忏悔点赞
    public static final String nianfo_home_chanhui_Dianzan_Ip = host_Ip + oooooo + "Confessdz";
    //nianfo——首页——佛号加载
    public static final String nianfo_home_zhunian_fohao_Ip = host_Ip + oooooo + "Fohaohq";
    //nianfo——首页——助念
    public static final String nianfo_home_zhunian_Get_Ip = host_Ip + oooooo + "Reciting";
    //nianfo——助念详情
    public static final String nianfo_zhunian_detail_Get_Ip = host_Ip + oooooo + "Recitingmx";
    //nianfo——首页——助念提交
    public static final String nianfo_home_zhunian_Commit_Ip = host_Ip + oooooo + "Recitingtj";
    //nianfo——首页——助念点赞
    public static final String nianfo_home_zhunian_ZN_Ip = host_Ip + oooooo + "Recitingzn";
    //功课明细——nianfo
    public static final String Mine_GK_NF = host_Ip + oooooo + "Buddhaset";
    //功课明细——诵经
    public static final String Mine_GK_SJ = host_Ip + oooooo + "Readingset";
    //功课明细——持咒
    public static final String Mine_GK_CZ = host_Ip + oooooo + "Japaset";

    //诵经明细
    public static final String SongJing_detail_Ip = host_Ip + oooooo + "Readingcj";
    //持咒明细
    public static final String ChiZhou_detail_Ip = host_Ip + oooooo + "Japacj";
    //念佛明细
    public static final String Nianfo_detail_Ip = host_Ip + oooooo + "Buddhacj";


    //用户关注列表
    public static final String mine_GZ_list_Ip = host_Ip + oooooo + "Keepuser";
    //按昵称和电话号码搜索
    public static final String search_People_Ip = host_Ip + oooooo + "Cxfriend";
    //收到好友邀请查询邀请人信息
    public static final String search_FriendInvite_Ip = host_Ip + oooooo + "Usermsg";
    //同意好友请求
    public static final String agreeFriend_Ip = host_Ip + oooooo + "Tjfriend";
    //请求好友列表
    public static final String getFriendList_Ip = host_Ip + oooooo + "Friendlist";
    //推荐最热接口
    public static final String Home_hot = host_Ip + oooooo + "Hot";
    //每页资讯条数
    public static final String ZiXun_total_count = "10";
    //需要刷新界面时的handler。what
    public static final int needSetMsg = 1;
    //加载数据失败的handler。what
    public static final int LoadFail = 2;
    //数据变更的handler。what
    public static final int needChanged = 0;

    public static String appFileName = "fojiao.apk";
    public static String SavePath = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static final String User_info_xiugainc = host_Ip + oooooo + "Dzgrnc";//昵称
    public static final String User_info_xiugaixb = host_Ip + oooooo + "Dzgrxb";//性别
    public static final String User_info_xiugaitemple = host_Ip + oooooo + "Meanstemple"; //寺庙


    //好友通知
    public static final String MsgTZ_IP = host_Ip + oooooo + "Msgtz";
    //添加好友
    public static final String QQfriend_IP = host_Ip + oooooo + "Qqfriend";
    //接收好友请求
    public static final String Jsfriend_IP = host_Ip + oooooo + "Jsfriend";
    //删除好友
    public static final String SCfriend_IP = host_Ip + oooooo + "Scfriend";
    //我的助念明细
    public static final String MyZuNian_IP = host_Ip + oooooo + "Recitingmx";

    //私聊信息接口
    public static final String Siliao_IP = host_Ip + oooooo + "Siliao";
    //私聊信息发送接口
    public static final String Siliao_fasong_IP = host_Ip + oooooo + "Fsxiaoxi";
    //私聊会话列表接口
    public static final String Siliao_List_IP = host_Ip + oooooo + "Siliaolist";


    //    //用户手机号绑定
//    public static final String Phone_Commit_Ip = host_Ip + oooooo + "Dzhqsjhm";
    //用户资料获取
    public static final String User_Info_Ip = host_Ip + oooooo + "Dzyhzlhq";
    //用户删除接口
    public static final String User_Delete_IP = host_Ip + oooooo + "Userdelete";
    //商品列表
    public static final String ShangPin_list_Ip = host_Ip + oooooo + "Shoplist";
    //商品详情页
    public static final String ShangPin_Detail_Ip = host_Ip + oooooo + "Shopd";
    //商品详情评论页
    public static final String ShangPin_Detail_PL_Ip = host_Ip + oooooo + "Shoppl";
    //商品详情评论页
    public static final String ShangPin_Detail_PL_COmmit_Ip = host_Ip + oooooo + "Shoptjpl";
    //支付清单页
    public static final String ZhiFu_Detail_Ip = host_Ip + oooooo + "Zcqd";
    //活动列表
    public static final String Activity_list_IP = host_Ip + oooooo + "Activity";
    //活动详情
    public static final String Activity_detail_IP = host_Ip + oooooo + "Activityd";
    //活动评论列表
    public static final String Activity_pinglun_IP = host_Ip + oooooo + "Activitypl";
    //活动评论添加
    public static final String Activity_pinglun_add_IP = host_Ip + oooooo + "Activitytjpl";
    //活动收藏添加
    public static final String Activity_Shoucang_IP = host_Ip + oooooo + "Activitykeep";
    //活动收藏列表
    public static final String Activity_Shoucang_list_IP = host_Ip + oooooo + "Keepactivity";
    //活动点赞
    public static final String Activity_DZ_IP = host_Ip + oooooo + "Activitylike";
    //活动信息报名
    public static final String Activity_BaoMing_IP = host_Ip + oooooo + "Enrollment";
    //活动报名
    public static final String Activity_BaoMing = host_Ip + oooooo + "Activitybm";
    //短信邀请接口
    public static final String little_sms_get__IP = host_Ip + oooooo + "Dxyqhq";

    /**
     * 商品列表
     */
    public static final String GOODS_LIST = host_Ip + oooooo + "Products";
    /**
     * 商品详情
     */
    public static final String GOODS_DETAIL = host_Ip + oooooo + "Productsd";
    //用户列表接口
    public static final String User_List_IP = host_Ip + oooooo + "Userlist";
    /**
     * 众筹列表
     */
    public static final String FUND_LIST = host_Ip + oooooo + "Zhongchou";
    /**
     * 众筹详情
     */
    public static final String FUND_DETAIL = host_Ip + oooooo + "Zhongchoud";
    //个人信息完善接口
    public static final String Mine_Grzl_IP = host_Ip + oooooo + "Dzuser";
    //登录接口
    public static final String Login_Ip = host_Ip + oooooo + "Dzlogin";
    //安全验证码
    public static final String safeKey = "TGubFeLjtK8vYZFA5zYfUAsJEkJekTyG";
    //    手机验证码地址:
    public static final String Mid_IP = host_Ip + oooooo + "Yzm";
    //本地注册接口
    public static final String Regist_Ip = host_Ip + oooooo + "Dzregister";
    //用户手机号绑定
    public static final String Phone_Commit_Ip = host_Ip + oooooo + "Dzhqsjhm";
    /**
     * 获取用户资料
     */
    public static final String USER_INFO = host_Ip + oooooo + "Dzyhzlhq";
    /**
     * 众筹详情的评论接口
     */
    public static final String FUNDING_DETAIL_COMMENTS = host_Ip + oooooo + "Cfgpl";
    /**
     * 众筹详情的添加评论接口
     */
    public static final String FUNDING_DETAIL_ADD_COMMENTS = host_Ip + oooooo + "Cfgtjpl";
    /**
     * 众筹详情的收藏接口
     */
    public static final String FUNDING_DETAIL_Shoucang = host_Ip + oooooo + "Cfgkeep";
    /**
     * 众筹详情的收藏列表接口
     */
    public static final String FUNDING_DETAIL_Shoucang_List = host_Ip + oooooo + "Keepcfg";


    //搜索
    //资讯搜索
    public static final String News_Search_Ip = host_Ip + oooooo + "Newsquery";
    //活动搜索
    public static final String Activity_Search_Ip = host_Ip + oooooo + "Activityquery";
    //供养搜索
    public static final String GY_Search_Ip = host_Ip + oooooo + "Shopquery";
    //众筹搜索
    public static final String CFG_Search_Ip = host_Ip + oooooo + "Cfgquery";

    //众筹支持人数
    public static final String CFG_List_Ip = host_Ip + oooooo + "Cfgqd";
    public static final String WXPay_API="7IG4NxObuSMwtbvy9GMkDGjj4myqycqS";
    //修改签名接口
    public static  final String SignChange=host_Ip+oooooo+"Dzgxqm";


    //全部点餐
    public static final String Order_total = host_Ip + oooooo + "Order";
    //分类点餐列表
    public static final String Order_special = host_Ip + oooooo + "Ordercx";
    //点餐类目
    public static final String Order_type = host_Ip + oooooo + "Ordertype";
    //加入购物车
    public static final String Order_add_car = host_Ip + oooooo + "Ordercar";
    //商品详情
    public static final String Order_detail = host_Ip + oooooo + "Orderd";
    //商品详情2
    public static final String Good_detail = host_Ip + iiiiii + "Goodsd";
    //商品收藏取消详情
    public static final String Order_shoucang = host_Ip + oooooo + "Orderkeep";
    //商品收藏列表
    public static final String Order_shoucang_list = host_Ip + oooooo + "Keeporder";
    //购物车列表
    public static final String Shopcar_list = host_Ip + oooooo + "Shopcar";
    //购物车保存修改
    public static final String Shopcar_save = host_Ip + oooooo + "Shopsave";
    //购物车删除商品
    public static final String Shopcar_delete = host_Ip + oooooo + "Shopdelete";
    //商品点赞
    public static final String Order_like = host_Ip + oooooo + "Orderlike";


    //书城我的收获地址
    public static final String Shucheng_shouhuo_list_Ip = host_Ip + oooooo + "Address";
    //书城添加收货地址
    public static final String Shucheng_shouhuo_add_Ip = host_Ip + oooooo + "Addresstj";
    //书城修改收货地址
    public static final String Shucheng_shouhuo_update_Ip = host_Ip + oooooo + "Addressxg";
    //书城删除收货地址
    public static final String Shucheng_shouhuo_delete_Ip = host_Ip + oooooo + "Addresssc";


    //我的订单页面
    public static final String MyOrders=host_Ip+oooooo+"Myorders";
    //订单详情页面
    public static final String MyOrders_detail=host_Ip+oooooo+"Myordersd";


    //    评论回复
    public static final String little_zixun_pl_add_IP =host_Ip+oooooo+"Pinglun_hf";
    public static final  String little_pl_huifu__IP= host_Ip+oooooo+"Pinglun_hfxs";
    //个人投稿列表接口
    public static final String Tougao_Mine_list_IP = host_Ip + oooooo + "Draftpt";

    //商城支付页面
    public static final String Goods_pay=host_Ip+pppppp+"Goodspay";

    //资讯发布接口
    public static final String little_news_add_IP = host_Ip + oooooo + "Xcx_newsadd";
    //上传图文
    public static final String upload_image = host_Ip + oooooo + "Draft";
    //资讯列表接口
    public static final String Zixun_List_IP = host_Ip + oooooo + "Draftlist";
    //投稿详情接口
    public static final String Zixun_Detail_IP = host_Ip + oooooo + "Draftxq";
    //资讯管理删除接口
    public static final String Archmage_delete_zixun_IP = host_Ip + oooooo + "Draftdelete";
    //资讯查询接口
    public static final String Zixun_Search = host_Ip + oooooo + "Draftquery";
    //资讯收藏取消接口
    public static final String Zixun_shoucang_cancle_IP = host_Ip + oooooo + "Draftkeep";
    //资讯点赞接口
    public static final String Zixun_dianzan_IP = host_Ip + oooooo + "Draftlike";
    //资讯评论提交接口
    public static final String Zixun_commitPL_IP = host_Ip + oooooo + "Drafttjpl";
    //资讯评论展示接口
    public static final String Zixun_PL_IP = host_Ip + oooooo + "Draftpl";
    //全部商品显示接口
    public static final String getGoodTotal = host_Ip + iiiiii + "Ordergl";
    //商品删除接口
    public static final String GoodsDelete = host_Ip + iiiiii + "Goodsdelete";
    //商品上下架接口
    public static final String GoodsShangjia = host_Ip + iiiiii + "Goodsxs";
    //商品置顶接口
    public static final String GoodsZhiding = host_Ip + iiiiii + "Goodszd";
    //商品添加接口
    public static final String GoodsAdd = host_Ip + iiiiii + "Goodsadd";
    //商品更新接口
    public static final String GoodsUpdate = host_Ip + iiiiii + "Goodsupdate";
    //商品账单接口
    public static final String GoodsDingdan = host_Ip + iiiiii + "Group";
    //管理员凑单接口
    public static final String GoodsAdminSave = host_Ip + iiiiii + "Adminsave";
    //管理员发货接口
    public static final String GoodsAdminfahuo= host_Ip + iiiiii + "Adminfahuo";

    //管理员订单管理接口
    public static final String GoodsAdminGroup= host_Ip + iiiiii + "Admingroup";
    //管理员订单管理接口
    public static final String PTtuikuan= host_Ip + iiiiii + "Pttuikuan";
    //管理员订单详情接口
    public static final String GoodsAdminDdxq= host_Ip + iiiiii + "Myddxq";
    //资讯收藏展示接口
    public static final String Zixun_shoucang_list_IP = host_Ip + oooooo + "Keepdraft";
    //邀请码提交接口欧
    public static final String PushPromoCode = host_Ip + iiiiii + "Promo";
//    //约功能条件信息获取接口
    public static final String getYueSetting = host_Ip + iiiiii + "Pact_set";
    //邀约列表获取接口
    public static final String getYueList = host_Ip + iiiiii + "Pactlist";
    //参与约活动
    public static final String yingYao = host_Ip + iiiiii + "Pactadd";
    //邀约发布第一步接口
    public static final String getYueFirst= host_Ip + iiiiii + "Pact";
    //邀约地点和金钱修改接口
    public static final String Pact_Update= host_Ip + iiiiii + "Pact_update";
    //邀约地点修改接口
    public static final String setYuePlace= host_Ip + iiiiii + "Pact_addresupdate";
    //邀约起价修改接口
    public static final String setYueMoney= host_Ip + iiiiii + "Pact_moneyupdate";
    //邀约参与人数列表接口
    public static final String yaoyue_people_list= host_Ip + iiiiii + "Pactuser";
    //积分页面接口
    public static final String JIFen= host_Ip + iiiiii + "Mycredit";
    //拼团详情接口
    public static final String Detail_PinTuan= host_Ip + iiiiii + "Groupd";
    //我的拼团——普通用户接口
    public static final String MyPinTuan_Normal= host_Ip + iiiiii + "Mygroup";
    //管理员积分页面接口
    public static final String AdminJIFen= host_Ip + iiiiii + "Admincredit";
    //拼团收藏及取消接口
    public static final String PinTuan_Shoucang= host_Ip + iiiiii + "Groupkeep";
    //拼团关注列表接口
    public static final String PinTuan_Guanzhu_List= host_Ip + iiiiii + "Keepgroup";

    //管理员积分体现接口
    public static final String AdminJIFen_update= host_Ip + iiiiii + "Creditupdate";
    //邀约删除接口
    public static final String yaoyue_Delete= host_Ip + iiiiii + "Pactdelete";
    //查询用户获取ID接口
    public static final String AdminSearch_GetID= host_Ip + oooooo + "Cxuser";
    //帮助接口
    public static final String BangZhu=host_Ip+oooooo+"Bzzxhq";
    //积分提现规则
    public static final String Credit=host_Ip+oooooo+"Credit";
    //普通商品退款
    public static final String TuiKuan_Normal=host_Ip+iiiiii+"Putuikuan";
    //提现提交
    public static final String Commit_Tixian=host_Ip+iiiiii+"Cash";
    //提现记录
    public static final String Tixian_List=host_Ip+iiiiii+"Cashlist";
    //提现设置
    public static final String Tixian_Setting=host_Ip+iiiiii+"Cashupdate";

    //搜索用户
    public static final String search_User_Ip = host_Ip + oooooo + "Cxuser";

    //请求添加好友
    public static final String ADD_Friend_Ip = host_Ip + oooooo + "Qqfriend";
    //消息通知显示
    public static final String TongzhiList_Ip = host_Ip + oooooo + "Msgtz";
    //消息通知删除
    public static final String TongzhiDelete_Ip = host_Ip + oooooo + "Shanchutz";
    //接受好友请求
    public static final String TongzhiAgree_Ip = host_Ip + oooooo + "Jsfriend";
    //好友列表
    public static final String FridendList = host_Ip + oooooo + "Friendlist";
    //删除好友
    public static final String FridendDelete = host_Ip + oooooo + "Scfriend";
    //私聊列表接口
    public static final String Chat_List = host_Ip + oooooo + "Siliao";
    //私聊文字接口
    public static final String Chat_Content_UpLoad = host_Ip + oooooo + "Fsxiaoxi";
    //私聊对象
    public static final String Chat_Message = host_Ip + oooooo + "Siliaolist";
    //咨询详情聊天列表接口
    public static final String little_advice_PL_IP = host_Ip + oooooo + "Xcx_advicepl";
    //咨询详情聊天文字回复接口
    public static final String little_advice_PL_text_IP = host_Ip + oooooo + "Xcx_adviceplwz";
    //咨询详情聊天图片回复接口
    public static final String little_advice_PL_img_IP = host_Ip + oooooo + "Xcx_advicepltp";
    //私信音频接口
    public static final String little_addvice_audio__IP = host_Ip + oooooo + "Xcx_adviceplyp";
    //私信视频接口
    public static final String little_addvice_video__IP = host_Ip + oooooo + "Xcx_adviceplsp";
    //单人列表接口    1.发出2 收到
    public static final String little_advice_List_one_IP = host_Ip + oooooo + "Xcx_adviceone";
    //所有咨询列表接口    1.发出2 收到
    public static final String little_advice_List_all_IP = host_Ip + oooooo + "Xcx_advice";
    //开店用户协议获取
    public static final String Store_Prol = host_Ip + oooooo + "Yhxyhq";
    //开店申请
    public static final String Store_Post_Apply = host_Ip + iiiiii + "Shopup";
    //我的店铺订单
    public static final String Mine_store = host_Ip + iiiiii + "Storegroup";
    //店铺返现比例设置
    public static final String Mine_store_Setting = host_Ip + iiiiii + "Shopupdate";
    //店铺详情
    public static final String Mine_store_Detail = host_Ip + iiiiii + "Stored";
    //快递信息查询
    public static final String Expresses = host_Ip + iiiiii + "Express";
    //快递公司列表查询
    public static final String ExpressesList= host_Ip + iiiiii + "Expresslist";
    //绑定快递信息
    public static final String Bdexpress= host_Ip + iiiiii + "Bdexpress";

    //首页轮播图
    public static final String getBanner=host_Ip+iiiiii+"Banner";

    //商城搜索
    public static final String Good_Search = host_Ip + oooooo + "Orderquery";


}
