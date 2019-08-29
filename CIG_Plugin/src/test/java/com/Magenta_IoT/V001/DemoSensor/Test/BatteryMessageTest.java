package com.Magenta_IoT.V001.DemoSensor.Test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.Magenta_IoT.V001.DemoSensor.ProtocolAdapterImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huawei.m2m.cig.tup.modules.protocol_adapter.IProtocolAdapter;

public class BatteryMessageTest {
	private IProtocolAdapter protocolAdapter;

    @Before
    public void setProtocolAdapter() {
        this.protocolAdapter = new ProtocolAdapterImpl();
    }
    
    @Test
    public void testBatteryMessage() throws Exception {
    	String data="0A3C0E42";
    	byte[] deviceReqByte = hexStringToByteArray(data);
    	
    	ObjectNode objectNode = protocolAdapter.decode(deviceReqByte);
    	int batteryLevel=objectNode.get("data").get(0).get("serviceData").get("batteryLevel").asInt();
        int batteryVoltage=objectNode.get("data").get(0).get("serviceData").get("batteryVoltage").asInt();
    	assertEquals(60, batteryLevel);
    	assertEquals(3650, batteryVoltage);
    	
    	ObjectMapper mapper = new ObjectMapper();
        ObjectNode cloudRspObjectNode = mapper.createObjectNode();
        cloudRspObjectNode.put("identifier", "123");
        cloudRspObjectNode.put("msgType", "cloudRsp");
        cloudRspObjectNode.put("request", deviceReqByte);
    	byte[] resp = protocolAdapter.encode(cloudRspObjectNode);
    	assertEquals(10, resp[0]);
    	assertEquals(0, resp[1]);
    	
    }
    
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    

}
