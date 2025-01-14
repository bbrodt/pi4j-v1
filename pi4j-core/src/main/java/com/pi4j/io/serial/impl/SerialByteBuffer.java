package com.pi4j.io.serial.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  SerialByteBuffer.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2024 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;

/**
 * This class implements a dynamic expanding byte buffer to
 * accommodate new data received from the serial port
 *
 * Adapted from sources at:
 * http://ostermiller.org/utils/src/CircularByteBuffer.java.html
 * Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 */
public class SerialByteBuffer {

    public static int DEFAULT_BUFFER_SCALE_FACTOR = 2;
    public static int DEFAULT_INITIAL_BUFFER_SIZE = 4096;

    private InputStream stream = new SerialByteBufferInputStream();
    private volatile int writeIndex = 0;
    private volatile int readIndex = 0;
    private byte[] buffer;

    public SerialByteBuffer(){
        // initialize buffer with default capacity
        this(DEFAULT_INITIAL_BUFFER_SIZE);
    }

    public SerialByteBuffer(int initialCapacity){
        // initialize buffer with user provided capacity
        buffer = new byte[initialCapacity];
    }

    public synchronized void clear(){
        // reset read and write index pointers
        readIndex = writeIndex = 0;
    }

    public InputStream getInputStream(){
        // return the input stream
        return stream;
    }

    public synchronized int capacity(){
        // return the buffer's total capacity
        return buffer.length;
    }

    public synchronized int remaining(){
        // return the number of (unused) bytes still available in the current buffer's capacity
        if (writeIndex < readIndex)
            return (readIndex - writeIndex - 1);
        return ((buffer.length - 1) - (writeIndex - readIndex));
    }

    public synchronized int available(){
        // return the number of bytes that are ready to be read
        if (readIndex <= writeIndex)
            return (writeIndex - readIndex);
        return (buffer.length - (readIndex - writeIndex));
    }

    private synchronized void resize(int length) {
        int min_capacity = buffer.length + length;
        int new_capacity = buffer.length * DEFAULT_BUFFER_SCALE_FACTOR;

        // double the capacity until the buffer is large enough to accommodate the new demand
        while (new_capacity < min_capacity) {
            new_capacity *= DEFAULT_BUFFER_SCALE_FACTOR;
        }

        // create a new buffer that can hold the newly determined
        // capacity and copy the bytes from the old buffer into the new buffer
        byte[] new_buffer = new byte[new_capacity];

        // 0123456789012345678901234567890123
        //              R
        //              W
        // -------------|--------------------

        // if the write index equals the read index, then there is no data in the old buffer that
        // is un-read and there is no need to copy any data from the old buffer to the new resized
        // buffer ... we can simply reset both the read index and write index to the zero position
        // in the new buffer
        if (writeIndex == readIndex) {
            readIndex = 0;  // reset READ pointer
            writeIndex = 0; // reset WRITE pointer
        }

        // 0123456789012345678901234567890123
        //        R     W
        // -------|XXXXX|--------------------

        // if the write index is greater than the read index, then the write data has not wrapped
        // back to the beginning of the circular buffer ... we can simply copy the remaining un-read
        // data in the original buffer to the new buffer and reset the read buffer to the zero index
        // and adjust the write buffer to the new position in the new buffer
        else if (writeIndex > readIndex) {
            // copy single payload (non-wrapping) from original buffer
            System.arraycopy(buffer, readIndex, new_buffer, 0, writeIndex-readIndex);
            readIndex = 0;                     // reset READ pointer
            writeIndex = writeIndex-readIndex; // adjust WRITE pointer
        }

        // 0123456789012345678901234567890123
        //        W                     R
        // XXXXXXX|---------------------|XXXX

        // it is possible that the write index can be a lower value than the read index; this happens
        // when a write operation on the circular buffer has wrapped the end of the buffer and started
        // writing bytes at the beginning free space ... in this case we need to copy both the un-read
        // data from the read index to the end of the buffer and data at the beginning of the buffer
        // up to the write index
        else {
            // copy two payloads (wrapping) from original circular buffer
            int dataLength = buffer.length - readIndex;
            System.arraycopy(buffer, readIndex, new_buffer, 0, dataLength); // copy end buffer data to new buffer
            System.arraycopy(buffer, 0, new_buffer, dataLength, writeIndex); // copy start buffer data to new buffer
            readIndex = 0;                      // reset READ pointer
            writeIndex = dataLength+writeIndex; // adjust WRITE pointer
        }

//        System.out.println(" - READ INDEX   - " + readIndex);
//        System.out.println(" - WRITE INDEX  - " + writeIndex);
//        System.out.println(" - NEW LENGTH   - " + length);
//        System.out.println(" - MIN CAPACITY - " + min_capacity);
//        System.out.println(" - NEW CAPACITY - " + new_capacity);
//        System.out.println(" - OLD BUFFER   - " + new String(buffer));
//        System.out.println(" - NEW BUFFER   - " + new String(new_buffer));

        // update buffer object reference
        // old buffer should get garbage collected
        buffer = new_buffer;
    }

    public void write(byte[] data) throws IOException, BufferOverflowException {
        write(data, 0, data.length);
    }

    public void write(byte[] data, int offset, int length) throws IOException {
        while (length > 0) {
            int remaining_space = remaining();
            if(remaining_space < length) {
                resize(length);
            }
            int realLen = Math.min(length, remaining_space);
            int firstLen = Math.min(realLen, buffer.length - writeIndex);
            int secondLen = Math.min(realLen - firstLen, buffer.length - readIndex - 1);
            int written = firstLen + secondLen;
            if (firstLen > 0) {
                System.arraycopy(data, offset, buffer, writeIndex, firstLen);
            }
            if (secondLen > 0) {
                System.arraycopy(data, offset + firstLen, buffer, 0, secondLen);
                writeIndex = secondLen;
            } else {
                writeIndex += written;
            }
            if (writeIndex == buffer.length) {
                writeIndex = 0;
            }
            offset += written;
            length -= written;
        }
        if (length > 0) {
            try {
                Thread.sleep(100);
            } catch (Exception x) {
                throw new IOException("Waiting for available space in buffer interrupted.");
            }
        }
    }

    protected class SerialByteBufferInputStream extends InputStream {

        @Override
        public int available() throws IOException {
            synchronized (SerialByteBuffer.this){
                return (SerialByteBuffer.this.available());
            }
        }

        @Override
        public int read() throws IOException {
            while (true){
                synchronized (SerialByteBuffer.this){
                    int available = SerialByteBuffer.this.available();
                    if (available > 0){
                        int result = buffer[readIndex] & 0xff; // we only care about fist 8 bits
                        readIndex++; // increment read index position
                        // if the read index reaches the maximum buffer capacity, then rollover to zero index
                        if (readIndex == buffer.length)
                            readIndex = 0;
                        return result;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch(Exception x){
                    throw new IOException("Blocking read operation interrupted.");
                }
            }
        }

        @Override
        public int read(byte[] data) throws IOException {
            return read(data, 0, data.length);
        }

        @Override
        public int read(byte[] data, int off, int len) throws IOException {
            while (true){
                synchronized (SerialByteBuffer.this){
                    int available = SerialByteBuffer.this.available();
                    if (available > 0){
                        int length = Math.min(len, available);
                        int firstLen = Math.min(length, buffer.length - readIndex);
                        int secondLen = length - firstLen;
                        System.arraycopy(buffer, readIndex, data, off, firstLen);
                        if (secondLen > 0){
                            System.arraycopy(buffer, 0, data, off+firstLen,  secondLen);
                            readIndex = secondLen;
                        } else {
                            readIndex += length;
                        }
                        if (readIndex == buffer.length) {
                            readIndex = 0;
                        }

                        return length;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch(Exception x){
                    throw new IOException("Blocking read operation interrupted.");
                }
            }
        }

        @Override
        public long skip(long n) throws IOException, IllegalArgumentException {
            while (true){
                synchronized (SerialByteBuffer.this){
                    int available = SerialByteBuffer.this.available();
                    if (available > 0){
                        int length = Math.min((int)n, available);
                        int firstLen = Math.min(length, buffer.length - readIndex);
                        int secondLen = length - firstLen;
                        if (secondLen > 0){
                            readIndex = secondLen;
                        } else {
                            readIndex += length;
                        }
                        if (readIndex == buffer.length) {
                            readIndex = 0;
                        }
                        return length;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch(Exception x){
                    throw new IOException("Blocking read operation interrupted.");
                }
            }
        }
    }
}
