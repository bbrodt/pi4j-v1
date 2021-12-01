package com.pi4j.component.servo.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  MaestroServoDriver.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2021 Pi4J
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

import com.pi4j.component.servo.ServoDriver;
import com.pi4j.io.gpio.Pin;

/**
 * Servo Driver implementation for the Pololu Maestro series of Servo Controllers.
 * @see MaestroServoProvider for details.
 *
 * @author Bob Brodt (rbrodt@gmail.com)
 *
 */
public class MaestroServoDriver implements ServoDriver {

	public final static int MIN_PULSE_WIDTH = 800; // in usec
	public final static int MAX_PULSE_WIDTH = 2200; // in usec

    protected Pin servoPin;
    protected MaestroServoProvider provider;

	public MaestroServoDriver(MaestroServoProvider provider, Pin servoPin) {
        this.provider = provider;
        this.servoPin = servoPin;
	}

    public int getServoPulseWidth() {
        return provider.getServoPosition(servoPin.getAddress());
    }

    public void setServoPulseWidth(int width) {
        provider.setServoPosition(servoPin.getAddress(), width);
    }

    public int getServoPulseResolution() {
        return 100;
    }

    public void setSpeed(int value) {
    	provider.setSpeed(servoPin.getAddress(), value);
    }

    public void setAcceleration(int value) {
    	provider.setAcceleration(servoPin.getAddress(), value);
    }

    public boolean isMoving() {
    	return provider.isMoving();
    }

    public int getMinValue() {
    	// values for setValue() are in 1/4 usec intervals
    	return MIN_PULSE_WIDTH * 4;
    }

    public int getMaxValue() {
    	// values for setValue() are in 1/4 usec intervals
    	return MAX_PULSE_WIDTH * 4;
    }

    public Pin getPin() {
        return servoPin;
    }
}
