package com.Magenta_IoT.V001.DemoSensor.commands;

import com.Magenta_IoT.V001.DemoSensor.DefaultMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.InputBuffer;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemperatureSetMeasurePeriodResponse extends Message {



	public byte msgId = 0x03;						//Define the MessageId	
	private String messageName = "TemperatureSetMeasurePeriodDeviceResponse";	//Name of the Message

	//Define properties
	private int errcode;
	private int mid;	  
	private int result;  //Depending of your Service Profile
	

	//Constructor: Normally you don't have to change this function.
	public TemperatureSetMeasurePeriodResponse() {
		setType("deviceReq");
		setMessageId(msgId);
		setName(messageName);
	}
	
	
    //Decode: Here the decoding of the Message from the Device is decoded.
	@Override
	public void decode(InputBuffer paramInputBuffer)
	  {
	    if (paramInputBuffer.getRemainingLength() >= 1)
	    {
	      messageId = paramInputBuffer.getByte();
	    }
	    if (paramInputBuffer.getRemainingLength() >= 1)
	    {
	      errcode = paramInputBuffer.getByte();
	    }
	    if (paramInputBuffer.getRemainingLength() >= 2)
	    {
	      mid = paramInputBuffer.getInt16();
	    }
	    if (paramInputBuffer.getRemainingLength() >= 2)
	    {
	      result = paramInputBuffer.getInt16Signed();
	    }
	  }
	  
	  public ObjectNode toJsonNode()
	  {
	    ObjectMapper localObjectMapper = new ObjectMapper();
	    ObjectNode responseNode = localObjectMapper.createObjectNode();
	    responseNode.put("msgType", "deviceRsp");
	    
	    responseNode.put("errcode", errcode);
	    responseNode.put("mid", mid);
	    ObjectNode resultNode = localObjectMapper.createObjectNode();
	    resultNode.put("result", result);
	    responseNode.set("body", resultNode);
	    
	    return responseNode;
	  }

	
    //registerMessage(): Normally you don't have to change this function.
	public void registerMessage() {
		DefaultMessageFactory.getInstance().register(getMessageId(), this.getClass());
	}

}
