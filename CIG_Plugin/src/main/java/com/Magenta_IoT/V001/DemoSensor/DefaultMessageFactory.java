package com.Magenta_IoT.V001.DemoSensor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMessageFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageFactory.class);

	private static final DefaultMessageFactory INSTANCE = new DefaultMessageFactory();

	private Map<Integer, Class<? extends Message>> messageFactory = new ConcurrentHashMap<Integer, Class<? extends Message>>();

	public static DefaultMessageFactory getInstance() {
		return INSTANCE;
	}

	public Message createMessage(int paramInt) {
		return createInstance(messageFactory.get(Integer.valueOf(paramInt)));
	}

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
	
	public void register(Integer msgId, Class<? extends Message> paramClass) {
		messageFactory.put(msgId,paramClass);
	}

	private DefaultMessageFactory() {
	//	messageFactory.put(Integer.valueOf(0x0A), BatteryMessage.class);
//    messageFactory.put(Integer.valueOf(0), StringData.class);
//    messageFactory.put(Integer.valueOf(1), IntData.class);
//    messageFactory.put(Integer.valueOf(6), IntDataNoResp.class);
//    messageFactory.put(Integer.valueOf(7), StringDataNoResp.class);
//    messageFactory.put(Integer.valueOf(4), hw0x2.class);
//    messageFactory.put(Integer.valueOf(5), hw0x3.class);
	}
}
