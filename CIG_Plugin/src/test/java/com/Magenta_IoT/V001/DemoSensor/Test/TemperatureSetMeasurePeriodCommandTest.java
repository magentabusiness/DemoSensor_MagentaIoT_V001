package com.Magenta_IoT.V001.DemoSensor.Test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.Magenta_IoT.V001.DemoSensor.BigEndianInputBuffer;
import com.Magenta_IoT.V001.DemoSensor.ProtocolAdapterImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huawei.m2m.cig.tup.modules.protocol_adapter.IProtocolAdapter;

public class TemperatureSetMeasurePeriodCommandTest {
	
	private IProtocolAdapter protocolAdapter;

    @Before
    public void setProtocolAdapter() {
        this.protocolAdapter = new ProtocolAdapterImpl();
    }
    
    @Test
    public void TemperatureSetMeasurePeriodCommand() throws Exception {
    	int exp_value=3493;
    	int exp_mid=2222;
    	int exp_msgId=3;
    	
    	ObjectMapper mapper = new ObjectMapper();
        ObjectNode cloudReqObjectNode = mapper.createObjectNode();
        ObjectNode paras = mapper.createObjectNode();
        paras.put("value", exp_value);
        cloudReqObjectNode.put("identifier", "123");
        cloudReqObjectNode.put("msgType", "cloudReq");
        cloudReqObjectNode.put("cmd", "SET_MEASURE_PERIOD");
        cloudReqObjectNode.set("paras", paras);
        cloudReqObjectNode.put("hasMore", 0);
        cloudReqObjectNode.put("mid", exp_mid);
        
        byte[] outputByte = protocolAdapter.encode(cloudReqObjectNode);
        
        BigEndianInputBuffer bufOutput= new BigEndianInputBuffer(outputByte);
        int msgId=bufOutput.getByte();
        int value=bufOutput.getInt16();
        int mid=bufOutput.getInt16();
        
        assertEquals(exp_msgId, msgId);
        assertEquals(exp_value, value);
        assertEquals(exp_mid, mid);
        
        //System.out.println("mid: "+mid);
        //System.out.println("Resp: "+Arrays.toString(outputByte));
    }
    
    @Test
    public void TemperatureSetMeasurePeriodResponse() throws Exception {
    	String data="030008AE000A";
    	byte[] deviceRspByte = hexStringToByteArray(data);
    	
    	 ObjectNode objectNode = protocolAdapter.decode(deviceRspByte);
    	 
    	 int errcode=objectNode.get("errcode").asInt();
    	 int mid = objectNode.get("mid").asInt();
    	 int result = objectNode.get("body").get("result").asInt();
    	 
    	 assertEquals(0x0,errcode);
    	 assertEquals(0x08AE, mid);
    	 assertEquals(0xA, result);
    	 
         //String str = objectNode.toString();
         //System.out.println("Resp: "+str);
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
