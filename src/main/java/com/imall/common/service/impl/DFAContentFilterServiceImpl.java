package com.imall.common.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.imall.common.sensitive.dfa.DFAFilter;
import com.imall.common.service.IContentFilterService;

@Service("contentFilterServiceImpl")
public class DFAContentFilterServiceImpl implements IContentFilterService{
	/**
	 * 是否使用过滤器
	 */
	@Value("${use.content.filter}")
	private boolean useContentFilter = true;
	
	@Override
	public void init() {
		DFAFilter.getInstance();
	}
	
	@Override
	public void reloadSensitiveWords() {
		DFAFilter.reloadSensitiveWords();
	}
	
	@Override
	public boolean hasFilterWord(String string) {
		if(!useContentFilter){
			return false;
		}
		return DFAFilter.getInstance().isContains(string);
	}
}
