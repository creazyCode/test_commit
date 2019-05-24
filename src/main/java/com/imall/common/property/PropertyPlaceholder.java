package com.imall.common.property;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.imall.common.utils.ResourceUtils;

public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {
	public static final Logger LOGGER = LoggerFactory.getLogger(PropertyPlaceholder.class);
	
    private static Map<String, String> propertyMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        
        LOGGER.info("====config loaded====");
        propertyMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            propertyMap.put(keyStr, value);
            LOGGER.info("{}={}", key, value);
        }
    }
    
    public static String getProperty(String name) {
    	if(propertyMap == null){
    		return null;
    	}
        return propertyMap.get(name);
    }
    
    public static void reloadConfig() {
    	LOGGER.info("====config reloaded====");
	    Configuration config = ResourceUtils.loadConfig("config/common-config.properties");
	    Iterator<String> keys = config.getKeys();
	    while(keys.hasNext()){
	    	String key = keys.next();
	    	propertyMap.put(key, config.getString(key));
	    	LOGGER.info("{}={}", key, config.getString(key));
	    }
	    
	    config = ResourceUtils.loadConfig("config/special-config.properties");
	    keys = config.getKeys();
	    while(keys.hasNext()){
	    	String key = keys.next();
	    	propertyMap.put(key, config.getString(key));
	    	LOGGER.info("{}={}", key, config.getString(key));
	    }
    }
}