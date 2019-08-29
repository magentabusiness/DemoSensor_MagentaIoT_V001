package com.Magenta_IoT.V001.DemoSensor.messages;

import com.Magenta_IoT.V001.DemoSensor.DefaultCloudMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.DefaultMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.InputBuffer;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BatteryMessage extends Message {

	/*
	### BatteryMessage / MessageId: 0A

	| Parameter      | Len[Byte] | Type   | Desc                 |
	| -------------- | :-------: | ------ | -------------------- |
	| msgId (0A)     |     1     | uint8  | Message Id = 0A      |
	| BatteryLevel   |     1     | uint8  | BatteryLevel in %    |
	| BatteryVoltage |     2     | uint16 | BatteryVoltage in mV |

	Responses:   
	Success: `0A00`  
	Error: `0A01`  

	Example:  
	Data: `0A170ABC`  
	Response: `0A00`
	*/

	public byte msgId = 0x0A;						//Define the MessageId
	private int defaultResponseValue = 0x0A;		//Define the MessageId or default Value for the response
	private boolean sendResponse = true;			//Set if a response should be sent. (E.g. If you want to use Early Release Flag it is not useful to send a response) 
	private int msgLenByte = 4;						//Define here the message length in Bytes	
	
	private String messageName = "BatteryMessage";	//Name of the Message
	private String serviceId = "Battery";           //The Service which is used 
	
	//Define properties
	private int batteryLevel = 0;
	private int batteryVoltage = 0;
	
	

	private boolean hasError = false;

	//Constructor: Normally you don't have to change this function.
	public BatteryMessage() {
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
				batteryLevel = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				batteryVoltage = paramInputBuffer.getInt16();
			}
		} catch (Exception e) {
			hasError = true;
		}

	}

	/*@Override
	public void encode(OutputBuffer paramOutputBuffer) {
		try {
			paramOutputBuffer.putByte(messageId);
			paramOutputBuffer.putByte(batteryLevel+5);
			paramOutputBuffer.putInt16(batteryVoltage);
		} catch (Exception e) {
			hasError = true;
		

	} */

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
			serviceDataNode.put("batteryLevel", this.batteryLevel);
			serviceDataNode.put("batteryVoltage", this.batteryVoltage);

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
