//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * This class is a wrapper around ByteArrayInputStream. The DataSet package
 * needs access to the byte array itself when resolving.
 *
 * @author Jens Ole Lauridsen
 * @version 1.01 8/26/97
 * @see java.io.ByteArrayInputStream
 */
public class InputStreamToByteArray extends InputStream implements BytesGetter, Serializable {
  
  private static final long serialVersionUID = 42L;
  
  protected byte[] buf;
  transient protected int pos;
  transient protected int mark = 0;
  protected int count;
  
  /**
   * Constructs an InputStreamToByteArray object.
   *
   * @param buf
   *          The buffer into which the data is read.
   */
  public InputStreamToByteArray(byte buf[]) {
    this(buf, 0, buf.length);
  }
  
  public InputStreamToByteArray(byte buf[], int offset, int length) {
    this.buf = buf;
    this.pos = offset;
    this.count = Math.min(offset + length, buf.length);
    this.mark = offset;
  }
  
  public InputStreamToByteArray() {
  }

  private static Field byteArrayBufField;
  
  static {
    try {
      byteArrayBufField = ByteArrayInputStream.class.getDeclaredField("buf");
      byteArrayBufField.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public InputStreamToByteArray(InputStream stream) {
    try {
      if (stream instanceof ByteArrayInputStream) {
        buf = (byte[]) byteArrayBufField.get(stream);

      } else if (stream instanceof InputStreamToByteArray) {
        buf = ((InputStreamToByteArray) stream).buf;

      } else if (stream instanceof BytesGetter) {
        buf = ((BytesGetter) stream).getBytes();

      } else {
        try {
          if (stream.markSupported())
            stream.reset();
        } catch (Exception ex) {
          // Ignore!!
        }
        int size;
        while (stream != null && (size = stream.available()) > 0) {
          byte[] b = new byte[size];
          stream.read(b);
          if (buf == null)
            buf = b;
          else {
            byte[] temp = new byte[buf.length + b.length];
            System.arraycopy(buf, 0, temp, 0, buf.length);
            System.arraycopy(b, 0, temp, buf.length, b.length);
            buf = temp;
          }
        }
      }
      if (buf == null)
        buf = new byte[0];
      
      count = buf.length;
      
    } catch (Exception e) {
      buf = new byte[0];
      count = 0;
      e.printStackTrace();
    } finally {
      if (stream != null)
        try {
          stream.close();
        } catch (IOException e) {
        }
    }
  }
  
  @Override
  public byte[] getBytes() {
    return buf;
  }
  
  public int getSize() {
    return buf.length;
  }
  /**
   * A static method that returns an array of bytes representing the InputStream
   * with the specified stream.
   *
   * @param stream
   *          The input stream
   * @return An array of bytes
   * @throws IOException
   *           An I/O error occurred.
   */
  static public byte[] getBytes(InputStream stream) throws IOException {
    if (stream instanceof BytesGetter) {
      BytesGetter buffer = (BytesGetter) stream;
      return buffer.getBytes();
    } else {
      try (InputStreamToByteArray in = new InputStreamToByteArray(stream)) {
        return in.getBytes();
      }
    }
  }
  
  @Override
  public synchronized int read() {
    return (pos < count) ? (buf[pos++] & 0xff) : -1;
  }
  
  @Override
  public synchronized int read(byte b[], int off, int len) {
    if (b == null) {
      throw new NullPointerException();
    } else if (off < 0 || len < 0 || len > b.length - off) {
      throw new IndexOutOfBoundsException();
    }
    if (pos >= count) {
      return -1;
    }
    if (pos + len > count) {
      len = count - pos;
    }
    if (len <= 0) {
      return 0;
    }
    System.arraycopy(buf, pos, b, off, len);
    pos += len;
    return len;
  }
  
  @Override
  public synchronized long skip(long n) {
    if (pos + n > count) {
      n = count - pos;
    }
    if (n < 0) {
      return 0;
    }
    pos += n;
    return n;
  }
  
  @Override
  public synchronized int available() {
    return count - pos;
  }
  
  @Override
  public boolean markSupported() {
    return true;
  }
  
  @Override
  public void mark(int readAheadLimit) {
    mark = pos;
  }
  
  @Override
  public synchronized void reset() {
    pos = mark;
    mark = 0;
  }

  @Override
  public void close() throws IOException {
  }
}
