package com.pi4j.component.servo.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  RPIServoBlasterProviderTest.java
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

/*
 * Class:     RPIServoBlasterProviderTest
 * Created:   5th February 2017
 *
 * @author Sam Hough
 * @version 1.1, 5th February 2017
 */
public class RPIServoBlasterProviderTest {
    private String resolvePhysical(Pin pin) {
        return RPIServoBlasterProvider.PIN_MAP.get(pin);
    }

    @Test
    public void wiringPi30Mapping() {
        assertEquals("P1-27", resolvePhysical(RaspiPin.GPIO_30));
    }

    @Test
    public void wiringPi31Mapping() {
        assertEquals("P1-28", resolvePhysical(RaspiPin.GPIO_31));
    }

    @Test
    public void wiringPi21Mapping() {
        assertEquals("P1-29", resolvePhysical(RaspiPin.GPIO_21));
    }

    @Test
    public void wiringPi22Mapping() {
        assertEquals("P1-31", resolvePhysical(RaspiPin.GPIO_22));
    }

    @Test
    public void wiringPi26Mapping() {
        assertEquals("P1-32", resolvePhysical(RaspiPin.GPIO_26));
    }

    @Test
    public void wiringPi23Mapping() {
        assertEquals("P1-33", resolvePhysical(RaspiPin.GPIO_23));
    }

    @Test
    public void wiringPi24Mapping() {
        assertEquals("P1-35", resolvePhysical(RaspiPin.GPIO_24));
    }

    @Test
    public void wiringPi27Mapping() {
        assertEquals("P1-36", resolvePhysical(RaspiPin.GPIO_27));
    }

    @Test
    public void wiringPi25Mapping() {
        assertEquals("P1-37", resolvePhysical(RaspiPin.GPIO_25));
    }

    @Test
    public void wiringPi28Mapping() {
        assertEquals("P1-38", resolvePhysical(RaspiPin.GPIO_28));
    }

    @Test
    public void wiringPi29Mapping() {
        assertEquals("P1-40", resolvePhysical(RaspiPin.GPIO_29));
    }
}
