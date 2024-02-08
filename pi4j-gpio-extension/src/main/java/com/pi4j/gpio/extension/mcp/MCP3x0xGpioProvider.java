package com.pi4j.gpio.extension.mcp;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: GPIO Extension
 * FILENAME      :  MCP3x0xGpioProvider.java
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

import java.io.IOException;

import com.pi4j.gpio.extension.base.AdcGpioProviderBase;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

/**
 *
 * <p>
 * This GPIO provider implements the base functionality for the MCP3004 & MCP3008 & MCP3204 & MCP3208 SPI ADC as native
 * Pi4J GPIO pins. It is a 10-bit ADC providing between 4 and 8 input channels.
 *
 * More information about the ADC chip can be found here: http://ww1.microchip.com/downloads/en/DeviceDoc/21295d.pdf
 * </p>
 *
 * <p>
 * The MCP3x0x family is connected via SPI connection to the Raspberry Pi and provides 4/8 GPIO pins that can be used
 * for analog input pins. The values returned are in the range from 0 to the highest number (e.g. 1023 or 4095)
 * depending on the ADC resolution.
 *
 * Note: This implementation currently only supports single-ended inputs.
 * </p>
 *
 * @author Robert Savage, pojd, Hendrik Motza
 */
public abstract class MCP3x0xGpioProvider extends AdcGpioProviderBase implements GpioProvider {

	public final int MAX_VALUE; // 10-bit ADC can produce values from 0 to 1023
	public final int MIN_VALUE = 0; // 10-bit ADC can produce values from 0 to 1023
	public final int RESOLUTION; // 10-bit ADC can produce values from 0 to 1023

	private final SpiDevice device;

	/**
	 * Create new instance of this MCP3x0x provider. Optionally enable or disable background monitoring and pin
	 * notification events.
	 *
	 * @param pins
	 *            the collection of all GPIO pins used with this ADC provider implementation
	 * @param channel
	 *            spi channel the MCP3x0x is connected to
	 * @param speed
	 *            spi speed to communicate with MCP3x0x
	 * @param mode
	 *            spi mode to communicate with MCP3x0x
	 * @throws IOException
	 *             if an error occurs during initialization of the SpiDevice
	 */
	public MCP3x0xGpioProvider(final Pin[] pins, final SpiChannel channel, final int speed, final int resolution,
			final SpiMode mode) throws IOException {
		super(pins);
		this.device = SpiFactory.getInstance(channel, speed, mode);
		RESOLUTION = resolution;
		MAX_VALUE = (1 << resolution) - 1;
	}

	// ------------------------------------------------------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------------------------------------------------------

	/**
	 * This method will perform an immediate data acquisition directly to the ADC chip to get the requested pin's input
	 * conversion value.
	 *
	 * @param pin requested input pin to acquire conversion value
	 *
	 * @return conversion value for requested analog input pin
	 * @throws IOException
	 */
	@Override
	public double getImmediateValue(final Pin pin) throws IOException {
		if (isInitiated()) {
			// read in value from ADC chip
			final double value = readAnalog(toCommand((short) pin.getAddress()));
			// validate value withing acceptable range
			if (value >= MIN_VALUE && value <= MAX_VALUE) {
				getPinCache(pin).setAnalogValue(value);
				return value;
			}
		}
		return INVALID_VALUE;
	}

	/**
	 * Get the minimum supported analog value for the ADC implementation.
	 *
	 * @return Returns the minimum supported analog value.
	 */
	@Override
	public double getMinSupportedValue() {
		return MIN_VALUE;
	}

	/**
	 * Get the maximum supported analog value for the ADC implementation.
	 *
	 * (For example, a 10 bit ADC's maximum value is 1023 and a 12-bit ADC's maximum value is 4095.
	 *
	 * @return Returns the maximum supported analog value.
	 */
	@Override
	public double getMaxSupportedValue() {
		return MAX_VALUE;
	}

	// ------------------------------------------------------------------------------------------
	// base implementation methods for MCP3x0x ADC chips
	// ------------------------------------------------------------------------------------------

	private short toCommand(final short channel) {
		final short command = (short) ((channel + 8) << 4);
		return command;
	}

	private boolean isInitiated() {
		return device != null;
	}

	private synchronized int readAnalog(final short channelCommand) {
		// send 3 bytes command - "1", channel command and some extra byte 0
		// http://hertaville.com/2013/07/24/interfacing-an-spi-adc-mcp3008-chip-to-the-raspberry-pi-using-c
		final short[] data;
		data = RESOLUTION > 10 ? new short[] { 1, channelCommand, 0, 0 } : new short[] { 1, channelCommand, 0 };
		short[] result;
		try {
			result = device.write(data);
		} catch (final IOException e) {
			return INVALID_VALUE;
		}

		// now take 8 and 9 bit from second byte (& with 0b11 and shift) and the last bytes to form the value
		int analogValue = ((result[1] & 3) << (RESOLUTION - 2)) + (result[2] << (RESOLUTION - 10));
		// 12 bit adc has a further byte
		if (RESOLUTION > 10) {
			analogValue += result[3];
		}
		return analogValue;
	}
}
