package com.imall.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConstantsBak {
	public static final String APP_PACKAGE_NAME_XPOSED = "de.robv.android.xposed.installer";
	public static final String APP_PACKAGE_NAME_ADBLOCKER = "pl.cinek.adblocker";
	public static final String APP_PACKAGE_NAME_XPRIVACY = "biz.bokhorst.xprivacy";
	public static final String APP_PACKAGE_NAME_MOBILE_FAKER = "com.unique.mobilefaker";
	public static final String APP_PACKAGE_NAME_YING_YONG_BIAN_LIANG = "com.sollyu.xposed.hook.model";
	public static final String APP_PACKAGE_NAME_XMANAGER = "com.imalljoy.xmanager";
	public static final String APP_PACKAGE_NAME_XMATOR = "com.miyauchirenge.xmator";
	public static final String APP_PACKAGE_NAME_XMATOR_TEST = "com.miyauchirenge.xmator.test";

	//刷第三方逻辑：1.启动的时候把已经安装的所有app信息加载好，去掉微愿，去掉xposed相关的，分好类
    //2.每次请求已经安装的app的时候，从分类中找对应的app，每个app可以增加个概率？记住有些app是必须有的，有些是概率很高的。这时候应该判断微愿有没有，有的话直接回复有，没有回复没有。另外还要加上当前请求的app？
    //3.把这些请求记录下来吧，看看什么情况。
    //4.clear的时候要把所有的app clear一遍，然后安装之前打开一次？
    //5.要有带不带刷第三方的选项，不带的话只要将真实的安装app给他们就行了，去掉xposed相关的。带的话就需要执行上面的逻辑？
    //6.xposed相关的app，可以获取到真实的安装app目录，不然可能出问题。
    //7.必须模拟的信息：模拟SDK int和SDK name，模拟分辨率；可以考虑自己写一个程序模拟所有的信息，这样就可以控制手机型号、IMEI之类的了？

    //改进现有的ASO逻辑：上面的5、6、7就可以了？

    //TODO 是不是应该把包名都变成小写再去比较？

    /**
     * xposed相关的app，都需要去掉的
     */

	public static final Map<String, String> APP_SDK_JIGUANG = new HashMap<String, String>();//极光sdk
    static{
        APP_SDK_JIGUANG.put("com.ehai", "一嗨租车");
        APP_SDK_JIGUANG.put("com.sdu.didi.psnger", "滴滴出行");
        APP_SDK_JIGUANG.put("com.jsbc.lznews", "荔枝新闻");
        APP_SDK_JIGUANG.put("com.lingan.seeyou", "美柚");
    }

    public static final Map<String, String> APP_SDK_YIGUAN = new HashMap<String, String>();//易观sdk
    static{
        APP_SDK_YIGUAN.put("cn.buding.martin", "微车违章查询");
        APP_SDK_YIGUAN.put("com.sdu.didi.psnger", "滴滴出行");
    }

    public static final Map<String, String> APP_SDK_AIRUI = new HashMap<String, String>();//艾瑞sdk
    static{
        APP_SDK_AIRUI.put("com.autonavi.minimap", "高德地图");
    }

    public static final Map<String, String> APP_SDK_GETUI = new HashMap<String, String>();//个推sdk
    static{
        APP_SDK_GETUI.put("com.zhenai.android", "珍爱网");
        APP_SDK_GETUI.put("com.moji.mjweather", "墨迹天气");
        APP_SDK_GETUI.put("com.babytree.apps.pregnancy", "宝宝树");
        APP_SDK_GETUI.put("cn.etouch.ecalendar", "中华万年历");
        APP_SDK_GETUI.put("ctrip.android.view", "携程");
        APP_SDK_GETUI.put("me.ele", "饿了么");
    }


    public static final Map<String, String> APP_SDK_XIAOMI = new HashMap<String, String>();//小米sdk
    static{
        APP_SDK_XIAOMI.put("com.sskj.flashlight", "随手电筒");
        APP_SDK_XIAOMI.put("com.ximalaya.ting.android", "喜马拉雅");
        APP_SDK_XIAOMI.put("com.gotokeep.keep", "KEEP");
        APP_SDK_XIAOMI.put("com.ss.android.essay.joke", "内涵段子");
    }


    public static final Map<String, String> APP_SDK_GEXIN = new HashMap<String, String>();//个信sdk
    static{
        APP_SDK_GEXIN.put("com.wuba", "58同城");
        APP_SDK_GEXIN.put("com.mt.mtxx.mtxx", "美图秀秀");
    }

    public static final Map<String, String> APP_SDK_YOUMENG = new HashMap<String, String>();//友盟sdk
    static{
        APP_SDK_YOUMENG.put("com.smile.gifmaker", "快手");
        APP_SDK_YOUMENG.put("com.ss.android.article.news", "今日头条");
        APP_SDK_YOUMENG.put("com.meitu.meiyancamera", "美颜相机");
    }

    //自动生成开始
    public static final Map<String, String> APP_SHEJIAOSHEQU = new HashMap<String, String>();//分类：社交社区
    static{
        APP_SHEJIAOSHEQU.put("com.zhenai.android", "珍爱网");
        APP_SHEJIAOSHEQU.put("com.gotokeep.keep", "KEEP");
        APP_SHEJIAOSHEQU.put("com.tencent.mm", "微信");
        APP_SHEJIAOSHEQU.put("com.tencent.mobileqq", "QQ");
        APP_SHEJIAOSHEQU.put("com.sina.weibo", "新浪微博");
        APP_SHEJIAOSHEQU.put("com.immomo.momo", "陌陌");
        APP_SHEJIAOSHEQU.put("com.budejie.www", "百思不得姐");
        APP_SHEJIAOSHEQU.put("qsbk.app", "糗百");
        APP_SHEJIAOSHEQU.put("com.ss.android.essay.joke", "内涵段子");
        APP_SHEJIAOSHEQU.put("com.zhihu.android", "知乎");
        APP_SHEJIAOSHEQU.put("com.qzone", "QQ空间");
        APP_SHEJIAOSHEQU.put("com.baidu.tieba", "百度贴吧");
        APP_SHEJIAOSHEQU.put("com.duitang.main", "堆糖");
    }

    public static final Map<String, String> APP_GONGJULEI = new HashMap<String, String>();//分类：工具类
    static{
        APP_GONGJULEI.put("com.sohu.inputmethod.sogou", "搜狗输入法");
        APP_GONGJULEI.put("com.baidu.BaiduMap", "百度地图");
        APP_GONGJULEI.put("com.baidu.input", "百度输入法");
        APP_GONGJULEI.put("com.tencent.qqpimsecureglobal", "腾讯手机管家");
        APP_GONGJULEI.put("com.snda.wifilocating", "WiFi万能钥匙");
        APP_GONGJULEI.put("com.iflytek.inputmethod", "讯飞输入法");
        APP_GONGJULEI.put("com.baidu.appsearch", "百度手机助手");
        APP_GONGJULEI.put("com.tencent.androidqqmail", "QQ邮箱");
        APP_GONGJULEI.put("sina.mobile.tianqitong", "新浪天气通");
        APP_GONGJULEI.put("com.youdao.dict", "有道词典");
        APP_GONGJULEI.put("com.baidu.homework", "作业帮");
        APP_GONGJULEI.put("com.MobileTicket", "铁路12306");
        APP_GONGJULEI.put("com.pp.assistant", "PP助手");
        APP_GONGJULEI.put("com.cootek.smartdialer", "触宝电话");
        APP_GONGJULEI.put("com.baidu.netdisk_ss", "百度云");
        APP_GONGJULEI.put("com.tencent.qqpinyin", "QQ输入法");
        APP_GONGJULEI.put("com.wochacha", "我查查");
        APP_GONGJULEI.put("com.tmri.app.main", "交管12123");
        APP_GONGJULEI.put("com.cubic.autohome", "汽车之家");
        APP_GONGJULEI.put("com.sskj.flashlight", "随手电筒");
        APP_GONGJULEI.put("cn.buding.martin", "微车违章查询");
        APP_GONGJULEI.put("com.moji.mjweather", "墨迹天气");
        APP_GONGJULEI.put("cn.etouch.ecalendar", "中华万年历");
        APP_GONGJULEI.put("com.qihoo.appstore", "360手机助手");
        APP_GONGJULEI.put("com.wandoujia.phoenix2", "豌豆荚");
        APP_GONGJULEI.put("com.lingan.seeyou", "美柚");
        APP_GONGJULEI.put("com.babytree.apps.pregnancy", "宝宝树");
        APP_GONGJULEI.put("com.eg.android.AlipayGphone", "支付宝");
        APP_GONGJULEI.put("com.qihoo.browser", "360手机浏览器");
    }

    public static final Map<String, String> APP_YUEDU = new HashMap<String, String>();//分类：阅读
    static{
        APP_YUEDU.put("com.jsbc.lznews", "荔枝新闻");
        APP_YUEDU.put("com.tencent.news", "腾讯新闻");
        APP_YUEDU.put("com.ss.android.article.news", "今日头条");
        APP_YUEDU.put("com.sina.news", "新浪新闻");
        APP_YUEDU.put("com.chaozh.iReaderFree", "掌阅");
        APP_YUEDU.put("com.netease.newsreader.activity", "网易新闻");
        APP_YUEDU.put("com.tencent.reading", "天天快报");
        APP_YUEDU.put("com.qq.reader", "QQ阅读");
        APP_YUEDU.put("com.ifeng.news2", "凤凰新闻");
        APP_YUEDU.put("com.hipu.yidian", "一点资讯");
    }

    public static final Map<String, String> APP_SHEXIANGMEITU = new HashMap<String, String>();//分类：摄像美图
    static{
        APP_SHEXIANGMEITU.put("com.mt.mtxx.mtxx", "美图秀秀");
        APP_SHEXIANGMEITU.put("com.meitu.meiyancamera", "美颜相机");
        APP_SHEXIANGMEITU.put("com.campmobile.snowcamera", "b612咔叽");
        APP_SHEXIANGMEITU.put("com.meitu.wheecam", "潮自拍");
        APP_SHEXIANGMEITU.put("com.tencent.ttpic", "天天P图");
        APP_SHEXIANGMEITU.put("com.lemon.faceu", "faceu");
    }

    public static final Map<String, String> APP_SHIPIN = new HashMap<String, String>();//分类：视频
    static{
        APP_SHIPIN.put("com.qiyi.video", "爱奇艺");
        APP_SHIPIN.put("com.tencent.qqlive", "腾讯视频");
        APP_SHIPIN.put("com.youku.phone", "优酷");
        APP_SHIPIN.put("com.ss.android.article.video", "西瓜视频");
        APP_SHIPIN.put("com.miui.video", "小米视频");
        APP_SHIPIN.put("com.letv.android.client", "乐视视频");
        APP_SHIPIN.put("com.sohu.sohuvideo", "搜狐视频");
        APP_SHIPIN.put("com.baidu.video", "百度视频");
        APP_SHIPIN.put("com.ss.android.ugc.aweme", "抖音");
        APP_SHIPIN.put("com.funshion.video.mobile", "风行视频");
        APP_SHIPIN.put("com.storm.smart", "暴风影音");
        APP_SHIPIN.put("com.ss.android.ugc.live", "火山小视频");
        APP_SHIPIN.put("com.meitu.meipaimv", "美拍");
        APP_SHIPIN.put("com.meelive.ingkee", "映客");
        APP_SHIPIN.put("com.yixia.videoeditor", "秒拍");
        APP_SHIPIN.put("com.smile.gifmaker", "快手");
        APP_SHIPIN.put("tv.danmaku.bili", "哔哩哔哩");
    }

    public static final Map<String, String> APP_YOUXI = new HashMap<String, String>();//分类：游戏
    static{
        APP_YOUXI.put("com.tencent.tmgp.sgame", "王者荣耀");
        APP_YOUXI.put("com.happyelements.AndroidAnimal", "开心消消乐");
        APP_YOUXI.put("com.qqgame.hlddz", "欢乐斗地主");
        APP_YOUXI.put("com.ztgame.bob", "球球大作战");
        APP_YOUXI.put("cn.jj", "jj斗地主");
        APP_YOUXI.put("com.tencent.qqgame.xq", "天天象棋");
    }

    public static final Map<String, String> APP_CHUXINGLEI = new HashMap<String, String>();//分类：出行类
    static{
        APP_CHUXINGLEI.put("com.ehai", "一嗨租车");
        APP_CHUXINGLEI.put("com.sdu.didi.psnger", "滴滴出行");
        APP_CHUXINGLEI.put("com.autonavi.minimap", "高德地图");
        APP_CHUXINGLEI.put("ctrip.android.view", "携程");
        APP_CHUXINGLEI.put("com.baidu.BaiduMap", "百度地图");
        APP_CHUXINGLEI.put("so.ofo.labofo", "OFO");
        APP_CHUXINGLEI.put("com.Qunar", "去哪");
        APP_CHUXINGLEI.put("com.taobao.trip", "飞猪旅行");
    }

    public static final Map<String, String> APP_GOUWU = new HashMap<String, String>();//分类：购物
    static{
        APP_GOUWU.put("me.ele", "饿了么");
        APP_GOUWU.put("com.taobao.taobao", "淘宝");
        APP_GOUWU.put("com.sankuai.meituan", "美团");
        APP_GOUWU.put("com.dianping.v1", "大众点评");
        APP_GOUWU.put("com.achievo.vipshop", "唯品会");
        APP_GOUWU.put("com.xunmeng.pinduoduo", "拼多多");
        APP_GOUWU.put("com.tmall.wireless", "天猫");
        APP_GOUWU.put("com.tuan800.tao800", "折800");
    }

    public static final Map<String, String> APP_YINLE = new HashMap<String, String>();//分类：音乐
    static{
        APP_YINLE.put("com.tencent.qqmusic", "QQ音乐");
        APP_YINLE.put("com.netease.cloudmusic", "网易云音乐");
        APP_YINLE.put("com.tencent.karaoke", "全民K歌");
        APP_YINLE.put("com.ximalaya.ting.android", "喜马拉雅FM");
        APP_YINLE.put("fm.xiami.main", "虾米音乐");
        APP_YINLE.put("com.changba", "唱吧");
        APP_YINLE.put("com.shoujiduoduo.ringtone", "铃声多多");
        APP_YINLE.put("fm.qingting.qtradio", "蜻蜓");
        APP_YINLE.put("cmccwm.mobilemusic", "咪咕音乐");
    }
    //自动生成完毕

    public static final Map<String, String> APP_STORE = new HashMap<String, String>();//分类：应用市场
    static{
        APP_STORE.put("com.baidu.appsearch", "百度");
        APP_STORE.put("com.huawei.appmarket", "华为");
        APP_STORE.put("com.oppo.market", "OPPO");
        APP_STORE.put("com.qihoo.appstore", "360");
        APP_STORE.put("com.bbk.appstore", "VIVO");
        APP_STORE.put("com.wandoujia.phoenix2", "豌豆荚");
        APP_STORE.put("com.xiaomi.market", "小米");
        APP_STORE.put("com.tencent.android.qqdownloader", "应用宝");
    }

    public static final Map<Map<String, String>, String> APP_CATEGORIS = new HashMap<Map<String, String>, String>();//所有分类的App
    static{
        APP_CATEGORIS.put(APP_SHEJIAOSHEQU, "社交社区");
        APP_CATEGORIS.put(APP_GONGJULEI, "工具类");
        APP_CATEGORIS.put(APP_YUEDU, "阅读");
        APP_CATEGORIS.put(APP_SHEXIANGMEITU, "摄像美图");
        APP_CATEGORIS.put(APP_SHIPIN, "视频");
        APP_CATEGORIS.put(APP_YOUXI, "游戏");
        APP_CATEGORIS.put(APP_CHUXINGLEI, "出行类");
        APP_CATEGORIS.put(APP_GOUWU, "购物");
        APP_CATEGORIS.put(APP_YINLE, "音乐");
        APP_CATEGORIS.put(APP_STORE, "应用市场");
    }



    public static final Map<String, String> APP_XPOSED_RELATED = new HashMap<String, String>();//xposed相关的App
    static{
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_XPOSED, "xposed");
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_ADBLOCKER, "AdBlocker:pl");
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_XPRIVACY, "XPrivacy");
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_MOBILE_FAKER, "Mobile Faker");
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_YING_YONG_BIAN_LIANG, "应用变量");
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_XMATOR, "XMator");
        APP_XPOSED_RELATED.put(APP_PACKAGE_NAME_XMATOR_TEST, "XMator test");
    }

    public static final Map<String, String> APP_XMATOR_RELATED = new HashMap<String, String>();//自己程序相关的
    static{
        //APP_XMATOR_RELATED.put(APP_PACKAGE_NAME_XMANAGER, "XManager");
        APP_XMATOR_RELATED.put(APP_PACKAGE_NAME_XMATOR, "XMator");
        APP_XMATOR_RELATED.put(APP_PACKAGE_NAME_XMATOR_TEST, "XMator test");
    }

    public static final Map<String, String> APP_SIMULATOR_RELATED = new HashMap<String, String>();//模拟器相关的app
    static{
        APP_SIMULATOR_RELATED.put("com.android.launcher", "TianTianLauncher");
        APP_SIMULATOR_RELATED.put("com.tiantian.ime", "天天输入法");
        APP_SIMULATOR_RELATED.put("com.kop.zkop", "TianTianAdbd");
        APP_SIMULATOR_RELATED.put("com.kaopu001.tiantianserver", "TiantianServer");
        APP_SIMULATOR_RELATED.put("com.thirdparty.superuser", "超级用户");
        APP_SIMULATOR_RELATED.put("com.cyjh.ikaopu", "靠谱游戏");
    }

    public static final Map<String, String> APP_ASO = new HashMap<String, String>();//可能刷的app 
    static{
        APP_ASO.put("com.imalljoy.wish", "微愿");
        APP_ASO.put("com.toycloud.SelfTimer17K", "自拍神器最靓");
        APP_ASO.put("com.motie.motiereader", "磨铁阅读");
        APP_ASO.put("com.yunqi.calendar", "运气日历");
        APP_ASO.put("com.kingsoft.calendar", "WPS日历");
        APP_ASO.put("com.tianxun.android.zh", "天巡旅行");
    }

    public static final Map<Map<String, String>, String> APP_SDK_ALL = new HashMap<Map<String, String>, String>();//所有第三方sdk
    static{
        APP_SDK_ALL.put(APP_SDK_JIGUANG, "极光");
        APP_SDK_ALL.put(APP_SDK_YIGUAN, "易观");
        APP_SDK_ALL.put(APP_SDK_AIRUI, "艾瑞");
        APP_SDK_ALL.put(APP_SDK_GETUI, "个推");
        APP_SDK_ALL.put(APP_SDK_XIAOMI, "小米");
        APP_SDK_ALL.put(APP_SDK_GEXIN, "个信");
        APP_SDK_ALL.put(APP_SDK_YOUMENG, "友盟");
    }

    public static List<String> PROXY_IPS = new ArrayList<String>();
    public static List<Integer> PROXY_PORTS = new ArrayList<Integer>();
    static{
        PROXY_IPS.add("192.168.1.211");

        PROXY_PORTS.add(2001);
        PROXY_PORTS.add(2002);
        PROXY_PORTS.add(2003);
        PROXY_PORTS.add(2004);
        PROXY_PORTS.add(2005);
        PROXY_PORTS.add(2006);
        PROXY_PORTS.add(2007);
        PROXY_PORTS.add(2008);
        PROXY_PORTS.add(2009);
        PROXY_PORTS.add(2010);
        PROXY_PORTS.add(2011);
        PROXY_PORTS.add(2012);
        PROXY_PORTS.add(2013);
        PROXY_PORTS.add(2014);
        PROXY_PORTS.add(2015);
    }
}
