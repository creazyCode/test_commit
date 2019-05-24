package com.imall.proxy.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.imall.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imall.proxy.IProxyProviderService;
import com.imall.proxy.IProxyService;
import com.imall.proxy.Proxy;
import com.imall.proxy.ProxyConfig;

public class ProxyServiceImpl implements IProxyService {
	public static final Logger LOGGER = LoggerFactory.getLogger(ProxyServiceImpl.class);

	private static ProxyServiceImpl instance;
	
	private IProxyProviderService proxyProviderService;
	
	private List<Proxy> availableProxies = new ArrayList<Proxy>();
	
	/**
	 * 保存测试成功过的proxy，以及测试的时间
	 */
	private Map<String, Long> testedProxy = new ConcurrentHashMap<String, Long>();

    /**
     * 本地缓存的文件，可以不通过proxy，直接在本地下载
     */
    private Map<String, Long> cacheFiles = new ConcurrentHashMap<String, Long>();
    private Long flushCacheRecordTime = 0L;

	private long getClientIpTime = 0;
	private String clientIp;//真正的IP

	private ExecutorService executorService;

	private long checkReloadProxyTime = 0;
	
	public static ProxyServiceImpl getInstance(){
		if(instance == null){
			instance = new ProxyServiceImpl();
			instance.proxyProviderService = new KuaiDaiLiProxyProviderServiceImpl();

			try{
                instance.initClientIp();
            }catch (Exception e){
			    e.printStackTrace();
            }
            try{
                instance.loadProxyCacheRecord();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                instance.checkCacheStatus();
            }catch (Exception e){
                e.printStackTrace();
            }
		}
		return instance;
	}

	private void initClientIp(){
		this.clientIp = HttpClientUtils.getClientIp(null, -1);
		this.getClientIpTime = System.currentTimeMillis();
	}
	
	private ProxyServiceImpl(){
		super();
		//		executorService = Executors.newCachedThreadPool();
		executorService = Executors.newFixedThreadPool(1000);
	}
	
	@Override
	public List<Proxy> getAllAvailableProxies() {
		if(this.availableProxies == null || this.availableProxies.size() == 0 || !this.availableProxies.get(0).isValid()){
//		if(this.availableProxies == null || this.availableProxies.size() == 0){
			this.forceToLoadProxies();
		}
		return this.availableProxies;
	}

	/**
	 * 检查是否需要reload proxy
	 */
	private synchronized void checkReloadProxy(){
		if (System.currentTimeMillis() - this.checkReloadProxyTime > CommonConstants.SECOND_M_S * 30){
			this.checkReloadProxyTime = System.currentTimeMillis();
			if(this.availableProxies == null || this.availableProxies.size() == 0 || !this.availableProxies.get(0).isValid()){
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						availableProxies = proxyProviderService.reloadProxyFromProviderServer(ProxyConfig.PROXY_LOAD_NUMBER_ONCE);
                        resetAllProxyServerProxyIps();
					}
				});
			}
		}
	}

	@Override
	public void forceToLoadProxies(){
		this.availableProxies = new ArrayList<Proxy>(proxyProviderService.loadProxyFromProvider(ProxyConfig.PROXY_LOAD_NUMBER_ONCE));
		resetAllProxyServerProxyIps();
		this.checkReloadProxyTime = System.currentTimeMillis();
	}

	private void resetAllProxyServerProxyIps(){
		if(!this.availableProxies.isEmpty()){
			ProxyConfig.PROXY_SERVER_PORT_IPS.clear();
			if(this.availableProxies.size() >= ProxyConfig.PROXY_SERVER_PORTS.size()){
				for(int index = 0; index < ProxyConfig.PROXY_SERVER_PORTS.size(); index ++){
					Integer port = ProxyConfig.PROXY_SERVER_PORTS.get(index);
					ProxyConfig.PROXY_SERVER_PORT_IPS.put(port, this.availableProxies.get(index));
				}
			}else{
				for(int index = 0; index < ProxyConfig.PROXY_SERVER_PORTS.size(); index ++){
					Integer port = ProxyConfig.PROXY_SERVER_PORTS.get(index);
					Proxy randomProxy = this.availableProxies.get(ObjectUtils.getRandom(0, this.availableProxies.size() - 1));
					ProxyConfig.PROXY_SERVER_PORT_IPS.put(port, randomProxy);
				}
			}
		}
	}

	private void resetProxyServerProxyIp(Proxy proxy, Proxy newProxy){
		if(ProxyConfig.PROXY_SERVER_PORT_IPS.isEmpty()){
			return;
		}
		for(Integer port : ProxyConfig.PROXY_SERVER_PORTS){
			if (proxy.equals(ProxyConfig.PROXY_SERVER_PORT_IPS.get(port))){
				if(newProxy == null){
					if(!this.availableProxies.isEmpty()){
						newProxy = this.availableProxies.get(ObjectUtils.getRandom(0, this.availableProxies.size() - 1));
					}
				}
				if(newProxy != null){
					ProxyConfig.PROXY_SERVER_PORT_IPS.put(port, newProxy);
				}else{
					ProxyConfig.PROXY_SERVER_PORT_IPS.remove(port);
				}
			}
		}

	}

	@Override
	public synchronized void forceToVerifyAllProxies(){
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				int size = 0;
				if(availableProxies != null){
					size = availableProxies.size();
					Iterator<Proxy> iter = availableProxies.iterator();
					while(iter.hasNext()){
						Proxy proxy = iter.next();
						if(!checkProxyAvailable(proxy, true)){
							iter.remove();
							resetProxyServerProxyIp(proxy, null);
						}
					}
				}
				LOGGER.info("force to verify all proxies, available {} in {}", availableProxies.size(), size);
				getAllAvailableProxies();
			}
		});
	}

	@Override
	public Proxy getFixedProxy(){
		return getProxy(false);
	}

	@Override
	public Proxy getProxyForPort(int port){
		Proxy proxy = ProxyConfig.PROXY_SERVER_PORT_IPS.get(port);
		while (!this.checkProxyAvailable(proxy, false)){
		    if (proxy != null){
                LOGGER.warn("proxy does not work: {} for port: {}, removed", proxy.toString(), port);
            }else{
                LOGGER.warn("there is no proxy for port: {}", port);
            }
			synchronized (this.availableProxies){
                Proxy newProxy = this.getRandomProxy();
                if (proxy != null){
                    this.availableProxies.remove(proxy);
                    if (newProxy != null){
                        resetProxyServerProxyIp(proxy, newProxy);
                    }
                }
				proxy = newProxy;
				if(proxy == null){
					break;
				}
			}
		}
		LOGGER.info("get proxy: {} for port: {}", proxy == null ? "" : proxy.toString(), port);
		checkReloadProxy();
		return proxy;
	}

	@Override
	public Proxy getRandomProxy() {
		return getProxy(true);
	}

	private Proxy getProxy(boolean isRandom){
		Proxy proxy = null;
		for(int i = 0; i < 3; i ++){
			List<Proxy> proxies = this.getAllAvailableProxies();
			if(proxies != null && proxies.size() > 0){
				proxy = this.doGetProxy(proxies, isRandom);
				if(proxy != null){
					break;
				}
			}
			ObjectUtils.waitForTime(300);
		}
		if(proxy == null){
			LOGGER.warn("sorry, there is no available proxy");
		}else{
			LOGGER.debug("get proxy successfully: {}, random: {}", proxy.toString(), isRandom);
		}
		checkReloadProxy();
		return proxy;
	}
	
	private Proxy doGetProxy(List<Proxy> proxies, boolean isRandom){
		Proxy proxy = null;
		List<Proxy> proxiesWithoutUnavailable = new ArrayList<Proxy>(proxies);
		while(proxiesWithoutUnavailable.size() > 0){
			if(isRandom){
				proxy = proxiesWithoutUnavailable.get(ObjectUtils.getRandom(0, proxiesWithoutUnavailable.size() - 1));
			}else{
				proxy = proxiesWithoutUnavailable.get(0);
			}
//			if(proxiesWithoutUnavailable.size() <= 1){
//				break;
//			}
			//有多个的时候才去check可用性，否则直接返回了
			if(this.checkProxyAvailable(proxy, false)){
				break;
			}else{
				LOGGER.warn("proxy does not work: {}, removed", proxy.toString());
				proxiesWithoutUnavailable.remove(proxy);
				synchronized (this.availableProxies){
					this.availableProxies.remove(proxy);
					resetProxyServerProxyIp(proxy, null);
				}
				proxy = null;
			}
		}
		return proxy;
	}

	@Override
	public boolean checkProxyAvailable(Proxy proxy, boolean forceTest) {
		if(proxy == null){
			return false;
		}
		boolean success = true;
		String key = proxy.toString();
		Long testedTime = this.testedProxy.get(key);
		long consumedTime = 0;
		if(testedTime == null || (forceTest && System.currentTimeMillis() - testedTime > CommonConstants.SECOND_M_S * 3)
				|| System.currentTimeMillis() - testedTime > ProxyConfig.PROXY_TEST_INTERVAL){
			String nowClientIp = null;
			long startTime = System.currentTimeMillis();
            nowClientIp = HttpClientUtils.getClientIp(proxy.getHost(), proxy.getPort());
            if(StringUtils.isBlank(nowClientIp)){
                success = false;
            }else if (this.getClientIp().equals(nowClientIp)){
                LOGGER.warn("代理服务器：[{}]是透明代理，测试不通过", proxy.toString());
                success = false;
            }
            if (success) {
                consumedTime = System.currentTimeMillis() - startTime;
                if (consumedTime > ProxyConfig.PROXY_TEST_MAX_AVAILABLE_RESPONSE_TIME) {
                    success = false;
                }
            }
			if(success){
				this.testedProxy.put(key, System.currentTimeMillis());
			}else{
				this.testedProxy.remove(key);
			}
			LOGGER.info("test proxy: {}:{}, result: {}, consumed: {}, real IP: {}",
					proxy.getHost(), proxy.getPort(), success, consumedTime, nowClientIp);
		}else{
			LOGGER.debug("tested {} before at {}, do not need to test again", key, TimeUtils.getTimeString(new Timestamp(testedTime)));
			if (ObjectUtils.checkPossibility(0.05)){
				LOGGER.info("tested {} before at {}, do not need to test again", key, TimeUtils.getTimeString(new Timestamp(testedTime)));
			}
		}
		return success;
	}

    public synchronized boolean checkProxyAvailableBak(Proxy proxy, boolean forceTest) {
        if(proxy == null){
            return false;
        }
        boolean success = true;
        String key = proxy.toString();
        Long testedTime = this.testedProxy.get(key);
        long consumedTime = 0;
        if(testedTime == null || (forceTest && System.currentTimeMillis() - testedTime > CommonConstants.SECOND_M_S * 3)
                || System.currentTimeMillis() - testedTime > ProxyConfig.PROXY_TEST_INTERVAL){
            String nowClientIp = null;
            long startTime = System.currentTimeMillis();
            success = HttpClientUtils.testProxy(proxy.getHost(), proxy.getPort());
            consumedTime = System.currentTimeMillis() - startTime;
            if(consumedTime > ProxyConfig.PROXY_TEST_MAX_AVAILABLE_RESPONSE_TIME){
                success = false;
            }
            if(success){
                nowClientIp = HttpClientUtils.getClientIp(proxy.getHost(), proxy.getPort());
                if (this.getClientIp().equals(nowClientIp)){
                    LOGGER.warn("代理服务器：[{}]是透明代理，测试不通过", proxy.toString());
                    success = false;
                }
            }
            if(success){
                this.testedProxy.put(key, System.currentTimeMillis());
            }else{
                this.testedProxy.remove(key);
            }
            LOGGER.info("test proxy: {}:{}, result: {}, consumed: {}, real IP: {}",
                    proxy.getHost(), proxy.getPort(), success, consumedTime, nowClientIp);
        }else{
            LOGGER.debug("tested {} before at {}, do not need to test again", key, TimeUtils.getTimeString(new Timestamp(testedTime)));
            if (ObjectUtils.checkPossibility(0.05)){
                LOGGER.info("tested {} before at {}, do not need to test again", key, TimeUtils.getTimeString(new Timestamp(testedTime)));
            }
        }
        return success;
    }

	public String getClientIp(){
		if (StringUtils.isBlank(this.clientIp)){
			this.initClientIp();
		}
		return this.clientIp;
	}

    @Override
    public boolean saveProxyCache(final String url, final byte[] bytes){
	    if(bytes != null && bytes.length > 0){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    doSaveProxyCache(url, bytes);
                }
            });
        }
        return true;
    }

    private boolean doSaveProxyCache(String url, byte[] bytes){
        boolean success = false;
        url = ObjectUtils.getUrlWithoutParameter(url);
        String fileName = this.getCacheFileName(url);
        if(!StringUtils.isBlank(fileName)){
            File file = new File(fileName);
            ResourceUtils.writeToFile(file, bytes);
            file = new File(fileName);
            if (file.exists()){
                this.addToProxyCache(url);
                success = true;
            }
        }
        LOGGER.info("cache: save result: {} to cache file: {} of url: {}, file size: {}", success, fileName, url, bytes.length);
        return success;
    }

    @Override
    public String getCacheFileName(String url){
        try {
            url = ObjectUtils.getUrlWithoutParameter(url);
            if(!url.contains("http")){
                url = "https://" + url;
            }
            URL urlObject = new URL(url);
            return ProxyConfig.PROXY_CACHE_FOLDER + urlObject.getHost()
                    + urlObject.getPath().replace("/", CommonConstants.FILE_SEPARATOR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean existInProxyCache(String url){
        url = ObjectUtils.getUrlWithoutParameter(url);
        if(this.cacheFiles.containsKey(url)){
            Long time = this.cacheFiles.get(url);
            if(isUrlCacheValid(url, time)){
                return true;
            }else{
                removeFromProxyCache(url);
            }
        }
        return false;
    }

    private boolean isUrlCacheValid(String url, Long time){
        boolean valid = false;
        if(time != null){
            if(System.currentTimeMillis() - time < ProxyConfig.PROXY_CACHE_EXPIRE_INTERVAL_APK
                    || (!url.toLowerCase().contains(".apk") && System.currentTimeMillis() - time < ProxyConfig.PROXY_CACHE_EXPIRE_INTERVAL_IMAGE)){
                valid = true;
            }
        }
        return valid;
    }

    private void flushProxyCacheRecord(){
        if(System.currentTimeMillis() - flushCacheRecordTime > 1 * CommonConstants.MINIUTE_M_S){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    doFlushProxyCacheRecord();
                }
            });
        }
    }

    /**
     * 把内存中的缓存记录flush到磁盘中
     */
    private synchronized void doFlushProxyCacheRecord(){
        this.flushCacheRecordTime = System.currentTimeMillis();

        List<String> cacheRecords = new ArrayList<String>();
        Set<String> keys = this.cacheFiles.keySet();
        for(String url : keys){
            cacheRecords.add(url + "," + TimeUtils.getTimeString(new Timestamp(this.cacheFiles.get(url))));
        }
        ResourceUtils.writeToFile(new File(ProxyConfig.PROXY_CACHE_RECORD_FILE), cacheRecords);
        LOGGER.info("cache: flush proxy cache record to file: {}", ProxyConfig.PROXY_CACHE_RECORD_FILE);
    }

    public synchronized void loadProxyCacheRecord(){
        Map<String, Long> temp = getAllCacheRecordFromFile();
        this.cacheFiles.clear();
        this.cacheFiles = temp;

        //加载完数据以后，认为也保存过数据了
        this.flushCacheRecordTime = System.currentTimeMillis();
        LOGGER.info("cache: load proxy cache record from file: {}, number: {}", ProxyConfig.PROXY_CACHE_RECORD_FILE, this.cacheFiles.size());
    }

    private Map<String, Long> getAllCacheRecordFromFile(){
        Map<String, Long> temp = new ConcurrentHashMap<>();
        List<String> cacheRecords = ResourceUtils.loadFile(ProxyConfig.PROXY_CACHE_RECORD_FILE);
        if(cacheRecords != null && cacheRecords.size() > 0) {
            for (String record : cacheRecords) {
                try{
                    String[] str = record.split(",");
                    Timestamp time = TimeUtils.getTimestamp(str[1]);
                    if(time != null){
                        temp.put(str[0], time.getTime());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return temp;
    }

	/**
	 * 检查所有缓存是否到期
	 */
	public void checkCacheStatus(){
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				doCheckCacheStatus();
			}
		});
	}

	private synchronized void doCheckCacheStatus(){
        Map<String, Long> temp = getAllCacheRecordFromFile();
        for(String url : temp.keySet()){
            Long time = temp.get(url);
            if(!isUrlCacheValid(url, time)){
                doRemoveFromProxyCache(url, true);
            }
        }
	}

    @Override
    public boolean addToProxyCache(String url){
        url = ObjectUtils.getUrlWithoutParameter(url);
        this.cacheFiles.put(url, System.currentTimeMillis());
        flushProxyCacheRecord();
        return true;
    }

    @Override
    public boolean removeFromProxyCache(String url){
        return doRemoveFromProxyCache(url, false);
    }

    private boolean doRemoveFromProxyCache(String url, boolean updateFile){
        url = ObjectUtils.getUrlWithoutParameter(url);
        long size = 0L;
        Long time = null;
        String fileName = this.getCacheFileName(url);
        if(this.cacheFiles.containsKey(url)){
            time = this.cacheFiles.remove(url);
            if(updateFile){
                flushProxyCacheRecord();
                File file = new File(fileName);
                size = file.length();
                if(file.exists()){
                    file.delete();
                }else{
                    LOGGER.warn("cache: remove warning: cache of url: {} does not exist in file: {}", url, fileName);
                }
            }
        }else{
            LOGGER.warn("cache: remove warning: cache of url: {} does not exist in memory", url);
        }
        LOGGER.info("cache: remove cache from local file {}: {}, {} of request: {}, {}", updateFile ? "sync" : "async", fileName,
                size, url, time == null ? "null" : TimeUtils.getTimeString(new Timestamp(time)));
        return true;
    }

    @Override
    public byte[] getProxyCache(String url){
        byte[] bytes = null;
        url = ObjectUtils.getUrlWithoutParameter(url);
        if(this.existInProxyCache(url)){
            bytes = ResourceUtils.loadFileToByteArray(this.getCacheFileName(url));
        }
        return bytes;
    }
}