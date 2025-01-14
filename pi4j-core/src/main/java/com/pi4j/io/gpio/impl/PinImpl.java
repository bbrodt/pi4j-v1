package com.pi4j.io.gpio.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  PinImpl.java
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
import java.util.Objects;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;

public class PinImpl implements Pin {

    private final int address;
    private final String name ;
    private final String provider;
    private final EnumSet<PinPullResistance> supportedPinPullResistance;
    private final EnumSet<PinMode> supportedPinModes;
    private final EnumSet<PinEdge> supportedPinEdges;

    public PinImpl(String provider, int address, String name, EnumSet<PinMode> modes, EnumSet<PinPullResistance> pullResistance, EnumSet<PinEdge> pinEdges) {
        this.provider = provider;
        this.address = address;
        this.name = name;
        this.supportedPinModes = modes;
        this.supportedPinPullResistance = pullResistance;
        this.supportedPinEdges = pinEdges;
    }

    public PinImpl(String provider, int address, String name, EnumSet<PinMode> modes, EnumSet<PinPullResistance> pullResistance) {
        this(provider, address, name, modes, pullResistance, EnumSet.allOf(PinEdge.class));
    }

    public PinImpl(String provider, int address, String name, EnumSet<PinMode> modes) {
        this(provider, address, name, modes, null);
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public EnumSet<PinMode> getSupportedPinModes() {
        if (supportedPinModes == null) {
            return EnumSet.noneOf(PinMode.class);
        }
        return supportedPinModes;
    }

    @Override
    public EnumSet<PinPullResistance> getSupportedPinPullResistance() {
        if (supportedPinPullResistance == null) {
            return EnumSet.noneOf(PinPullResistance.class);
        }
        return supportedPinPullResistance;
    }

    @Override
    public boolean supportsPinPullResistance(){
        return supportedPinPullResistance != null && !supportedPinPullResistance.isEmpty();
    }

    @Override
    public EnumSet<PinEdge> getSupportedPinEdges(){
        if (supportedPinEdges == null) {
            return EnumSet.allOf(PinEdge.class);
        }
        return supportedPinEdges;
    }

    @Override
    public boolean supportsPinEdges(){
        return supportedPinEdges != null && !supportedPinEdges.isEmpty();
    }

    @Override
    public boolean supportsPinEvents(){
        return supportsPinEdges();
    }

    @Override
    public int compareTo(Pin o) {
        if(this.getAddress() < o.getAddress())
            return -1;
        else if(this.getAddress() > o.getAddress())
            return 1;
        else
            return  0;
    }

    @Override
    public boolean equals(Object obj) {
        // matching object instance
        if (obj == this)
            return true;

        // reject is not a pin instance
        if (!(obj instanceof Pin))
            return false;

        // match on pin provider, name and address
        Pin pin = (Pin) obj;
        return (pin.getProvider().equals(getProvider()) &&
                Objects.equals(pin.getName(), getName()) &&
                pin.getAddress() == getAddress());
    }
}
