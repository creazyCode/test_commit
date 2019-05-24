package com.imall.proxy;

import java.util.List;

public interface IProxyProviderService {
	/**
	 * 从服务提供商那获得Proxy
	 * 
	 * @param number 获得Proxy的数量
	 * @return
	 */
	public List<Proxy> loadProxyFromProvider(int number);

	/**
	 * 检查是否需要reload proxy，暂时没啥用了？？？
	 *
	 * @param number
	 */
	public void checkReloadProxy(final int number);

    /**
     * @param number
     */
    public List<Proxy> reloadProxyFromProviderServer(final int number);
}
