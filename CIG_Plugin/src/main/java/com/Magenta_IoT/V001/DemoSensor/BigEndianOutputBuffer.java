package com.Magenta_IoT.V001.DemoSensor;

public class BigEndianOutputBuffer extends OutputBuffer {
	public BigEndianOutputBuffer() {
	}

	public void putInt16(int paramInt) {
		putByte((paramInt & 0xFF00) >> 8);
		putByte(paramInt & 0xFF);
	}

	public void putInt24(int paramInt) {
		putByte((paramInt & 0xFF0000) >> 16);
		putByte((paramInt & 0xFF00) >> 8);
		putByte(paramInt & 0xFF);
	}

	public void putInt32(int paramInt) {
		putByte((paramInt & 0xFF000000) >> 24);
		putByte((paramInt & 0xFF0000) >> 16);
		putByte((paramInt & 0xFF00) >> 8);
		putByte(paramInt & 0xFF);
	}
}
