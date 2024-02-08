package com.pi4j.component.motor.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  GpioBridgeMotor.java
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

import com.pi4j.component.motor.BridgeMotorBase;
import com.pi4j.component.motor.MotorBase;
import com.pi4j.component.motor.MotorState;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;

class GpioBridgeMotor extends BridgeMotorBase {
	private static int PWM_RANGE = 100;
    private GpioPinDigitalOutput outPin1;
    private GpioPinDigitalOutput outPin2;
    private GpioPinPwmOutput pwmPin;

    public GpioBridgeMotor(GpioPin pins[]) {
    	this("MOTOR1", pins);
    }

    public GpioBridgeMotor(String name, GpioPin pins[]) {
    	super(name);
    	outPin1 = (GpioPinDigitalOutput)pins[0];
    	outPin2 = (GpioPinDigitalOutput)pins[1];
    	pwmPin = (GpioPinPwmOutput)pins[2];
    	pwmPin.setPwmRange(PWM_RANGE);
    }

	@Override
	public void setState(MotorState state) {

		super.setState(state);

        switch(state) {
            case STOP: {
                // turn all GPIO pins to OFF state
                outPin1.setState(PinState.LOW);
                outPin2.setState(PinState.LOW);
                setSpeed(0f);

                break;
            }
            case FORWARD: {
            	outPin1.setState(PinState.HIGH);
            	outPin2.setState(PinState.LOW);

                break;
            }
            case REVERSE: {
            	outPin1.setState(PinState.LOW);
            	outPin2.setState(PinState.HIGH);

                break;
            }
            default: {
                throw new UnsupportedOperationException("Cannot set motor state: " + state.toString());
            }
        }
        pwmPin.setPwm((int)(getSpeed()*PWM_RANGE));

        try {
        	Thread.sleep(50);
        }
        catch (Exception e) {

        }
	}
}
