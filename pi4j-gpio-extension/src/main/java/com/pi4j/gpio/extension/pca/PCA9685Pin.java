package com.pi4j.gpio.extension.pca;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: GPIO Extension
 * FILENAME      :  PCA9685Pin.java
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
import java.util.EnumSet;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.impl.PinImpl;

/**
 * Represents all of the 16 PWM channels provided by the PCA9685 I2C 12-bit PWM LED/Servo controller.
 *
 * @author Christian Wehrli
 * @see PCA9685GpioProvider
 */
public class PCA9685Pin {

    public static final Pin PWM_00 = createPwmPin(0, "PWM 0");
    public static final Pin PWM_01 = createPwmPin(1, "PWM 1");
    public static final Pin PWM_02 = createPwmPin(2, "PWM 2");
    public static final Pin PWM_03 = createPwmPin(3, "PWM 3");
    public static final Pin PWM_04 = createPwmPin(4, "PWM 4");
    public static final Pin PWM_05 = createPwmPin(5, "PWM 5");
    public static final Pin PWM_06 = createPwmPin(6, "PWM 6");
    public static final Pin PWM_07 = createPwmPin(7, "PWM 7");
    public static final Pin PWM_08 = createPwmPin(8, "PWM 8");
    public static final Pin PWM_09 = createPwmPin(9, "PWM 9");
    public static final Pin PWM_10 = createPwmPin(10, "PWM 10");
    public static final Pin PWM_11 = createPwmPin(11, "PWM 11");
    public static final Pin PWM_12 = createPwmPin(12, "PWM 12");
    public static final Pin PWM_13 = createPwmPin(13, "PWM 13");
    public static final Pin PWM_14 = createPwmPin(14, "PWM 14");
    public static final Pin PWM_15 = createPwmPin(15, "PWM 15");

    public static final Pin[] ALL = {
            PCA9685Pin.PWM_00,
            PCA9685Pin.PWM_01,
            PCA9685Pin.PWM_02,
            PCA9685Pin.PWM_03,
            PCA9685Pin.PWM_04,
            PCA9685Pin.PWM_05,
            PCA9685Pin.PWM_06,
            PCA9685Pin.PWM_07,
            PCA9685Pin.PWM_08,
            PCA9685Pin.PWM_09,
            PCA9685Pin.PWM_10,
            PCA9685Pin.PWM_11,
            PCA9685Pin.PWM_12,
            PCA9685Pin.PWM_13,
            PCA9685Pin.PWM_14,
            PCA9685Pin.PWM_15};

    private static Pin createPwmPin(int channel, String name) {
        return new PinImpl(PCA9685GpioProvider.NAME, channel, name, EnumSet.of(PinMode.PWM_OUTPUT));
    }
}
