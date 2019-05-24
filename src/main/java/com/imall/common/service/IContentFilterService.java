package com.imall.common.service;

public interface IContentFilterService {
	public void init();
	
	public void reloadSensitiveWords();

	public boolean hasFilterWord(String string);
}
