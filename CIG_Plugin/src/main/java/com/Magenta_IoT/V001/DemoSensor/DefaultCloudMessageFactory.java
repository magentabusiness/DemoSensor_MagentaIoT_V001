package com.Magenta_IoT.V001.DemoSensor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCloudMessageFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCloudMessageFactory.class);

	private static final DefaultCloudMessageFactory INSTANCE = new DefaultCloudMessageFactory();

	private Map<String, Class<? extends Message>> messageCmdFactory = new ConcurrentHashMap<String, Class<? extends Message>>();
	private Map<Integer, Class<? extends Message>> messageRespFactory = new ConcurrentHashMap<Integer, Class<? extends Message>>();


	public static DefaultCloudMessageFactory getInstance() {
		return INSTANCE;
	}

	public Message createMessage(String paramString) {
		return createInstance(messageCmdFactory.get(paramString));
		

		
	}
	
	public Message createRespMessage(int msgId) {
		
		return createInstance(messageRespFactory.get(msgId));
	}

	/*public String getCloudMessageRspName() {
		name = "hw0x0";
		if (name.substring(name.length() - 1).equals(String.valueOf(ProtocolAdapterImpl.rspId))) {
			realname = name;
		}
		name = "hw0x1";
		if (name.substring(name.length() - 1).equals(String.valueOf(ProtocolAdapterImpl.rspId))) {
			realname = name;
		}
		LOGGER.info("getCloudMessageRspName: {}",realname);
		System.out.println("getCloudMessageRspName: "+realname);
		return realname;
	}*/

	/*public String getCloudMessageRspData() {
		name = "hw0x0";
		if (name == realname) {
			rspData = "AA05";
			realname = "";
		}
		name = "hw0x1";
		if (name == realname) {
			rspData = "AA06";
			realname = "";
		}
		return rspData;
	}*/

	private Message createInstance(Class<? extends Message> paramClass) {
		try {
			if (paramClass != null) {
				return (Message) paramClass.newInstance();
			}
		} catch (Exception localException) {
			LOGGER.error("Unexpected exception when createInstance.Exception:", localException);
		}

		return null;
	}
	
	public void registerResponse(Integer msgId, Class<? extends Message> paramClass) {
		messageRespFactory.put(msgId,paramClass);		
	}
	public void registerCommand(String cmdName, Class<? extends Message> paramClass) {
		messageCmdFactory.put(cmdName, paramClass);		
	}

	private DefaultCloudMessageFactory() {
	//	messageRespFactory.put(0x0A,BatteryMessage.class);
//    messageFactory.put("StringCmd", StringCmd.class);
//    messageFactory.put("IntCmd", IntCmd.class);
//    messageFactory.put("hw0x0", hw0x0.class);
//    messageFactory.put("hw0x1", hw0x1.class);
	}

	
}
