package com.Magenta_IoT.V001.DemoSensor.messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.Magenta_IoT.V001.DemoSensor.DefaultCloudMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.DefaultMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.InputBuffer;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemperatureArrayMessage extends Message {

	/*
	 ### TemperatureArrayMessage / MessageId: 02

	| Parameter      | Len[Byte] | Type   | Desc                                |
	| -------------- | :-------: | ------ | ----------------------------------- |
	| msgId (02)     |     1     | uint8  | Message Id = 02                     |
	| ts_start       |     4     | uint32 | Timestamp for 1. Temperature        |
	| interval_mode  |     1     | uint8  | 1...sec, 2...min                    |
	| interval       |     2     | uint16 | interval depending on interval_mode |
	| 1. temperature |     2     | int16  | temperature                         |
	| 2. temperature |     2     | int16  | temperature                         |
	.
	.
	| n. temperature |     2     | int16  | temperature                         |
	
	  
	Responses:   
	Success: `0200LEN`  
	Error: `0201`  
	
	Example: 
	Data: `025C9DF06502000500E900F8`  
	Response: `020002`
	 */

	public byte msgId = 0x02; // Define the MessageId
	private int defaultResponseValue = 0x02; // Define the MessageId or default Value for the response
	private boolean sendResponse = true; // Set if a response should be sent. (E.g. If you want to use Early Release
											// Flag it is not useful to send a response)
	private int msgLenByte = 10; // minLenght //Define here the message length in Bytes

	private String messageName = "TemperatureArrayMessage"; // Name of the Message
	private String serviceId = "Temperature"; // The Service which is used

	// Define properties
	// private float temperature = 0;
	private int intervalMode = 0; // 1..seconds, 2..minutes
	private int interval = 0;
	List<Float> temperatures = new ArrayList<>();
	private long tsStart;
	

	private boolean hasError = false;

	// Constructor: Normally you don't have to change this function.
	public TemperatureArrayMessage() {
		setType("deviceReq");
		setMessageId(msgId);
		setName(messageName);

		if (sendResponse) {
			setResponse(String.format("%02x", defaultResponseValue));
		} else {
			setResponse(null);
		}
	}

	// Decode: Here the decoding of the Message from the Device is decoded.
	@Override
	public void decode(InputBuffer paramInputBuffer) {
		try {
			if (paramInputBuffer.getRemainingLength() <= this.msgLenByte)
				hasError = true; // Check length

			if (paramInputBuffer.getRemainingLength() >= 1) {
				messageId = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 4) {
				tsStart = paramInputBuffer.getInt32();
			}
			if (paramInputBuffer.getRemainingLength() >= 1) {
				intervalMode = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				interval = paramInputBuffer.getInt16();
			}
			while (paramInputBuffer.getRemainingLength() >= 2) {
				temperatures.add(paramInputBuffer.getInt16Signed() / 10f);
			}

		} catch (Exception e) {
			hasError = true;
		}

	}

	// toJson: In this Function the json according to your Profile is generated.
	@Override
	public ObjectNode toJsonNode() {
		ObjectMapper localObjectMapper = new ObjectMapper();
		ObjectNode rootNode = localObjectMapper.createObjectNode();
		rootNode.put("msgType", this.getType());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			ArrayNode dataArrayNode = localObjectMapper.createArrayNode();

			Iterator<Float> TemperatureIterator = temperatures.iterator();
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTimeInMillis(tsStart * 1000);
			int count = 0;
			while (TemperatureIterator.hasNext()) {
				Float aktTemp = TemperatureIterator.next();
				ObjectNode serviceDataNode = localObjectMapper.createObjectNode();
				serviceDataNode = localObjectMapper.createObjectNode();
				serviceDataNode.put("temperature", aktTemp);

				ObjectNode serviceNode = localObjectMapper.createObjectNode();
				serviceNode.put("serviceId", serviceId);

				serviceNode.set("serviceData", serviceDataNode);

				if (count > 0) {  //Do not add offset on first Temperature
					if (intervalMode == 1) {
						cal.add(Calendar.SECOND, interval);
					} else if (intervalMode == 2) {
						cal.add(Calendar.MINUTE, interval);
					}
				}

				serviceNode.put("eventTime", sdf.format(cal.getTime()));

				dataArrayNode.add(serviceNode);
				count++;
			}

			rootNode.set("data", dataArrayNode);

		} catch (Exception e) {
			hasError = true;

		}
		return rootNode;
	}

	// getResponse: Generate the Response for the data.
	@Override
	public String getResponse(InputBuffer paramInputBuffer) {
		if (!sendResponse)
			return null;
		if (paramInputBuffer == null || response == null) {
			return response;
		} else {
			decode(paramInputBuffer);
			if (this.hasError) {
				return response + "01";
			} else {
				return response + "00"+String.format("%02x", temperatures.size());
			}
		}
	}

	// registerMessage(): Normally you don't have to change this function.
	public void registerMessage() {
		DefaultMessageFactory.getInstance().register(getMessageId(), this.getClass());
		DefaultCloudMessageFactory.getInstance().registerResponse(getMessageId(), this.getClass());
	}

}
