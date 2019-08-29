package com.Magenta_IoT.V001.DemoSensor.messages;

import com.Magenta_IoT.V001.DemoSensor.DefaultCloudMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.DefaultMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.InputBuffer;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ConnectivityMessage extends Message {

	/*
	### CellLocationMessage / MessageId: 1B

	| Parameter      | Len[Byte] | Type  | Desc                  |
	| -------------- | :-------: | ----- | --------------------- |
	| msgId (1B)     |     1     | uint8 | Message Id = 0B       |
	| cellId         |     4     | uint32| cellId ECI            |
	| pci            |     2     | uint16|           optional    |
	| celevel        |     1     | unt8  | 0,1,2     optional    |
	| rsrp           |     2     | int16 | dBm*10    optional    |
	| rsrq           |     2     | int16 | dBm*10    optional    |
	| rssi           |     2     | int16 | dBm*10    optional    |
	| snr            |     2     | int16 | dB*10     optional    |
	| sinr           |     2     | int16 | dB*10     optional    |
	
	

	Responses:   
	none  

	Example:  
	1B 00490A65 01C8 01 FCF7 FF94 FD32 0053 0000
	1B00490A6501C801FCF7FF94FD3200530000
	
	cellId: 4786789  --> 0x00490A65
	pci: 456   	-->	0x01C8
	celevel: 1  -->	0x01
	rsrp: -777 	-->	0xFCF7
	rsrq: -108 	-->	0xFF94
	rssi: -718 	-->	0xFD32
	snr: 83 	-->	0x0053
	sinr: 0   	-->	0x0000
	
	*/

	public byte msgId = 0x1B;						//Define the MessageId
	private int defaultResponseValue = 0x1B;		//Define the MessageId or default Value for the response
	private boolean sendResponse = false;			//Set if a response should be sent. (E.g. If you want to use Early Release Flag it is not useful to send a response) 
	private int msgLenByte = 5;						//Define here the message length in Bytes	
	
	private String messageName = "ConnectivityMessage";	//Name of the Message
	private String serviceId = "Connectivity";           //The Service which is used 
	
	//Define properties
	private int cellId = 0;
	private int pci=0;
	private int celevel=0;
	private double rsrp=0;
	private double rsrq=0;
	private double rssi=0;
	private double snr=0;
	private double sinr=0;
	
		

	private boolean hasError = false;

	//Constructor: Normally you don't have to change this function.
	public ConnectivityMessage() {
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
			if (paramInputBuffer.getRemainingLength()<=this.msgLenByte) hasError=true;  //Check length
			
			if (paramInputBuffer.getRemainingLength() >= 1) {
				messageId = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 4) {
				cellId = paramInputBuffer.getInt32();
			}
			
			if (paramInputBuffer.getRemainingLength() >= 2) {
				pci = paramInputBuffer.getInt16();
			}
			if (paramInputBuffer.getRemainingLength() >= 1) {
				celevel = paramInputBuffer.getByte();
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				rsrp = paramInputBuffer.getInt16Signed()/10.0;
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				rsrq = paramInputBuffer.getInt16Signed()/10.0;
			}
			
			if (paramInputBuffer.getRemainingLength() >= 2) {
				rssi = paramInputBuffer.getInt16Signed()/10.0;
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				snr = paramInputBuffer.getInt16Signed()/10.0;
			}
			if (paramInputBuffer.getRemainingLength() >= 2) {
				sinr = paramInputBuffer.getInt16Signed()/10.0;
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
			serviceDataNode.put("cellId", this.cellId);
			serviceDataNode.put("pci", this.pci);
			serviceDataNode.put("celevel", this.celevel);
			serviceDataNode.put("rsrp", this.rsrp);
			serviceDataNode.put("rsrq", this.rsrq);
			serviceDataNode.put("rssi", this.rssi);
			serviceDataNode.put("snr", this.snr);
			serviceDataNode.put("sinr", this.sinr);
	

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
