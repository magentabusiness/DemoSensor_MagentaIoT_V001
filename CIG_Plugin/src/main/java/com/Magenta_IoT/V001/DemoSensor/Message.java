package com.Magenta_IoT.V001.DemoSensor;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Message {
	protected String name;
	protected int messageId;
	protected String type;
	protected String response;

	public Message() {
	}

	public void encode(OutputBuffer paramOutputBuffer) {
	}

	public void decode(InputBuffer paramInputBuffer) {
	}
	
	public void setResponse(String paramResponse) {
		response=paramResponse;
	}
	
	public String getResponse(InputBuffer paramInputBuffer) {
		return response;
	}

	public String getName() {
		return name;
	}

	public void setName(String paramString) {
		name = paramString;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setType(String paramString) {
		type = paramString;
	}

	public String getType() {
		return type;
	}

	public void setMessageId(int paramInt) {
		messageId = paramInt;
	}

	public ObjectNode toJsonNode() {
		return null;
	}

	public void configJsonNode(ObjectNode paramObjectNode) throws Exception {
	}
}
