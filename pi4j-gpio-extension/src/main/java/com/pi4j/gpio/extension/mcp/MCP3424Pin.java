package com.pi4j.gpio.extension.mcp;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.impl.PinImpl;

import java.util.EnumSet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: GPIO Extension
 * FILENAME      :  MCP3424Pin.java
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

/**
 *
 * <p>
 * This GPIO provider implements the MCP3424 I2C GPIO expansion board as native Pi4J GPIO pins. It is a 18-bit
 * delta/sigma ADC providing 4 input channels. More information about the board can be found here:
 * http://ww1.microchip.com/downloads/en/DeviceDoc/22088c.pdf
 * </p>
 *
 * <p>
 * The MCP3424 is connected via I2C connection to the Raspberry Pi and provides 4 analog input channels.
 * </p>
 *
 * @author Alexander Falkenstern
 */
public class MCP3424Pin {

    public static final Pin GPIO_CH0 = createAnalogInputPin(0, "ANALOG INPUT 0");
    public static final Pin GPIO_CH1 = createAnalogInputPin(1, "ANALOG INPUT 1");
    public static final Pin GPIO_CH2 = createAnalogInputPin(2, "ANALOG INPUT 2");
    public static final Pin GPIO_CH3 = createAnalogInputPin(3, "ANALOG INPUT 3");

    public static Pin[] ALL_PINS = { MCP3424Pin.GPIO_CH0,
                                     MCP3424Pin.GPIO_CH1,
                                     MCP3424Pin.GPIO_CH2,
                                     MCP3424Pin.GPIO_CH3 };

    private static Pin createAnalogInputPin(int channel, String name) {
        return new PinImpl(MCP3424GpioProvider.NAME, channel, name, EnumSet.of(PinMode.ANALOG_INPUT));
    }
}
