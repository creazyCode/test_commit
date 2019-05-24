package com.imall.common.domain;

/**
 *
 * @author jianxunji
 *
 */
public interface IUser {
	public Long getUid();

	public void setUid(Long uid);

	public String getUuid();

	public void setUuid(String uuid);

	public String getUserName();

	public void setUserName(String userName);

	public String getName();

	public void setName(String name);

	public String getEmail();

	public void setEmail(String email);
}
