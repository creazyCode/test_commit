package com.imall.common.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imall.common.domain.PushNotification;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

/**
 * @author jianxunji
 *
 */
public class AppleUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppleUtils.class);
	
	public static boolean sendPushNotification(String p12FilePath, String password, 
			List<PushNotification> notifications, boolean debug){
		boolean successful = false;
		ApnsService service = null;
		if(debug){
			service = APNS.newService()
		    .withCert(p12FilePath, password)
		    .withSandboxDestination()
		    .build();
		}else{
			service = APNS.newService()
		    .withCert(p12FilePath, password)
		    .withProductionDestination()
		    .build();
		}
		for(PushNotification apn : notifications){
			try{
				String payload = APNS.newPayload()
						.alertBody(apn.getContent())
						.sound("default")
						.customField("type", apn.getType())
						.customField("showPage", apn.getShowPage())
						.customFields(apn.getCustomFields())
						.build();
				service.push(apn.getDeviceToken(), payload);
				Map<String, Date> inactiveDevices = service.getInactiveDevices();
				for (String deviceToken : inactiveDevices.keySet()) {
				    Date inactiveAsOf = inactiveDevices.get(deviceToken);
				    LOGGER.debug("apns result:{}", inactiveAsOf);
				}
				successful = true;
				LOGGER.debug("send push notification ios to {}, {}", new Object[]{apn.getUid(), payload});
			}catch(Exception e){
				LOGGER.error("send notification error.", e);
				successful = false;
			}
		}
		return successful;
	}
}