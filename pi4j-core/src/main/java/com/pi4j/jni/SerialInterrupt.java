package com.pi4j.jni;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  SerialInterrupt.java
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


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pi4j.util.NativeLibraryLoader;

/**
 * <p>
 * This class provides static methods to configure the native Pi4J library to listen to serial
 * interrupts and invoke callbacks into this class. Additionally, this class provides a listener
 * registration allowing Java consumers to subscribe to serial data receive events.
 * </p>
 *
 * @see <a href="https://pi4j.com/">https://pi4j.com/</a>
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 */
public class SerialInterrupt {

    private static Map<Integer, SerialInterruptListener> listeners = new ConcurrentHashMap<>();

    // private constructor
    private SerialInterrupt()  {
        // forbid object construction
    }

    static {
        // Load the platform library
        NativeLibraryLoader.load("libpi4j.so", "pi4j");
    }

    /**
     * <p>
     * This method is used to instruct the native code to setup a monitoring thread to monitor
     * interrupts that represent changes to the selected serial port.
     * </p>
     *
     * @param fileDescriptor the serial file descriptor/handle
     * @return A return value of a negative number represents an error. A return value of '0'
     *         represents success and that the serial port is already being monitored. A return value
     *         of '1' represents success and that a new monitoring thread was created to handle the
     *         requested serial port.
     */
    public static native int enableSerialDataReceiveCallback(int fileDescriptor);

    /**
     * <p>
     * This method is used to instruct the native code to stop the monitoring thread monitoring
     * interrupts on the selected serial port.
     * </p>
     *
     * @param fileDescriptor the serial file descriptor/handle

     * @return A return value of a negative number represents an error. A return value of '0'
     *         represents success and that no existing monitor was previously running. A return
     *         value of '1' represents success and that an existing monitoring thread was stopped
     *         for the requested serial port.
     */
    public static native int disableSerialDataReceiveCallback(int fileDescriptor);

    /**
     * <p>
     * This method is provided as the callback handler for the Pi4J native library to invoke when a
     * GPIO interrupt is detected. This method should not be called from any Java consumers. (Thus
     * is is marked as a private method.)
     * </p>
     *
     * @param fileDescriptor the serial file descriptor/handle
     * //@param data byte array of data received on this event from the serial receive buffer
     */
    private static void onDataReceiveCallback(int fileDescriptor, byte[] data) {

        // notify event listeners
        if(listeners.containsKey(fileDescriptor)){
            SerialInterruptListener listener = listeners.get(fileDescriptor);
            if(listener != null) {
                SerialInterruptEvent event = new SerialInterruptEvent(listener, fileDescriptor, data);
                listener.onDataReceive(event);
            }
        }

        //System.out.println("SERIAL PORT [" + fileDescriptor + "] DATA LENGTH = " + data.length + " / " + new String(data));
    }

    /**
     * <p>
     * Java consumer code can all this method to register itself as a listener for pin state
     * changes.
     * </p>
     *
     * @see com.pi4j.jni.SerialInterruptListener
     * @see com.pi4j.jni.SerialInterruptEvent
     *
     * @param fileDescriptor the serial file descriptor/handle
     * @param listener A class instance that implements the GpioInterruptListener interface.
     */
    public static synchronized void addListener(int fileDescriptor, SerialInterruptListener listener) {
        if (!listeners.containsKey(fileDescriptor)) {
            listeners.put(fileDescriptor, listener);
            enableSerialDataReceiveCallback(fileDescriptor);
        }
    }

    /**
     * <p>
     * Java consumer code can all this method to unregister itself as a listener for pin state
     * changes.
     * </p>
     *
     * @see com.pi4j.jni.SerialInterruptListener
     * @see com.pi4j.jni.SerialInterruptEvent
     *
     * @param fileDescriptor the serial file descriptor/handle
     */
    public static synchronized void removeListener(int fileDescriptor) {
        if (listeners.containsKey(fileDescriptor)) {
            listeners.remove(fileDescriptor);
            disableSerialDataReceiveCallback(fileDescriptor);
        }
    }


    /**
     * <p>
     * Returns true if the listener is already registered for event callbacks.
     * </p>
     *
     * @see com.pi4j.jni.SerialInterruptListener
     * @see com.pi4j.jni.SerialInterruptEvent
     *
     * @param fileDescriptor the serial file descriptor/handle
     */
    public static synchronized boolean hasListener(int fileDescriptor) {
        return listeners.containsKey(fileDescriptor);
    }
}
