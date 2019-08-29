package com.Magenta_IoT.V001.DemoSensor.commands;

import com.Magenta_IoT.V001.DemoSensor.DefaultCloudMessageFactory;
import com.Magenta_IoT.V001.DemoSensor.Message;
import com.Magenta_IoT.V001.DemoSensor.OutputBuffer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemperatureSetMeasurePeriodCommand extends Message {
	public byte msgId = 0x03;
	private int value;
	private int mid;

	public TemperatureSetMeasurePeriodCommand() {
		setType("cloudReq");
		setName("SET_MEASURE_PERIOD");
		setMessageId(msgId);

	}

	public int getValue() {
		return value;
	}

	public void setValue(int paramInt) {
		value = paramInt;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int paramInt) {
		mid = paramInt;
	}

	public void encode(OutputBuffer paramOutputBuffer) {
		paramOutputBuffer.putByte(messageId);
		paramOutputBuffer.putInt16(value);
		paramOutputBuffer.putInt16(mid);
	}

	public void configJsonNode(ObjectNode paramObjectNode) throws Exception {
		mid = Integer.parseInt(paramObjectNode.get("mid").asText());
		JsonNode parasNode = paramObjectNode.get("paras");

		JsonNode valueNode = parasNode.get("value");
		if (valueNode != null) {
			setValue(valueNode.asInt());
		}
		
	}

	// registerMessage(): Normally you don't have to change this function.
	public void registerCommand() {
		DefaultCloudMessageFactory.getInstance().registerCommand(getName(), this.getClass());
		//DefaultCloudMessageFactory.getInstance().registerResponse(getMessageId(), TemperatureSetMeasurePeriod.class);
	}
}
