/*
 * Copyright 2008 Android4ME
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package android.content.res;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple helper class that allows reading of integers.
 * 
 * TODO: implement buffering
 * 
 * @author Dmitry Skiba
 */
public final class IntReader {

  private InputStream stream;
  private boolean bigEndian;
  private int position;

  public IntReader() {}

  public IntReader(InputStream stream, boolean bigEndian) {
    reset(stream, bigEndian);
  }

  /**
   * Reset the POJO to use a new stream.
   * 
   * @param newStream the {@code InputStream} to use
   * @param isBigEndian a boolean for whether or not the stream is in Big Endian format
   */
  public final void reset(InputStream newStream, boolean isBigEndian) {
    stream = newStream;
    bigEndian = isBigEndian;
    position = 0;
  }

  /**
   * Close the current stream being used by the POJO.
   */
  public final void close() {
    if (stream == null) {
      return;
    }

    try {
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    reset(null, false);
  }

  public final InputStream getStream() {
    return stream;
  }

  public final boolean isBigEndian() {
    return bigEndian;
  }

  public final void setBigEndian(boolean isBigEndian) {
    bigEndian = isBigEndian;
  }

  public final int readByte() throws IOException {
    return readInt(1);
  }

  public final int readShort() throws IOException {
    return readInt(2);
  }

  public final int readInt() throws IOException {
    return readInt(4);
  }

  /**
   * Read an integer of a certain length from the current stream.
   * 
   * @param length to read
   * @return
   * @throws IOException
   */
  public final int readInt(int length) throws IOException {
    if ((length < 0) || (length > 4)) {
      throw new IllegalArgumentException();
    }
    int result = 0;
    int byteRead = 0;
    if (bigEndian) {
      for (int i = (length - 1) * 8; i >= 0; i -= 8) {
        byteRead = stream.read();
        if (byteRead == -1) {
          throw new EOFException();
        }
        position += 1;
        result |= (byteRead << i);
      }
    } else {
      length *= 8;
      for (int i = 0; i != length; i += 8) {
        byteRead = stream.read();
        if (byteRead == -1) {
          throw new EOFException();
        }
        position += 1;
        result |= (byteRead << i);
      }
    }
    return result;
  }

  /**
   * Return an integer array of a certain length from current offset.
   * 
   * @param length
   * @return
   * @throws IOException
   */
  public final int[] readIntArray(int length) throws IOException {
    int[] array = new int[length];

    readIntArray(array, 0, length);

    return array;
  }

  /**
   * Read (store) an integer array of a specific length and offset.
   * 
   * @param array
   * @param offset
   * @param length
   * @throws IOException
   */
  public final void readIntArray(int[] array, int offset, int length) throws IOException {
    for (; length > 0; length -= 1) {
      array[offset++] = readInt();
    }
  }

  /**
   * Read and return a byte array of a specific length.
   * 
   * @param length
   * @return
   * @throws IOException
   */
  public final byte[] readByteArray(int length) throws IOException {
    byte[] array = new byte[length];
    int read = stream.read(array);
    position += read;

    if (read != length) {
      throw new EOFException();
    }

    return array;
  }

  /**
   * Skip a specific number of bytes in the stream.
   * 
   * @param bytes
   * @throws IOException
   */
  public final void skip(int bytes) throws IOException {
    if (bytes > 0) {
      long skipped = stream.skip(bytes);
      position += skipped;
      if (skipped != bytes) {
        throw new EOFException();
      }
    }
  }

  public final void skipInt() throws IOException {
    skip(4);
  }

  public final int available() throws IOException {
    return stream.available();
  }

  public final int getPosition() {
    return position;
  }
}
