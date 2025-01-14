package com.pi4j.temperature;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  TemperatureScale.java
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

@SuppressWarnings("unused")
public enum TemperatureScale {

    CELSIUS("Celsius","°C",TemperatureConversion.ABSOLUTE_ZERO_CELSIUS),
    FARENHEIT("Farenheit","°F", TemperatureConversion.ABSOLUTE_ZERO_FARENHEIT),
    KELVIN("Kelvin","K", TemperatureConversion.ABSOLUTE_ZERO_KELVIN),
    RANKINE("Rankine","°R", TemperatureConversion.ABSOLUTE_ZERO_RANKINE);

    private String name;
    private String units;
    private double absoluteZero = 0;

    TemperatureScale(String name, String units, double absoluteZero){
        this.name= name;
        this.units = units;
        this.absoluteZero = absoluteZero;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public String getValueString(double temperature) {
        return temperature + " " + units;
    }

    public double getAbsoluteZero() {
        return absoluteZero;
    }

    @Override
    public String toString() {
        return name;
    }

}
