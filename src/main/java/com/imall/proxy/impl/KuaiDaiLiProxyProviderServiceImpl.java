package com.imall.proxy.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.imall.common.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.imall.promotion.utils.HttpUtils;
import com.imall.proxy.IProxyProviderService;
import com.imall.proxy.Proxy;
import com.imall.proxy.ProxyConfig;

/**
 * 快代理的API接口类
 * 
 * @author jianxunji
 */
public class KuaiDaiLiProxyProviderServiceImpl implements IProxyProviderService {
	public static final Logger LOGGER = LoggerFactory.getLogger(KuaiDaiLiProxyProviderServiceImpl.class);
	
	public static final String SVIP_API = "http://svip.kuaidaili.com/api/getproxy/";
//	public static final String SVIP_ORDER_NUMBER = "947877538949212";
	
	public static final String PRIVATE_API = "http://dps.kuaidaili.com/api/getdps/";
//	public static final String PRIVATE_ORDER_NUMBER = "977981798705723";
	
	private ExecutorService executorService;
	
	public KuaiDaiLiProxyProviderServiceImpl() {
		super();
//		executorService = Executors.newCachedThreadPool();
		executorService = Executors.newFixedThreadPool(1000);
	}

	public List<Proxy> loadProxyFromProviderBak(int number, boolean forceLoadFromProvider) {
		return doLoadProxyFromLocal();
	}
	
	public synchronized List<Proxy> loadProxyFromProviderBak2(int number, boolean forceLoadFromProvider) {
		for(int i = 0; i < 150; i ++){
			if(this.checkProxyLoading()){
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				break;
			}
		}
		List<Proxy> proxies = new ArrayList<Proxy>();
		if(forceLoadFromProvider || this.checkShouldLoadProxy()){
			proxies = this.reloadProxyFromProviderServer(number);
		}else{
			proxies = this.doLoadProxyFromLocal();
		}
		return proxies;
	}
	
	@Override
	public synchronized List<Proxy> loadProxyFromProvider(int number) {
		List<Proxy> proxies = this.doLoadProxyFromLocal();
		if(proxies == null || proxies.isEmpty()){
			proxies = this.reloadProxyFromProviderServer(number);
		}else{
			this.checkReloadProxy(number);
		}
		return proxies;
	}

	@Override
    public synchronized List<Proxy> reloadProxyFromProviderServer(final int number) {
		return doLoadProxyFromServer(number);
	}
	
	@Override
	public void checkReloadProxy(final int number){
		if(this.checkShouldLoadProxy()){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
                    reloadProxyFromProviderServer(number);
				}
			});
		}
	}
	
	private synchronized List<Proxy> doLoadProxyFromServer(int number){
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < 300; i ++){
			if(this.checkProxyLoading()){
				ObjectUtils.waitForTime(300);
			}else{
				break;
			}
		}
		List<Proxy> proxies = new ArrayList<Proxy>();
		int i = 0;
		try {
			String pid = ProcessUtils.getCurrentPid();
			File proxyLockFile = new File(ProxyConfig.PROXY_LOCK_FILE);
			ResourceUtils.writeToFile(proxyLockFile, pid);
			Timestamp getTime = new Timestamp(System.currentTimeMillis());
			Timestamp invalidTime = new Timestamp(System.currentTimeMillis() + 
					ProxyConfig.PROXY_RELOAD_INTERVAL);
			//load proxy from server
			for(i = 0; i < 6; i ++){
				boolean withDuplicate = false;
				if(i > 1){
					withDuplicate = true;
					LOGGER.warn("连续{}次获取不到可用的代理，允许获取今天重复的IP", i);
				}
                //从这里选择获得代理IP的方式
//				proxies = this.loadProxyFromSVIP(number);
				proxies = this.loadProxyFromPrivate(number, withDuplicate);
				if(proxies != null && proxies.size() > 0){
					break;
				}
				ObjectUtils.waitForTime(300);
			}

            String allProxiesOnlyFile = ProxyConfig.PROXY_ALL_ONLY_DAILY_FILE + TimeUtils.getTimeString(new Timestamp(System.currentTimeMillis()), CommonConstants.DATE_FORMAT);
            List<String> allProxiesOnlyList = ResourceUtils.loadFile(allProxiesOnlyFile);
            TreeSet<String> allProxiesOnly = new TreeSet<String>(allProxiesOnlyList);

			StringBuilder proxyString = new StringBuilder(TimeUtils.getTimeString(getTime));
			proxyString.append("\n");
			proxyString.append(TimeUtils.getTimeString(invalidTime));
			if(proxies != null){
				proxyString.append("\n");
				proxyString.append(proxies.size());
				for(Proxy proxy : proxies){
					proxy.setGetTime(getTime);
					proxy.setInvalidTime(invalidTime);
					proxyString.append("\n");
					proxyString.append(proxy.toString());

					if(!allProxiesOnly.contains(proxy.toString())){
                        allProxiesOnlyList.add(proxy.toString());
                        allProxiesOnly.add(proxy.toString());
                    }
                }
			}
			ResourceUtils.writeToFile(new File(ProxyConfig.PROXY_LIST_FILE), proxyString.toString());

            String allProxiesFile = ProxyConfig.PROXY_ALL_DAILY_FILE + TimeUtils.getTimeString(new Timestamp(System.currentTimeMillis()), CommonConstants.DATE_FORMAT);
            String allProxies = ResourceUtils.loadFileToString(allProxiesFile);
			if (!StringUtils.isBlank(allProxies)){
				allProxies = allProxies + "\n\n";
			}else{
                allProxies = "";
            }
			allProxies = allProxies + proxyString.toString();
			ResourceUtils.writeToFile(new File(allProxiesFile), allProxies);

            ResourceUtils.writeToFile(new File(allProxiesOnlyFile), allProxiesOnlyList);

			proxyLockFile.delete();
			LOGGER.info("load proxies from kuaidaili complete for {} times, consumed: {} \n{}",
					i + 1, System.currentTimeMillis() - startTime, proxyString.toString());
		} catch (Exception e) {
			LOGGER.error("load proxies from kuaidaili error for " + (i + 1) + " times", e);
		}
		return proxies;
	}
	
	private List<Proxy> loadProxyFromSVIP(int number, boolean withDuplicate){
		//http://svip.kuaidaili.com/api/getproxy/?orderid=947877538949212&num=5&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_ha=1&quality=2&dedup=1&format=json&sep=1
		//http://svip.kuaidaili.com/api/getproxy/?orderid=947877538949212&num=100&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&quality=1&format=json&sep=1
		Map<String, String> params = new TreeMap<String, String>();
		params.put("orderid", ProxyConfig.KUAIDAILI_ORDER_NUMBER_SVIP);
		params.put("num", String.valueOf(number));
		params.put("b_pcchrome", String.valueOf(1));
		params.put("b_pcie", String.valueOf(1));
		params.put("b_pcff", String.valueOf(1));
		params.put("protocol", String.valueOf(1));
		params.put("method", String.valueOf(2));
		params.put("an_an", String.valueOf(1));
		params.put("an_ha", String.valueOf(1));
		params.put("quality", String.valueOf(1));//稳定程度：0 1 2
		params.put("area", "中国");
		if(!withDuplicate){
			params.put("dedup", String.valueOf(1));//过滤今天提取过的IP
		}
		params.put("f_loc", String.valueOf(1));
		params.put("f_an", String.valueOf(1));
		params.put("format", "json");
		params.put("sep", String.valueOf(1));
		return this.requestProxyFromServer(SVIP_API, params);
	}
	
	/**
	 * @param number
	 * @param withDuplicate 可以有今天重复的IP
	 * @return
	 */
	private List<Proxy> loadProxyFromPrivate(int number, boolean withDuplicate){
		//http://dps.kuaidaili.com/api/getdps/?orderid=1111&num=5&ut=1&dedup=1&format=json&sep=1
		Map<String, String> params = new TreeMap<String, String>();
		params.put("orderid", ProxyConfig.KUAIDAILI_ORDER_NUMBER_PRIVATE);
		params.put("num", String.valueOf(number));
		params.put("ut", String.valueOf(1));//稳定使用大于10分钟
		if(!withDuplicate){
			params.put("dedup", String.valueOf(1));//过滤今天提取过的IP
		}
		params.put("format", "json");
		params.put("sep", String.valueOf(1));
		
		return this.requestProxyFromServer(PRIVATE_API, params);
	}
	
	private List<Proxy> requestProxyFromServer(String url, Map<String, String> params){
		List<Proxy> proxies = new ArrayList<Proxy>();
//		String response = HttpUtils.getInstance(null, -1).get(url, null, params);
		String response = HttpClientUtils.get(url, null, params);
		if(!StringUtils.isEmpty(response) && response.indexOf("{") == 0){
			Map<String, Object> map = JsonUtils.fromJsonToMap(response);
			if(map.containsKey("data") && map.get("data") instanceof Map){
				Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
				if(dataMap.containsKey("proxy_list") && dataMap.get("proxy_list") instanceof List){
					List<String> proxyList = (List<String>) dataMap.get("proxy_list");
					if(proxyList != null && proxyList.size() > 0){
						for(String proxyString : proxyList){
							if(!StringUtils.isEmpty(proxyString)){
								Proxy proxy = Proxy.parseFromString(proxyString.split(",")[0]);
								if(proxy != null){
									proxies.add(proxy);
								}
							}
						}
					}
				}
			}
		}
		LOGGER.info("load proxies from: {}, {}", url, JsonUtils.fromObjectToJson(proxies));
		return proxies;
	}
	
	private List<Proxy> doLoadProxyFromLocal(){
		List<Proxy> proxies = new ArrayList<Proxy>();
		List<String> proxyContent = null;
		try {
			File proxyListFile = new File(ProxyConfig.PROXY_LIST_FILE);
			if(proxyListFile.exists()){
				proxyContent = FileUtils.readLines(proxyListFile, "UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(proxyContent != null && proxyContent.size() > 4){
			Timestamp getTime = TimeUtils.getTimestamp(proxyContent.get(0));
			Timestamp invalidTime = TimeUtils.getTimestamp(proxyContent.get(1));
			for(int index = 3; index < proxyContent.size(); index ++){
				Proxy proxy = Proxy.parseFromString(proxyContent.get(index));
				if(proxy != null){
					proxy.setGetTime(getTime);
					proxy.setInvalidTime(invalidTime);
					proxies.add(proxy);
				}
			}
		}
		return proxies;
	}
	
	private boolean checkProxyLoading(){
		File proxyLockFile = new File(ProxyConfig.PROXY_LOCK_FILE);
		if(proxyLockFile.exists()){
			String pidFileContent = null;
			try {
				pidFileContent = FileUtils.readFileToString(new File(ProxyConfig.PROXY_LOCK_FILE));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!StringUtils.isEmpty(pidFileContent)){
				if(ProcessUtils.checkPidExist(pidFileContent)){
					return true;
				}else{
					proxyLockFile.delete();
				}
			}
		}
		return false;
	}
	
	private boolean checkShouldLoadProxy(){
		List<String> proxyContent = null;
		try {
			proxyContent = FileUtils.readLines(new File(ProxyConfig.PROXY_LIST_FILE), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(proxyContent != null && proxyContent.size() > 4){
			Timestamp invalidTime = TimeUtils.getTimestamp(proxyContent.get(1));
			if(invalidTime != null && invalidTime.getTime() > System.currentTimeMillis()){
				Integer number = Integer.valueOf(proxyContent.get(2));
				if(number != null && number > 0){
					return false;
				}
			}
		}
		return true;
	}
	
}
