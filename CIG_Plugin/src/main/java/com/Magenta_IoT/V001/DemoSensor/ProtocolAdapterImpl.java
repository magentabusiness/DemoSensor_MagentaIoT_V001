package com.Magenta_IoT.V001.DemoSensor;


import com.Magenta_IoT.V001.DemoSensor.commands.TemperatureSetMeasurePeriodCommand;
import com.Magenta_IoT.V001.DemoSensor.commands.TemperatureSetMeasurePeriodResponse;
import com.Magenta_IoT.V001.DemoSensor.commands.TemperatureSetTransferPeriodCommand;
import com.Magenta_IoT.V001.DemoSensor.commands.TemperatureSetTransferPeriodResponse;
import com.Magenta_IoT.V001.DemoSensor.messages.BatteryAndSignalMessage;
import com.Magenta_IoT.V001.DemoSensor.messages.BatteryMessage;
import com.Magenta_IoT.V001.DemoSensor.messages.ConnectivityMessage;
import com.Magenta_IoT.V001.DemoSensor.messages.ClockRequestMessage;
import com.Magenta_IoT.V001.DemoSensor.messages.LocationMessage;
import com.Magenta_IoT.V001.DemoSensor.messages.TemperatureArrayMessage;
import com.Magenta_IoT.V001.DemoSensor.messages.TemperatureMessage;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.huawei.m2m.cig.tup.modules.protocol_adapter.IProtocolAdapter;
import java.util.Arrays;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolAdapterImpl implements IProtocolAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolAdapterImpl.class);
	private static final String MANU_FACTURERID = "MagentaIoT";
	private static final String MODEL = "V001";
	
	

	public ProtocolAdapterImpl() {

		new BatteryMessage().registerMessage();
		new ConnectivityMessage().registerMessage();		
		new LocationMessage().registerMessage();
		new ClockRequestMessage().registerMessage();
		new TemperatureMessage().registerMessage();
		new TemperatureArrayMessage().registerMessage();
		new BatteryAndSignalMessage().registerMessage();
		
		
		
		//Commands
		new TemperatureSetMeasurePeriodCommand().registerCommand();
		new TemperatureSetMeasurePeriodResponse().registerMessage();
		
		new TemperatureSetTransferPeriodCommand().registerCommand();
		new TemperatureSetTransferPeriodResponse().registerMessage();

	}

	public static int rspId = 0;

	public void activate() {
		LOGGER.info("ProtocolAdapterImpl activate");
	}

	public void deactivate() {
		LOGGER.info("ProtocolAdapterImpl deactivate");
	}

	public ObjectNode decode(byte[] paramArrayOfByte) throws Exception {
		// System.out.println(Arrays.toString(paramArrayOfByte));
		if (paramArrayOfByte == null) {
			// throw new NullPointerException("rawData is null");
		}

		LOGGER.info("receive raw message {}", Arrays.toString(paramArrayOfByte));

		int i = getMessageId(paramArrayOfByte);
		rspId = i;
		Message message = DefaultMessageFactory.getInstance().createMessage(i);
		if (message == null) {
			LOGGER.error("msg  id={} not found!", Integer.valueOf(i));
			// System.out.println("msg id={} not found!" + Integer.valueOf(i));
			return null;
		}

		LOGGER.info("msg  id={} found!", Integer.valueOf(i));

		BigEndianInputBuffer localBigEndianInputBuffer = new BigEndianInputBuffer(paramArrayOfByte);
		message.decode(localBigEndianInputBuffer);
		LOGGER.info("decode jsonNode is {}", message.toJsonNode());
		// System.out.println("decode jsonNode is {}; "+ message.toJsonNode());
		return message.toJsonNode();
	}

	private int getMessageId(byte[] paramArrayOfByte) {
		byte[] arrayOfByte = new byte[1];
		System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 1);
		BigEndianInputBuffer localBigEndianInputBuffer = new BigEndianInputBuffer(arrayOfByte);
		return localBigEndianInputBuffer.getByte();
	}

	public byte[] encode(ObjectNode paramObjectNode) throws Exception {
		if (paramObjectNode == null) {
			LOGGER.error("objectNode == null");
			return null;
		}

		LOGGER.info("encode jsonNode is {}", paramObjectNode);
		if (!paramObjectNode.has("msgType")) {
			LOGGER.error("msgType not found");
			return null;
		}

		String str = paramObjectNode.get("msgType").asText();
		if ("cloudReq".equals(str))
			return encodeCloudRequest(paramObjectNode);
		if ("cloudRsp".equals(str)) {
			return encodeCloudResponse(paramObjectNode);
		}
		LOGGER.error(String.format("invalid msgType {}", new Object[] { str }));
		return null;
	}

	private byte[] encodeCloudRequest(ObjectNode paramObjectNode) throws Exception {
		if (!paramObjectNode.has("cmd")) {
			LOGGER.error("cmd not found, objectNode is {}", paramObjectNode);
			return null;
		}

		String str = paramObjectNode.get("cmd").asText();
		
		return encodeHelper(paramObjectNode, str);
	}

	private byte[] encodeCloudResponse(ObjectNode paramObjectNode) throws Exception {

		byte[] result;
		String request = paramObjectNode.get("request").asText();
		//System.out.println("request = : "+request);
		try {
			boolean isHex = request.matches("^[0-9a-fA-F]+$");
			if (isHex) {
				result = HexString2Bytes(request);
				//System.out.println("request ist Hex: "+request);
			} else {
				result = Base64.getDecoder().decode(request);   //On real Server request is Base64 encoded, in pluginTester its a Hex String
				//System.out.println("request ist Base64: "+request);
			}
			
		} catch (Exception e) {
			LOGGER.error("{}",e);
			return null;
		}

		LOGGER.info("result: {}", result);
		//System.out.println("result:  "+result);

		int msgId = getMessageId(result);

		// int msgId =
		// getMessageId(HexString2Bytes(paramObjectNode.get("request").asText()));
		Message localNBMessage = DefaultCloudMessageFactory.getInstance().createRespMessage(msgId);

		// String resp = localNBMessage.getResponse(new
		// BigEndianInputBuffer(HexString2Bytes(paramObjectNode.get("request").asText())));
		String resp = localNBMessage.getResponse(new BigEndianInputBuffer(result));

		//System.out.println("Response for " + msgId + " is: " + resp);

		if (isStringEmpty(resp)) {
			return null;
		}


		return HexString2Bytes(resp);


	}

	private byte[] encodeHelper(ObjectNode paramObjectNode, String paramString) throws Exception {
		if (isStringEmpty(paramString)) {
			return null;
		}
		
		//System.out.println("paramString: "+paramString);

		Message localNBMessage = DefaultCloudMessageFactory.getInstance().createMessage(paramString);
		if (localNBMessage == null) {
			System.out.println("localNBMessage null");
			return null;
		}
		//System.out.println("Name: " +localNBMessage.getName());

		localNBMessage.configJsonNode(paramObjectNode);

		BigEndianOutputBuffer localBigEndianOutputBuffer = new BigEndianOutputBuffer();
		localNBMessage.encode(localBigEndianOutputBuffer);

		LOGGER.info("result byte is {}", Arrays.toString(localBigEndianOutputBuffer.toByteArray()));
		//System.out.println("result byte is: "+Arrays.toString(localBigEndianOutputBuffer.toByteArray()));
		return localBigEndianOutputBuffer.toByteArray();
	}

	public String getManufacturerId() {
		return MANU_FACTURERID;
	}

	public String getModel() {
		return MODEL;
	}

	private boolean isStringEmpty(String paramString) {
		if ((paramString == null) || (paramString.isEmpty())) {
			return true;
		}

		return false;
	}

	private byte[] HexString2Bytes(String paramString) {
		if ((paramString == null) || (paramString.length() == 0)) {
			return null;
		}
		byte[] arrayOfByte1 = new byte[paramString.length() / 2];
		byte[] arrayOfByte2 = paramString.getBytes();
		for (int i = 0; i < arrayOfByte2.length / 2; i++) {
			arrayOfByte1[i] = uniteBytes(arrayOfByte2[(i * 2)], arrayOfByte2[(i * 2 + 1)]);
		}
		return arrayOfByte1;
	}

	private byte uniteBytes(byte paramByte1, byte paramByte2) {
		int i = Byte.decode("0x" + new String(new byte[] { paramByte1 })).byteValue();
		i = (byte) (i << 4);
		int j = Byte.decode("0x" + new String(new byte[] { paramByte2 })).byteValue();
		byte b = (byte) (i | j);
		return b;
	}
}
