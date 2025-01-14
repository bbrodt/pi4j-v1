package com.pi4j.component.sensor;

import com.pi4j.component.ComponentListener;
import com.pi4j.component.ObserveableComponentBase;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  AnalogSensorBase.java
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


public abstract class AnalogSensorBase extends ObserveableComponentBase implements AnalogSensor {

    @Override
    public void addListener(AnalogSensorListener... listener) {
        super.addListener(listener);
    }

    @Override
    public synchronized void removeListener(AnalogSensorListener... listener) {
        super.removeListener(listener);
    }

    protected synchronized void notifyListeners(AnalogSensorValueChangeEvent event) {
        for(ComponentListener listener : super.listeners) {
            ((AnalogSensorListener)listener).onValueChange(event);
        }
    }

    @Override
    public boolean isValue(double value)
    {
        return (getValue() == value);
    }

    @Override
    public boolean isValueInRange(double min, double max)
    {
        double value = getValue();
        return (value >= min && value <= max);
    }

}
