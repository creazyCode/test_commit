package com.imall.proxy;

import com.imall.common.utils.CommonConstants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jianxunji
 *
 */
public class ProxyConfig {
	private static final String PROXY_LIST_FILE_NAME = "proxy_list.txt";
    private static final String PROXY_LOCK_FILE_NAME = "proxy_lock.pid";
    private static final String PROXY_ALL_DAILY_FILE_NAME = "all_proxies_";
    private static final String PROXY_ALL_ONLY_DAILY_FILE_NAME = "all_proxies_only_";
    private static final String PROXY_CACHE_FOLDER_NAME = "cache";
    private static final String PROXY_CACHE_RECORD_FILE_NAME = "cache_records.txt";

	public static String PROXY_FOLDER = "/Users/jason/temp/proxy/";
	public static String PROXY_LIST_FILE = PROXY_FOLDER + PROXY_LIST_FILE_NAME;
	public static String PROXY_LOCK_FILE = PROXY_FOLDER + PROXY_LOCK_FILE_NAME;
	/**
	 * 某一天获得的所有代理
	 */
	public static String PROXY_ALL_DAILY_FILE = PROXY_FOLDER + PROXY_ALL_DAILY_FILE_NAME;
    /**
     * 某一天获得的所有代理，去重并且只剩下代理
     */
	public static String PROXY_ALL_ONLY_DAILY_FILE = PROXY_FOLDER + PROXY_ALL_ONLY_DAILY_FILE_NAME;

	public static boolean PROXY_CACHE_USE = false;
    public static String PROXY_CACHE_FOLDER = PROXY_FOLDER + PROXY_CACHE_FOLDER_NAME + CommonConstants.FILE_SEPARATOR;
    public static String PROXY_CACHE_URL = "http://192.168.1.188/proxy/cache/";

    /**
     * 记录cache的保存情况，格式为：url, time, file name
     */
    public static String PROXY_CACHE_RECORD_FILE = PROXY_FOLDER + PROXY_CACHE_RECORD_FILE_NAME;

	public static String KUAIDAILI_ORDER_NUMBER_SVIP = "947877538949212";
	public static String KUAIDAILI_ORDER_NUMBER_PRIVATE = "977981798705723";
	
	public static int PROXY_LOAD_NUMBER_ONCE = 10;
    /**
     * 5分钟10个IP
     */
    public static long PROXY_RELOAD_INTERVAL = CommonConstants.MINIUTE_M_S * 5;
    /**
     * 2分钟测试一下该ip是否可用
     */
	public static long PROXY_TEST_INTERVAL = CommonConstants.MINIUTE_M_S * 2;
    /**
     * 一个proxy启动的线程数
     */
	public static int PROXY_WORKERS_NUMBER = 300;
    /**
     * 3秒之内返回结果，认为代理可用，否则不可用
     */
	public static long PROXY_TEST_MAX_AVAILABLE_RESPONSE_TIME = CommonConstants.SECOND_M_S * 3;

    /**
     * cache的过期时间
     */
    public static double PROXY_CACHE_EXPIRE_INTERVAL_IMAGE = 1.0 * CommonConstants.DAY_M_S;
    public static double PROXY_CACHE_EXPIRE_INTERVAL_APK = 1.0 * CommonConstants.HOUR_M_S;

    public static final List<Integer> PROXY_SERVER_PORTS = new CopyOnWriteArrayList<Integer>();
    /**
     * 保存哪个port的server使用哪个代理
     */
	public static final Map<Integer, Proxy> PROXY_SERVER_PORT_IPS = new ConcurrentHashMap <Integer, Proxy>();

    public final static long PROXY_CACHE_MIN_SIZE_APK = 3000 * 1000;//cache中可以保存的apk文件的最小值
    public final static long PROXY_CACHE_MIN_SIZE_NONE_APK = 10 * 1000;//cache中可以保存的除了apk以外的静态文件的最小值

	public static void resetProxyFolder(String newProxyFolder){
		PROXY_FOLDER = newProxyFolder;
		PROXY_LIST_FILE = PROXY_FOLDER + PROXY_LIST_FILE_NAME;
		PROXY_LOCK_FILE = PROXY_FOLDER + PROXY_LOCK_FILE_NAME;
		PROXY_ALL_DAILY_FILE = PROXY_FOLDER + PROXY_ALL_DAILY_FILE_NAME;
        PROXY_ALL_ONLY_DAILY_FILE = PROXY_FOLDER + PROXY_ALL_ONLY_DAILY_FILE_NAME;

        PROXY_CACHE_FOLDER = PROXY_FOLDER + PROXY_CACHE_FOLDER_NAME + CommonConstants.FILE_SEPARATOR;
        PROXY_CACHE_RECORD_FILE = PROXY_FOLDER + PROXY_CACHE_RECORD_FILE_NAME;
	}
}
