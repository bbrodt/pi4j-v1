package com.pi4j.component.motor.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  GpioDualMotorBridgeComponent.java
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

import com.pi4j.component.motor.DualMotorBridgeBase;
import com.pi4j.component.motor.MotorState;
import com.pi4j.component.motor.SpeedVector;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class GpioDualMotorBridgeComponent extends DualMotorBridgeBase {

    public GpioDualMotorBridgeComponent(GpioPin motor1pins[], GpioPin motor2pins[]) {
    	motor1 = new GpioBridgeMotor("LEFT", motor1pins);
    	motor2 = new GpioBridgeMotor("RIGHT", motor2pins);
    }


	@Override
	public void setSpeedVector(SpeedVector vector) {
		float m1 = 0, m2 = 0;
		MotorState s1 = MotorState.STOP, s2 = MotorState.STOP;
		super.setSpeedVector(vector);
		if (vector.magnitude>0) {
			if (vector.direction>=0 && vector.direction<90) {
				m1 = vector.magnitude * Sin(2*vector.direction);
				if (m1<0) {
					m1 = -m1;
					s1 = MotorState.REVERSE;
				}
				else
					s1 = MotorState.FORWARD;
				m2 = vector.magnitude;
				s2 = MotorState.FORWARD;
			}
			else if (vector.direction>=90 && vector.direction<180) {
				m1 = -vector.magnitude * Sin(2*vector.direction);
				s1 = MotorState.REVERSE;
				if (m1<0) {
					m1 = -m1;
				}
				m2 = vector.magnitude;
				s2 = MotorState.REVERSE;
			}
			else if (vector.direction>=180 && vector.direction<270) {
				m2 = vector.magnitude * Sin(2*vector.direction);
				s2 = MotorState.REVERSE;
				if (m2<0) {
					m2 = -m2;
				}
				m1 = vector.magnitude;
				s1 = MotorState.REVERSE;
			}
			else if (vector.direction>=270 && vector.direction<=360) {
				m2 = vector.magnitude * Sin(2*vector.direction);
				if (m2<0) {
					m2 = -m2;
					s2 = MotorState.REVERSE;
				}
				else
					s2 = MotorState.FORWARD;
				m1 = vector.magnitude;
				s1 = MotorState.FORWARD;
			}
		}
		if (m1==0)
			s1 = MotorState.STOP;
		motor1.setSpeed(m1);
		motor1.setState(s1);

		if (m2==0)
			s2 = MotorState.STOP;
		motor2.setSpeed(m2);
		motor2.setState(s2);
	}

	public MotorState getState(int id) {
		if (id==0)
			return motor1.getState();
		else if (id==1)
			return motor1.getState();
		return MotorState.STOP;
	}

	float Sin(float value) {
		// value = heading angle (0=north, 90=east, 180=south 270=west)
		// convert to trignometric angle (0=east, 90=north, etc.)
		value = (360-value+90) % 360;
		return (float)Math.sin(Math.toRadians(value));
	}

	float Cos(float value) {
		value = (360-value+90) % 360;
		return (float)Math.cos(Math.toRadians(value));
	}
}
