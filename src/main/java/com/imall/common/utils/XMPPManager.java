package com.imall.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imall.common.domain.PushNotification;
import com.imall.common.domain.XMPPUser;

public class XMPPManager {
	public static final Logger LOGGER = LoggerFactory.getLogger(XMPPManager.class);
	
	private static XMPPManager instance;
	
	public static final String XMPP_PUSH_ELEMENT = "push";
	public static final String XMPP_PUSH_NAMESPACE = "imall";
	
	public static final String XMPP_PUSH_ACTION = "action";
	public static final String XMPP_PUSH_CONTENT = "content";
	
	public static final String XMPP_PUSHACCOUNT_USERNAME = "imallpush";
	public static final String XMPP_PUSHACCOUNT_PASSWORD = "imallpush";
	
	
	private String xmppHost = "chat.imalljoy.com";
//	private String xmppHost = "xmpp.imalljoy.com";
	private String xmppService = CommonConstants.XMPP_USER_SERVICE_NAME;
	private String xmppGroupService = CommonConstants.XMPP_GROUP_SERVICE_NAME;
	private int xmppPort = 5222;
	
	private AbstractXMPPConnection connection;
	private String userName = XMPP_PUSHACCOUNT_USERNAME;
	
	private XMPPManager() {
		
	}
	
	public synchronized static XMPPManager initInstance(String host, int hostPort, String suffix) {
		instance = new XMPPManager();
		if(!StringUtils.isBlank(suffix) || StringUtils.isBlank(instance.userName)){
			instance.initXMPPServerAccount(host, hostPort, suffix);
		}
		loginAsServer();
		return instance;
	}
	
	public synchronized static void loginAsServer(){
		instance.createAccount(instance.userName, XMPP_PUSHACCOUNT_PASSWORD, null);
		instance.login(instance.userName, XMPP_PUSHACCOUNT_PASSWORD);
	}
	
	public synchronized static XMPPManager getInstance() {
		if (instance == null) {
			initInstance("chat.imalljoy.com", 5222, "");
		}
		return instance;
	}
	
	public synchronized void initXMPPServerAccount(String host, int hostPort, String suffix){
		this.xmppHost = host;
		this.xmppPort = hostPort;
		this.userName = "server_" + MacAddressUtils.getMACAddress() + "_" + suffix;
		if(StringUtils.isBlank(this.userName)){
			this.userName = XMPP_PUSHACCOUNT_USERNAME;
		}
	}
	
	public synchronized boolean connect(){
		return this.connect(null);
	}
	
	public synchronized boolean connect(AbstractXMPPConnection connection){
		if(connection == null){
			connection = this.connection;
		}
		if (connection == null) {
			connection = this.getXMPPConnection();
			if(this.connection == null){
				this.connection = connection;
			}
		}
		if(!connection.isConnected()){
			try {
				// Connect to the server
				connection.connect();
				// 重连机制, 如果失去连接, 固定每隔30秒自动重连
				ReconnectionManager rm = ReconnectionManager.getInstanceFor(connection); 
				rm.enableAutomaticReconnection();
				rm.setFixedDelay(30);
			} catch (SmackException e) {
				LOGGER.error("connect xmpp server error SmackException", e);
				return false;
			} catch (XMPPException e) {
				LOGGER.error("connect xmpp server error XMPPException", e);
				return false;
			} catch (IOException e) {
				LOGGER.error("connect xmpp server error IOException", e);
				return false;
			}
		}
		return true;
	}
	
	private AbstractXMPPConnection getXMPPConnection(){
		XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
				.builder()
				// Setting Host or Server IP
				.setHost(xmppHost)
				// Setting Service Ports
				.setPort(xmppPort)
				// Setting Service name: chat.imalljoy.com
				.setServiceName(xmppService)
				// Setting and using default Presence
				.setSendPresence(true)
				.setSecurityMode(SecurityMode.disabled)
				.setDebuggerEnabled(true);

		return new XMPPTCPConnection(config.build());
	}
	
	public synchronized AbstractXMPPConnection login(String userName, String password){
		AbstractXMPPConnection currentConnection = null;
		try {
			currentConnection = this.doLogin(userName, password);
		} catch (Exception e) {
			LOGGER.error("login xmpp user error 1", e);
			this.createAccount(userName, password, null);
			try {
				currentConnection = this.doLogin(userName, password);
			} catch (Exception e1) {
				LOGGER.error("login xmpp user error 2", e1);
			}
		}
		return currentConnection;
	}
	
	private AbstractXMPPConnection doLogin(String userName, String password) throws XMPPException, SmackException, IOException{
		AbstractXMPPConnection currentConnection = null;
		if(userName.equals(this.userName)){
			currentConnection = connection;
		}else{
			currentConnection = this.getXMPPConnection();
			this.connect(currentConnection);
		}
		ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
		reconnectionManager.enableAutomaticReconnection();
		if(!currentConnection.isAuthenticated()){
			//假如没有登录
			currentConnection.login(userName, password, CommonConstants.XMPP_RESOURCE_NAME);
		}
//		reconnectionManager.setFixedDelay(30);
		
		if (currentConnection != null && !currentConnection.isAuthenticated()) {
			LOGGER.warn("NOT login");
		}
		return currentConnection;
	}
	
	/**
	 * @param username
	 * @param password
	 * @param attributes
	 * name -- the user's nickname.
	 * email -- the user's email address.
	 * city -- the user's city.
	 * phone -- the user's phone number.
	 * url -- the user's website.
	 * text -- textual information to associate with the account.
	 * 
	 * @return
	 */
	public synchronized boolean createAccount(String username, String password, Map<String, String> attributes) {
		//检查用户是否存在
		this.connect();
		AccountManager accountManager = AccountManager.getInstance(connection);
		try {
			accountManager.sensitiveOperationOverInsecureConnection(true);
			if (attributes != null && attributes.size() > 0) {
				accountManager.createAccount(username, password, attributes);
			} else {
				accountManager.createAccount(username, password);
			}
		} catch (NotConnectedException e) {
			LOGGER.error("create xmpp user error NotConnectedException", e);
			return false;
		} catch (NoResponseException e) {
			LOGGER.error("create xmpp user error NoResponseException", e);
			return false;
		} catch (XMPPErrorException e) {
			LOGGER.error("create xmpp user error XMPPErrorException", e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean createChatGroupAccountAndJoin(XMPPUser adminXmppUser, 
			String chatGroupName, String chatGroupPassword, 
			String chatGroupNickName, List<? extends XMPPUser> users) {
		String chatGroupJid = chatGroupName + "@" + xmppGroupService;
		AbstractXMPPConnection currentConnection = null;
		AbstractXMPPConnection currentConnection2 = null;
		List<AbstractXMPPConnection> currentConnection2s = new ArrayList<AbstractXMPPConnection>();
        try {  
            currentConnection = this.login(adminXmppUser.getChatId(), adminXmppUser.getChatPassword());
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(currentConnection);
            MultiUserChat room = multiUserChatManager.getMultiUserChat(chatGroupJid);
            room.create(adminXmppUser.getChatId());
            Form form = room.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            //房间的名称
            submitForm.setAnswer("muc#roomconfig_roomname", chatGroupNickName);
            //保证只有注册的昵称才能进入房间
            submitForm.setAnswer("x-muc#roomconfig_reservednick",false);
            //设置为永久房间
            submitForm.setAnswer("muc#roomconfig_persistentroom",true);
            //设置房间人数上限
            List<String> maxusers = new ArrayList<String>();
            maxusers.add(String.valueOf(CommonConstants.CHAT_GROUP_MAX_USER_SIZE + 1));
            submitForm.setAnswer("muc#roomconfig_maxusers", maxusers);
            room.sendConfigurationForm(submitForm);
            
//            multiUserChat.join(adminXmppUser.getChatNickName());
            List<String> jids = new ArrayList<String>();
            for(XMPPUser user : users){
            	jids.add(user.getJid());
//            	currentConnection2 = this.login(user.getChatId(), user.getChatPassword());
//            	currentConnection2s.add(currentConnection2);
//            	MultiUserChatManager multiUserChatManager2 = MultiUserChatManager.getInstanceFor(currentConnection2);
//            	MultiUserChat room2 = multiUserChatManager2.getMultiUserChat(chatGroupJid);
//            	room2.join(user.getChatNickName() + " - " + user.getChatId());
            	
//            	Presence presence = new Presence(Presence.Type.unavailable);
//            	currentConnection2.sendStanza(presence);
            	
            	room.invite(user.getJid(), adminXmppUser.getChatNickName() + "邀请你~");
            }
        	room.invite(adminXmppUser.getJid(), adminXmppUser.getChatNickName() + "邀请你~");
            //重新登录并给user member的权限
            room.grantMembership(jids);
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException("创建群组失败~");
        }finally{
        	currentConnection.disconnect();
        	currentConnection = null;
        	
        	for(AbstractXMPPConnection connection : currentConnection2s){
        		connection.disconnect();
        		connection = null;
        	}
        }
        return true;
	}
	
	/**
	 * @param adminXmppUser
	 * @param chatGroupName
	 * @param chatGroupNickName 群组昵称
	 * @param chatGroupUserNickName 用户群昵称
	 * @return
	 */
	public synchronized boolean changeChatGroupNickName(XMPPUser adminXmppUser, String chatGroupName, 
			String chatGroupNickName, String chatGroupUserNickName) {
		String chatGroupJid = chatGroupName + "@" + xmppGroupService;
		AbstractXMPPConnection currentConnection = null;
		List<AbstractXMPPConnection> currentConnection2s = new ArrayList<AbstractXMPPConnection>();
		try {  
			currentConnection = this.login(adminXmppUser.getChatId(), adminXmppUser.getChatPassword());
			MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(currentConnection);
			MultiUserChat room = multiUserChatManager.getMultiUserChat(chatGroupJid);
			room.join(adminXmppUser.getChatId() + " - " + CommonConstants.XMPP_RESOURCE_NAME);
			if(!StringUtils.isBlank(chatGroupNickName)){
				//TODO dirty fix
				room.changeSubject(chatGroupNickName);
			}
			if(!StringUtils.isBlank(chatGroupUserNickName)){
				room.changeNickname(chatGroupUserNickName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改群组失败~");
		}finally{
			currentConnection.disconnect();
			currentConnection = null;
			
			for(AbstractXMPPConnection connection : currentConnection2s){
				connection.disconnect();
				connection = null;
			}
		}
		return true;
	}
	
	public synchronized boolean changeUserMembershipInChatGroup(XMPPUser adminXmppUser, 
			String chatGroupName, List<? extends XMPPUser> users, boolean isAdd) {
		String chatGroupJid = chatGroupName + "@" + xmppGroupService;
		AbstractXMPPConnection currentConnection = null;
		AbstractXMPPConnection currentConnection2 = null;
		List<AbstractXMPPConnection> currentConnection2s = new ArrayList<AbstractXMPPConnection>();
		try {  
			currentConnection = this.login(adminXmppUser.getChatId(), adminXmppUser.getChatPassword());
			MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(currentConnection);
			MultiUserChat room = multiUserChatManager.getMultiUserChat(chatGroupJid);
			room.join(adminXmppUser.getChatId() + " - " + CommonConstants.XMPP_RESOURCE_NAME);
			List<String> jids = new ArrayList<String>();
			for(XMPPUser user : users){
				jids.add(user.getJid());
				if(isAdd){
//					currentConnection2 = this.login(user.getChatId(), user.getChatPassword());
//					currentConnection2s.add(currentConnection2);
//					MultiUserChatManager multiUserChatManager2 = MultiUserChatManager.getInstanceFor(currentConnection2);
//					MultiUserChat room2 = multiUserChatManager2.getMultiUserChat(chatGroupJid);
//					room2.join(user.getChatNickName() + " - " + user.getChatId());
					room.invite(user.getJid(), adminXmppUser.getChatNickName() + "邀请你~");
				}else{
					room.kickParticipant(user.getChatId(), "亲，你被踢了哦");
				}
			}
			if(isAdd){
				room.grantMembership(jids);
			}else{
				room.revokeMembership(jids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改群组失败~");
		}finally{
			currentConnection.disconnect();
			currentConnection = null;
			
			for(AbstractXMPPConnection connection : currentConnection2s){
				connection.disconnect();
				connection = null;
			}
		}
		return true;
	}
	
	public synchronized boolean changeChatGroupAdmin(String chatGroupName, 
			XMPPUser oldAdminXmppUser, XMPPUser newAdminXmppUser) {
		String chatGroupJid = chatGroupName + "@" + xmppGroupService;
		AbstractXMPPConnection currentConnection1 = null;
		AbstractXMPPConnection currentConnection2 = null;
		try {  
			currentConnection1 = this.login(oldAdminXmppUser.getChatId(), oldAdminXmppUser.getChatPassword());
			MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(currentConnection1);
			MultiUserChat room1 = multiUserChatManager.getMultiUserChat(chatGroupJid);
			room1.join(oldAdminXmppUser.getChatId() + " - " + CommonConstants.XMPP_RESOURCE_NAME);
			
			currentConnection2 = this.login(newAdminXmppUser.getChatId(), newAdminXmppUser.getChatPassword());
			Presence presence = new Presence(Presence.Type.unavailable);
			currentConnection2.sendStanza(presence);
			MultiUserChatManager multiUserChatManager2 = MultiUserChatManager.getInstanceFor(currentConnection2);
			MultiUserChat room2 = multiUserChatManager2.getMultiUserChat(chatGroupJid);
			room2.join(newAdminXmppUser.getChatId() + " - " + CommonConstants.XMPP_RESOURCE_NAME);
			
			List<Affiliate> owners = room1.getOwners();
			boolean isOldAdminOwner = false;
			if(owners != null){
				for(Affiliate affiliate : owners){
					if(affiliate.getJid().equalsIgnoreCase(oldAdminXmppUser.getJid())){
						isOldAdminOwner = true;
						break;
					}
				}
			}
			if(isOldAdminOwner){
				room1.grantOwnership(newAdminXmppUser.getJid());
				room1.revokeOwnership(oldAdminXmppUser.getJid());
			}else{
				room1.grantAdmin(newAdminXmppUser.getJid());
			}
			room2.revokeAdmin(oldAdminXmppUser.getJid());
			room2.revokeMembership(oldAdminXmppUser.getJid());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("修改群组失败~");
		}finally{
			currentConnection1.disconnect();
			currentConnection1 = null;
			
			currentConnection2.disconnect();
			currentConnection2 = null;
		}
		return true;
	}
	
	public boolean push(List<PushNotification> pushNotifications) {
		boolean successful = false;
		if(pushNotifications == null){
			return successful;
		}
		loginAsServer();
		for(PushNotification pushNotification : pushNotifications){
			try {
				Message message = new Message();
				String user = pushNotification.getUid() + "@" + xmppService;
				message.setTo(user);
				message.setBody("");
				
				String str = JsonUtils.fromObjectToJson(pushNotification);
				
				DefaultExtensionElement dee = new DefaultExtensionElement(XMPP_PUSH_ELEMENT, XMPP_PUSH_NAMESPACE);
				dee.setValue(XMPP_PUSH_ACTION, "push");
				dee.setValue(XMPP_PUSH_CONTENT, str);
				message.addExtension(dee);
				
				connection.sendStanza(message);
				successful = true;
				LOGGER.debug("send push notification android to {}, {}", new Object[]{pushNotification.getUid(), str});
			} catch (NotConnectedException e) {
				LOGGER.error("push to xmpp server error", e);
				successful = false;
			}
		}
		return successful;
	}
}