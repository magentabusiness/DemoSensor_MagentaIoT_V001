package com.Magenta_IoT.V001.DemoSensor.messages;

import com.Magenta_IoT.V001.DemoSensor.DefaultCloudMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.DefaultMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.InputBuffer;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemperatureMessage extends Message {

	/*
	### TemperatureMessage / MessageId: 01

	| Parameter   | Len[Byte] | Type  | Desc             |
	| ----------- | :-------: | ----- | ---------------- |
	| msgId (01)  |     1     | uint8 | Message Id = 01  |
	| temperature |     2     | int16 | Temperature * 10 |
	| sendResponse|     1     | uint8 | optional         |
	
	Responses:   
	Success: `0100`  
	Error: `0101`  
	
	 
	233 --> 23.3 °C  0x00E9
	Example 1:
	Data: `0100E9`  
	No Response
	
	Example 2:
	Data: `0100E901`
	Response: `0100
	
	*/

	public byte msgId = 0x01;						//Define the MessageId
	private int defaultResponseValue = 0x01;		//Define the MessageId or default Value for the response
	private boolean sendResponse = true;			//Set if a response should be sent. (E.g. If you want to use Early Release Flag it is not useful to send a response) 
	private int msgLenByte = 3;						//Define here the message length in Bytes	
	
	private String messageName = "TemperatureMessage";	//Name of the Message
	private String serviceId = "Temperature";           //The Service which is used 
	
	//Define properties
	private float temperature = 0;	

	private boolean hasError = false;

	//Constructor: Normally you don't have to change this function.
	public TemperatureMessage() {
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
			if (paramInputBuffer.getRemainingLength() < this.msgLenByte) hasError=true;  //Check length
			
			if (paramInputBuffer.getRemainingLength() >= 1) {
				messageId = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				temperature = paramInputBuffer.getInt16Signed()/10f;
			}
			
			if (paramInputBuffer.getRemainingLength() >= 1) {
				sendResponse=true;
			} else {
				sendResponse=false;
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
			serviceDataNode.put("temperature", this.temperature);

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
			if (!sendResponse) return null;
			if (this.hasError) {
				return response+"01";
			} else {
				return response+"00";
			}			
		}
	}
	
	
    //registerMessage(): Normally you don't have to change this function.
	public void registerMessage() {
		DefaultMessageFactory.getInstance().register(getMessageId(), this.getClass());
		DefaultCloudMessageFactory.getInstance().registerResponse(getMessageId(), this.getClass());
	}

}
