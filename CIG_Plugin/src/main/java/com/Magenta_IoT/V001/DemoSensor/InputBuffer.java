package com.Magenta_IoT.V001.DemoSensor;


import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;

public abstract class InputBuffer
{
  protected byte[] buf = null;
  protected int pos = 0;
  protected int limit = 0;
  
  public InputBuffer(byte[] paramArrayOfByte) {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public InputBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    buf = paramArrayOfByte;
    pos = paramInt1;
    limit = (paramInt1 + paramInt2);
  }
  
  protected void checkLength(int paramInt) {
    if (pos + paramInt > limit) {
      throw new BufferOverflowException();
    }
  }
  
  public int getByteSigned() {
    checkLength(1);
    int i = buf[(pos++)];
    return i;
  }
  
  public int getByte() {
    return getByteSigned() & 0xFF;
  }
  
  public abstract int getInt16();
  
  public abstract int getInt16Signed();
  
  public abstract int getInt24();
  
  public abstract int getInt24Signed();
  
  public abstract int getInt32();
  
  public abstract int getInt32Signed();
  
  public int getRemainingLength() {
    return limit - pos;
  }
  
  public byte[] getByteArray(int paramInt) {
    checkLength(paramInt);
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(buf, pos, arrayOfByte, 0, paramInt);
    pos += paramInt;
    return arrayOfByte;
  }
  
  public String getString(int paramInt, String paramString) {
    checkLength(paramInt);
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(buf, pos, arrayOfByte, 0, paramInt);
    pos += paramInt;
    try
    {
      return new String(arrayOfByte, paramString);
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      throw new IllegalStateException(localUnsupportedEncodingException);
    }
  }
}
