package com.pi4j.wiringpi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  Shift.java
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


import com.pi4j.util.NativeLibraryLoader;

/**
 * <h1>WiringPi Shift Library</h1>
 *
 * <p>
 * WiringPi includes a shift library which more or less mimics the one in the Arduino system. This
 * allows you to shift 8-bit data values out of the Pi, or into the Pi from devices such as
 * shift-registers (e.g. 74,595) and so-on, although it can also be used in some bit-banging
 * scenarios.
 * </p>
 *
 * <p>
 * Before using the Pi4J library, you need to ensure that the Java VM in configured with access to
 * the following system libraries:
 * <ul>
 * <li>pi4j</li>
 * <li>wiringPi</li>
 * </ul>
 * <blockquote> This library depends on the wiringPi native system library. (developed by
 * Gordon Henderson @ <a href="http://wiringpi.com">http://wiringpi.com</a>)
 * </blockquote>
 * </p>
 *
 * @see <a href="https://pi4j.com/">https://pi4j.com/</a>
 * @see <a
 *      href="http://wiringpi.com/reference/shift-library/">http://wiringpi.com/reference/shift-library/</a>
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 */
public class Shift {

    public static final int LSBFIRST = 0;
    public static final int MSBFIRST = 1;

    // private constructor
    private Shift() {
        // forbid object construction
    }

    static {
        // Load the platform library
        NativeLibraryLoader.load("libpi4j.so", "pi4j");
    }

    /**
     * <p>uint8_t shiftIn (uint8_t dPin, uint8_t cPin, uint8_t order);</p>
     *
     * <p>
     * This shifts an 8-bit data value in with the data appearing on the dPin and the clock being
     * sent out on the cPin. Order is either LSBFIRST or MSBFIRST. The data is sampled after the
     * cPin goes high. (So cPin high, sample data, cPin low, repeat for 8 bits) The 8-bit value is
     * returned by the function.
     * </p>
     *
     * @see <a
     *      href="http://wiringpi.com/reference/shift-library/">http://wiringpi.com/reference/shift-library/</a>
     * @param dPin data pin
     * @param cPin clock pin
     * @param order order is either LSBFIRST or MSBFIRST
     * @return <p>
     *         The 8-bit shifted value is returned by the function.
     *         </p>
     */
    public static native byte shiftIn(byte dPin, byte cPin, byte order);

    /**
     * <p>void shiftOut (uint8_t dPin, uint8_t cPin, uint8_t order, uint8_t val);</p>
     *
     * <p>
     * The shifts an 8-bit data value val out with the data being sent out on dPin and the clock
     * being sent out on the cPin. order is as above. Data is clocked out on the rising or falling
     * edge; ie. dPin is set, then cPin is taken high then low and repeated for the 8 bits.
     * </p>
     *
     * @see <a
     *      href="http://wiringpi.com/reference/shift-library/">http://wiringpi.com/reference/shift-library/</a>
     * @param dPin data pin
     * @param cPin clock pin
     * @param order order is either LSBFIRST or MSBFIRST
     * @param val 8-bit data value
     */
    public static native void shiftOut(byte dPin, byte cPin, byte order, byte val);
}
