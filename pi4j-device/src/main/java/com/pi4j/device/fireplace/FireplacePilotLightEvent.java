package com.pi4j.device.fireplace;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  FireplacePilotLightEvent.java
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


import java.util.EventObject;

public class FireplacePilotLightEvent extends EventObject {

	private static final long serialVersionUID = 7591655670928344605L;
	protected final boolean isPilotLightOn;

    public FireplacePilotLightEvent(Fireplace fireplaceComponent, boolean isPilotLightOn) {
        super(fireplaceComponent);
        this.isPilotLightOn = isPilotLightOn;
    }

    public Fireplace getFireplace() {
        return (Fireplace)getSource();
    }

    public boolean isPilotLightOn() {
        return isPilotLightOn;
    }
    public boolean isPilotLightOff() {
        return !isPilotLightOn;
    }
}
