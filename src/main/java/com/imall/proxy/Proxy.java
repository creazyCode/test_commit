package com.imall.proxy;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

public class Proxy implements Serializable{
	private String host;
	private int port;
	private Timestamp getTime;
	private Timestamp invalidTime;

	public Timestamp getGetTime() {
		return getTime;
	}

	public void setGetTime(Timestamp getTime) {
		this.getTime = getTime;
	}

	public Timestamp getInvalidTime() {
		return invalidTime;
	}

	public void setInvalidTime(Timestamp invalidTime) {
		this.invalidTime = invalidTime;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isValid() {
		return this.getInvalidTime() == null || System.currentTimeMillis() <= this.getInvalidTime().getTime();
	}

	public static Proxy parseFromString(String proxyString) {
		if (!StringUtils.isEmpty(proxyString)) {
			String[] strs = proxyString.split(":");
			if (strs.length == 2 && !StringUtils.isEmpty(strs[0]) && !StringUtils.isEmpty(strs[1])) {
				Proxy proxy = new Proxy();
				proxy.setHost(strs[0]);
				proxy.setPort(Integer.valueOf(strs[1]));
				return proxy;
			}
		}
		return null;
	}

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Proxy)){
            return false;
        }
	    if (this == obj){
	        return true;
        }
        return this.toString().equals(obj.toString());
    }

    @Override
	public String toString() {
		return new StringBuilder().append(host).append(":").append(port).toString();
	}
}
