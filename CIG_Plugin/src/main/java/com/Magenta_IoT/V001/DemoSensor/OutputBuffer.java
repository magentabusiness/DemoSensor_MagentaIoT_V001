package com.Magenta_IoT.V001.DemoSensor;

import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;

public abstract class OutputBuffer
{
  public OutputBuffer() {}
  
  protected byte[] buf = new byte['Ȁ'];
  protected int pos = 0;
  
  protected void checkLength(int paramInt) {
    if (pos + paramInt > buf.length) {
      throw new BufferOverflowException();
    }
  }
  
  public void putByte(int paramInt) {
    checkLength(1);
    buf[(pos++)] = ((byte)paramInt);
  }
  
  public abstract void putInt16(int paramInt);
  
  public abstract void putInt24(int paramInt);
  
  public abstract void putInt32(int paramInt);
  
  public void putByteArray(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null) {
      return;
    }
    
    putByteArray(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void putByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if ((paramArrayOfByte == null) || (paramInt2 == 0)) {
      return;
    }
    
    checkLength(paramInt2);
    System.arraycopy(paramArrayOfByte, paramInt1, buf, pos, paramInt2);
    pos += paramInt2;
  }
  
  public void putString(String paramString1, String paramString2) {
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = paramString1.getBytes(paramString2);
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      throw new IllegalStateException(localUnsupportedEncodingException);
    }
    
    putByteArray(arrayOfByte);
  }
  
  public byte[] toByteArray() {
    byte[] arrayOfByte = new byte[pos];
    System.arraycopy(buf, 0, arrayOfByte, 0, pos);
    return arrayOfByte;
  }
}

