package com.pi4j.component.temperature.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  TmpDS18S20DeviceType.java
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


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.TemperatureSensorBase;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1DeviceType;
import com.pi4j.temperature.TemperatureScale;

/**
 * DS18B20 - Programmable Resolution 1-Wire Digital Thermometer.
 * <p/>
 * See https://www.maximintegrated.com/en/products/analog/sensors-and-sensor-interface/DS18B20.html
 *
 * @author Peter Schuebl
 */
public class TmpDS18S20DeviceType implements W1DeviceType {

    public static final int FAMILY_CODE = 0x10;

    @Override
    public int getDeviceFamilyCode() {
        return FAMILY_CODE;
    }

    @Override
    public Class<? extends W1Device> getDeviceClass() {
        return TmpDS18S20.class;
    }

    @Override
    public TmpDS18S20 create(final File deviceDir) {
        return new TmpDS18S20(deviceDir);
    }

    /*
    use w1Master.getDevices(TmpDS18B20.class) instead

    public static List<TmpDS18B20> getDevices(final W1Master master) {
        final List<TmpDS18B20> devices = new ArrayList<>();
        for (W1Device device : master.getDevices(FAMILY_CODE)) {
            devices.add((TmpDS18B20) device);
        }
        return devices;
    }
    */

    static class TmpDS18S20 extends TemperatureSensorBase implements TemperatureSensor, W1Device {

        private final Logger log = Logger.getLogger(TmpDS18S20.class.getName());

        private final String id;

        private File deviceDir;

        private double lastGoodTemperature = Double.NaN;

        // (?s).*crc=[0-9a-f]+ ([A-Z]+).*t=([0-9]+)
        private final Pattern VALUE_PATTERN = Pattern.compile("(?s).*crc=[0-9a-f]+ (?<success>[A-Z]+).*t=(?<temp>-?[0-9]+)");

        @Override
        public String getId() {
            return id;
        }

        public TmpDS18S20(final File deviceDir) {
            String deviceName;
            try {
                deviceName = new String(Files.readAllBytes(new File(deviceDir, "name").toPath()));
            } catch (IOException e) {
                // FIXME logging
                deviceName = deviceDir.getName();
            }
            if (deviceName.endsWith("\n")) { deviceName = deviceName.substring(0, deviceName.length() - 1); }

            id = deviceName;
            setName(deviceName);
            this.deviceDir = deviceDir;
        }

        @Override
        public double getTemperature() {
            double temperature = lastGoodTemperature;
            try {
                String value = getValue();
                temperature = parseValue(value);
            } catch (Exception e) {
                log.warning("Error reading temperature - returning last known temperature - " + e.toString());
            }

            return temperature;
        }

        private double parseValue(final String value) throws Exception {
            double result = Double.NaN;
        /*
        53 01 4b 46 7f ff 0d 10 e9 : crc=e9 YES
        53 01 4b 46 7f ff 0d 10 e9 t=21187
         */
            final Matcher matcher = VALUE_PATTERN.matcher(value.trim());
            if (matcher.matches()) {
                if (matcher.group("success").equals("YES")) {
                    int tempValue = Integer.valueOf(matcher.group("temp"));

                    // rounding: DS18B20 is +/- 0.5 degree Celsius - no point in being too detailed
                    BigDecimal bd = BigDecimal.valueOf(tempValue);
                    bd = bd.movePointLeft(3);
                    bd = bd.setScale(1, RoundingMode.HALF_UP);
                    result = bd.doubleValue();
                } else {
                    throw new Exception("temperature value is not valid: " + value);
                }
            } else {
                throw new Exception("value regex didn't match");
            }
            return result;
        }

        @Override
        public TemperatureScale getScale() {
            return TemperatureScale.CELSIUS;
        }

        @Override
        public int getFamilyId() {
            return FAMILY_CODE;
        }

        @Override
        public String getValue() throws IOException {
            return new String(Files.readAllBytes(new File(deviceDir, "w1_slave").toPath()));
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof W1Device && id.equals(((W1Device) obj).getId());
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

}
