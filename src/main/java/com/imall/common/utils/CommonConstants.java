package com.imall.common.utils;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * @author jianxunji
 *
 */
public class CommonConstants {
	public static final Pattern CONNECT_MATCHER = Pattern.compile("^connect.*", Pattern.CASE_INSENSITIVE);

	public static final long SECOND_M_S = 1000L;
	public static final long MINIUTE_M_S = 60L * SECOND_M_S;
	public static final long HOUR_M_S = 60L * MINIUTE_M_S;
	public static final long DAY_M_S = 24L * HOUR_M_S;

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String S_DATE_FORMAT = "yyyyMMdd";
	public static final String MONTH_DATE_FORMAT = "yyyy-MM";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_STAMP_FORMAT_HOUR = "yyyy-MM-dd HH";
	public static final String TIME_STAMP_FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
	public static final String TIME_STAMP_GMT_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final String TIME_STAMP_UTC_FORMAT = "MMM dd, yyyy hh:mm a ZZZ";
	public static final String TIME_STAMP_ENGLISH_FORMAT = "MMM d, yyyy K:m:s a";
	public static final String TIME_STAMP_ENGLISH = "MMM-dd-yyyy K:m:s a";
	public static final String TIME_STAMP_FULL_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	public static final String DATE_FORMAT_CHINESE = "yyyy年MM月dd日";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT);
    public static final SimpleDateFormat SIMPLE_TIME_STAMP_FORMAT = new SimpleDateFormat(TIME_STAMP_FORMAT);
    
    public static final String IMAGE_SUFFIX_100_WIDTH = "@!100width";
//    public static final String IMAGE_SUFFIX_100_WIDTH = "@!100width.jpg";
//    public static final String IMAGE_SUFFIX_100_WIDTH = "@!100width.png";
	public static final String IMAGE_SUFFIX_200_WIDTH = "@!200width";
//	public static final String IMAGE_SUFFIX_200_WIDTH = "@!200width.jpg";
//	public static final String IMAGE_SUFFIX_200_WIDTH = "@!200width.png";
	public static final String IMAGE_SUFFIX_300_WIDTH = "@!300width";
	public static final String IMAGE_SUFFIX_400_WIDTH = "@!400width";
	public static final String IMAGE_SUFFIX_500_WIDTH = "@!500width";
	public static final String IMAGE_SUFFIX_600_WIDTH = "@!600width";
	public static final String IMAGE_SUFFIX_750_WIDTH = "@!750width";
	public static final String IMAGE_SUFFIX_1000_WIDTH = "@!1000width";
	public static final String IMAGE_SUFFIX_2000_WIDTH = "@!2000width";
	
	public static final String XMPP_USER_SERVICE_NAME = "imalljoy.com";
	public static final String XMPP_GROUP_SERVICE_NAME = "conference.imalljoy.com";
	
	public static final String XMPP_RESOURCE_NAME = "wish_server";
	
	public static final String THIRD_PARTY_USER_PASSWORD = "3rd_party_pass";
	public static final String FAKE_USER_PASSWORD = "aimao123";
	
    public static final Integer CHAT_GROUP_MAX_USER_SIZE = 19;
    
	//public static final String STATIC_SERVER_ROOT = "http://imall-app.oss-cn-beijing.aliyuncs.com/";
	public static final String STATIC_SERVER_ROOT = "http://cdn.imalljoy.com/";
	public static final String STATIC_SERVER_ROOT_2 = "http://cdn.appweiyuan.com/";
	public static final String STATIC_SERVER_ROOT_HTTPS = "https://cdn.appweiyuan.com/";

	public static final String WISH_WEB_URL = "http://www.appweiyuan.com/";
	public static final String WISH_SHARE_URL = "http://www.makeweiyuan.com/";
//	public static final String WISH_SHARE_URL = WISH_WEB_URL;
//	public static final String WISH_WECHAT_REDIRECT_URL = WISH_SHARE_URL;

	public static final String WISH_TEST_WEB_URL = "http://test.appweiyuan.com/web/";
	public static final String WISH_TEST_SHARE_URL = "http://test.makeweiyuan.com/web/";
//	public static final String WISH_TEST_SHARE_URL = WISH_TEST_WEB_URL;
	public static final String WISH_TEST_WECHAT_REDIRECT_URL = WISH_TEST_SHARE_URL;

	public static final String WISH_WEB_REDIRECT_URL_WEIBO = WISH_SHARE_URL;
	public static final String WISH_WEB_REDIRECT_URL_WECHAT = WISH_SHARE_URL;
	public static final String WISH_WEB_REDIRECT_URL_QQ = WISH_SHARE_URL;
	public static final String WISH_WEB_REDIRECT_URL_WEIBO_TEST = WISH_TEST_SHARE_URL;//应该可以跳到测试服务器
	public static final String WISH_WEB_REDIRECT_URL_WECHAT_TEST = WISH_TEST_SHARE_URL;//跳不过去，需要修改后台的配置
	public static final String WISH_WEB_REDIRECT_URL_QQ_TEST = WISH_TEST_SHARE_URL;//跳不过去，需要修改后台的配置
	
	public static final String WISH_M_URL = "http://m.appweiyuan.com/";
	public static final String WISH_TEST_M_URL = "http://test.appweiyuan.com/imall/";
	
	public static final long TOKEN_EXPIRES_IN = 10000 * DAY_M_S;
	
	
	public static final HashSet<String> RONI_USER_EMAIL = new HashSet<String>() {
		{
			add("xmbr14@sina.com");add("ev2278@sina.com");add("sb8867@sina.com");add("er2219@sina.com");
			add("lkqlw5@sina.com");add("qe4608@sina.com");add("al3643@sina.com");add("dvrz77@sina.com");
			add("rb7981@sina.com");add("bpxbj9@sina.com");add("cfak51@sina.com");add("juxu18@sina.com");
			add("ab7429@sina.com");add("xndb70@sina.com");add("xzkv78@sina.com");add("ohaw81@sina.com");
			add("dw7488@sina.com");add("braet9@sina.com");add("xnrk71@sina.com");add("sr8292@sina.com");
			add("mcwf65@sina.com");add("fe4086@sina.com");add("de7586@sina.com");add("rxmjo2@sina.com");
			add("sjrjk6@sina.com");add("chja74@sina.com");add("vpmts7@sina.com");add("enfvr8@sina.com");
			add("iyimc4@sina.com");add("alggj0@sina.com");add("yryg25@sina.com");add("obmq70@sina.com");
			add("xxyua3@sina.com");add("owexu0@sina.com");add("zxqot7@sina.com");add("obiho7@sina.com");
			add("os4501@sina.com");add("dwap85@sina.com");add("xpypv4@sina.com");add("mxgeo4@sina.com");
			add("lyxvx3@sina.com");add("fibs34@sina.com");add("vsii97@sina.com");add("mzct96@sina.com");
			add("bcsjy9@sina.com");add("ggma64@sina.com");add("jayu95@sina.com");add("cq6046@sina.com");
			add("hfyth0@sina.com");add("ic5534@sina.com");add("veasb6@sina.com");add("rbce53@sina.com");
			add("uacd44@sina.com");add("fv5617@sina.com");add("lhdc75@sina.com");add("tvmk43@sina.com");
			add("tbhq86@sina.com");add("qlixe7@sina.com");add("mhbe44@sina.com");add("go0330@sina.com");
			add("bhmkl0@sina.com");add("qode52@sina.com");add("ny9283@sina.com");add("ehtnp0@sina.com");
			add("zivpl4@sina.com");add("yw2224@sina.com");add("ckfjd7@sina.com");add("pxatn9@sina.com");
			add("lttjj5@sina.com");add("mwuqb2@sina.com");add("kd1172@sina.com");add("oq6693@sina.com");
			add("uabe08@sina.com");add("wuimp8@sina.com");add("tf5357@sina.com");add("cmmfh3@sina.com");
			add("rytkv6@sina.com");add("flzq58@sina.com");add("hrezi2@sina.com");add("uv4596@sina.com");
			add("ghac12@sina.com");add("yokso0@sina.com");add("trzeq3@sina.com");add("lpdn77@sina.com");
			add("lvmke1@sina.com");add("loyp70@sina.com");add("we7420@sina.com");add("mjtld2@sina.com");
			add("myye64@sina.com");add("dn7343@sina.com");add("whsng3@sina.com");add("nw8896@sina.com");
			add("skypm0@sina.com");add("hhpdp9@sina.com");add("tcyij1@sina.com");add("hldhi4@sina.com");
			add("cire74@sina.com");add("mu9416@sina.com");add("dwsyg5@sina.com");add("mbpm78@sina.com");
			add("dscb24@sina.com");add("yfjfj3@sina.com");add("kpvys3@sina.com");add("jp4838@sina.com");
			add("zvdhv1@sina.com");add("ptjbp1@sina.com");add("tvhy44@sina.com");add("vtfh19@sina.com");
			add("zopvr1@sina.com");add("eazxy7@sina.com");add("msfef3@sina.com");add("ioyj90@sina.com");
			add("rzrl37@sina.com");add("fe7259@sina.com");add("acjba4@sina.com");
			

		}
	};
}
