package com.imall.proxy;

import java.util.List;

public interface IProxyService {
	/**
	 * 获得一个随机Proxy
	 */
	public List<Proxy> getAllAvailableProxies();

	/**
	 * 强制load新的proxy from server
	 */
	public void forceToLoadProxies();

	/**
	 * 强制测试所有的proxies，如果有不能用的则删除，如果都不能用则重新获取
	 */
	public void forceToVerifyAllProxies();
	
	/**
	 * 在一定期限之内获得一个固定的Proxy
	 */
	public Proxy getFixedProxy();

	/**
	 * @param port
	 * @return
	 */
	public Proxy getProxyForPort(int port);

    /**
	 * 获得一个随机Proxy
	 */
	public Proxy getRandomProxy();
	
	/**
	 * 检查一个Proxy是否可用，同时只能有一个在检查
	 * 
	 * @param proxy
	 * @param forceTest 强制立即测试
	 * @return 
	 */
	public boolean checkProxyAvailable(Proxy proxy, boolean forceTest);

    public String getCacheFileName(String url);

    public boolean saveProxyCache(String url, byte[] bytes);

    public byte[] getProxyCache(String url);
	/**
	 * 是否在本地的缓存中
	 *
	 * @param url
	 * @return
	 */
	public boolean existInProxyCache(String url);

	public boolean addToProxyCache(String url);

    public boolean removeFromProxyCache(String url);
}
