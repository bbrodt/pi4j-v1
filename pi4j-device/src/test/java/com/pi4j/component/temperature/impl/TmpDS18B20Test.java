package com.pi4j.component.temperature.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  TmpDS18B20Test.java
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


import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Peter Schuebl
 */
public class TmpDS18B20Test {
    private W1Master master;

    @Before
    public void setupMaster() throws URISyntaxException {
        master = new W1Master(new File(TmpDS18B20Test.class.getResource("/w1/sys/bus/w1/devices").toURI()).toString());
    }

    @Test
    public void testDevices() {
        //System.out.println(master.toString());
        final List<W1Device> devices = master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
        assertEquals(2, devices.size());
        for (W1Device device : devices) {
            //System.out.println(((TemperatureSensor) device).getTemperature());
            assertTrue((((TemperatureSensor) device).getTemperature()) > 20.0);
        }
    }

    public void testName() throws URISyntaxException {
        final String id = "28-00000698ebb1";
        TmpDS18B20DeviceType.TmpDS18B20 device = createDevice(id);
        device.setName("My Sensor");

        assertEquals("My Sensor", device.getName());
        assertEquals(id, device.getId());

    }

    private TmpDS18B20DeviceType.TmpDS18B20 createDevice(String id) throws URISyntaxException {
        final TmpDS18B20DeviceType deviceType = new TmpDS18B20DeviceType();
        final URI uri = TmpDS18B20Test.class.getResource("/w1/sys/bus/w1/devices/" + id).toURI();
        final File deviceDir = new File(uri);
        return deviceType.create(deviceDir);
    }

    @SuppressWarnings("unlikely-arg-type")
	@Test
    public void testEquals() throws Exception {
        final W1Device w1Devicea1 = createDevice("28-00000698ebb1");
        final W1Device w1Devicea2 = createDevice("28-00000698ebb1");

        assertTrue(w1Devicea1.equals(w1Devicea2));

        final W1Device w1Deviceb = createDevice("28-00000698ebb2");
        assertFalse(w1Devicea1.equals(w1Deviceb));
        assertFalse(w1Devicea1.equals(null));

        assertFalse(w1Devicea1.equals("123"));
    }

    @Test
    public void testHashCode() throws Exception {
        final W1Device w1Devicea1 = createDevice("28-00000698ebb1");
        final W1Device w1Devicea2 = createDevice("28-00000698ebb1");
        assertEquals(w1Devicea1.hashCode(), w1Devicea2.hashCode());

        final W1Device w1Deviceb = createDevice("28-00000698ebb2");
        assertNotEquals(w1Devicea1.hashCode(), w1Deviceb.hashCode());
    }
}
