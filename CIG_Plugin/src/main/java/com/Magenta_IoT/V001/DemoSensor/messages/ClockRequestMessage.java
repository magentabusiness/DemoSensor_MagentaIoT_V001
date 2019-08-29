package com.Magenta_IoT.V001.DemoSensor.messages;

import com.Magenta_IoT.V001.DemoSensor.DefaultCloudMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.DefaultMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.InputBuffer;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClockRequestMessage extends Message {

	/*
	### ClockRequestMessage / MessageId: 0E
	
	| Parameter   | Len[Byte] | Type  | Desc             |
	| ----------- | :-------: | ----- | ---------------- |
	| msgId (0E)  |     1     | uint8 | Message Id = 0E  |
	| timeRequest |     1     | uint8 | Time Request = 1 |
	
	Responses:   
	Success: `0EUNIX_TIMESTAMP_HEX`  
	Error: `0E01`  
	
	Example: 
	Data: `0E01`  
	Response: `0E5C9DDB77`
	*/

	public byte msgId = 0x0E;						//Define the MessageId
	private int defaultResponseValue = 0x0E;		//Define the MessageId or default Value for the response
	private boolean sendResponse = true;			//Set if a response should be sent. (E.g. If you want to use Early Release Flag it is not useful to send a response) 
	private int msgLenByte = 2;  					//Define here the message length in Bytes	
	
	private String messageName = "ClockRequestMessage";	//Name of the Message
	private String serviceId = "Clock";           //The Service which is used 
	
	//Define properties
	private int timeRequest = 0;
		

	private boolean hasError = false;

	//Constructor: Normally you don't have to change this function.
	public ClockRequestMessage() {
		setType("deviceReq");
		setMessageId(msgId);
		setName(messageName);

		if (sendResponse) {
			setResponse(String.format("%02x", defaultResponseValue));
		} else {
			setResponse(null);
		}
	}
	
	
    //Decode: Here the decoding of the Message from the Device is decoded.
	@Override
	public void decode(InputBuffer paramInputBuffer) {
		try {
			if (paramInputBuffer.getRemainingLength()!=this.msgLenByte) hasError=true;  //Check length
			
			if (paramInputBuffer.getRemainingLength() >= 1) {
				messageId = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 1) {
				timeRequest = paramInputBuffer.getByte();
			}
			
		} catch (Exception e) {
			hasError = true;
		}

	}

	
	//toJson: In this Function the json according to your Profile is generated.
	@Override
	public ObjectNode toJsonNode() {
		ObjectMapper localObjectMapper = new ObjectMapper();
		ObjectNode rootNode = localObjectMapper.createObjectNode();
		rootNode.put("msgType", this.getType());
		try {
			ArrayNode dataArrayNode = localObjectMapper.createArrayNode();
			ObjectNode serviceDataNode = localObjectMapper.createObjectNode();
			serviceDataNode = localObjectMapper.createObjectNode();
			serviceDataNode.put("timeRequest", this.timeRequest);
	

			ObjectNode serviceNode = localObjectMapper.createObjectNode();
			serviceNode.put("serviceId", serviceId);

			serviceNode.set("serviceData", serviceDataNode);

			dataArrayNode.add(serviceNode);

			rootNode.set("data", dataArrayNode);
			
		} catch (Exception e) {
			hasError = true;
			
		}
		return rootNode;
	}
	
	
	//getResponse: Generate the Response for the data.
	@Override
	public String getResponse(InputBuffer paramInputBuffer) {
		if (!sendResponse) return null;
		if (paramInputBuffer == null || response == null) {
			return response;
		} else {
			decode(paramInputBuffer);
			if (this.hasError) {
				return response+"01";
			} else {
				long unixTime = System.currentTimeMillis() / 1000L;
				return response+String.format("%04x", unixTime);
			}			
		}
	}
	
	
    //registerMessage(): Normally you don't have to change this function.
	public void registerMessage() {
		DefaultMessageFactory.getInstance().register(getMessageId(), this.getClass());
		DefaultCloudMessageFactory.getInstance().registerResponse(getMessageId(), this.getClass());
	}

}
