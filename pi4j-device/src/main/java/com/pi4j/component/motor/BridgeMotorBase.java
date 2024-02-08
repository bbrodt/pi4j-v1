package com.pi4j.component.motor;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  BridgeMotorBase.java
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

public abstract class BridgeMotorBase extends MotorBase implements BridgeMotor {
    private String name;
    private MotorState currentState = MotorState.STOP;
    private float speed;

    public BridgeMotorBase(String name) {
    	this.name = name;
    }

    public void setName(String name) {
    	this.name = name;
    }

    public String getName() {
    	return name;
    }

    public void setSpeed(float speed) {
    	this.speed = speed;
    }

    public float getSpeed() {
    	return speed;
    }

	@Override
	public void setState(MotorState state) {
		currentState = state;
	}

 	@Override
 	public MotorState getState() {
         return currentState;
 	}
}
