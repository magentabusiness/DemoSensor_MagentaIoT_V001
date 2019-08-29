package com.Magenta_IoT.V001.DemoSensor;

public class BigEndianInputBuffer extends InputBuffer {
	public BigEndianInputBuffer(byte[] paramArrayOfByte) {
		super(paramArrayOfByte);
	}

	public BigEndianInputBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
		super(paramArrayOfByte);
	}

	public int getInt16() {
		int i = getByte();
		int j = getByte();

		return i << 8 | j;
	}

	public int getInt16Signed() {
		int i = getByteSigned();
		int j = getByte();

		return i << 8 | j;
	}

	public int getInt24() {
		int i = getByte();
		int j = getByte();
		int k = getByte();

		return i << 16 | j << 8 | k;
	}

	public int getInt24Signed() {
		int i = getByteSigned();
		int j = getByte();
		int k = getByte();

		return i << 16 | j << 8 | k;
	}

	public int getInt32() {
		int i = getByte();
		int j = getByte();
		int k = getByte();
		int m = getByte();

		return i << 24 | j << 16 | k << 8 | m;
	}

	public int getInt32Signed() {
		int i = getByteSigned();
		int j = getByte();
		int k = getByte();
		int m = getByte();

		return i << 24 | j << 16 | k << 8 | m;
	}
}
