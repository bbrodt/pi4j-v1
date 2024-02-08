package com.pi4j.gpio.extension.mcp;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: GPIO Extension
 * FILENAME      :  MCP3008GpioProvider.java
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

import com.pi4j.gpio.extension.base.AdcGpioProvider;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiMode;

/**
 *
 * <p>
 * This GPIO provider implements the MCP3008 SPI GPIO expansion board as native Pi4J GPIO pins. It is a 10-bit ADC
 * providing 8 input channels. More information about the board can be found here: -
 * http://ww1.microchip.com/downloads/en/DeviceDoc/21295d.pdf
 * </p>
 *
 * <p>
 * The MCP3008 is connected via SPI connection to the Raspberry Pi and provides 8 GPIO pins that can be used for analog
 * input pins. The values returned are in the range 0-1023 (10 bit value).
 *
 * Note: This implementation currently only supports single-ended inputs.
 * </p>
 *
 * @author pojd, Hendrik Motza
 */
public class MCP3008GpioProvider extends MCP3x0xGpioProvider implements AdcGpioProvider {

	public static final String NAME = "com.pi4j.gpio.extension.mcp.MCP3008GpioProvider";
	public static final String DESCRIPTION = "MCP3008 GPIO Provider";
	public static final int INPUT_COUNT = 8;
	public static final int RESOLUTION = 10;

	/**
	 * Create new instance of this MCP3008 provider with background monitoring and pin notification events enabled.
	 *
	 * @param channel
	 *            spi channel the MCP3008 is connected to
	 * @throws IOException
	 *             if an error occurs during initialization of the SpiDevice
	 */
	public MCP3008GpioProvider(final SpiChannel channel) throws IOException {
		this(channel, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE, true);
	}

	/**
	 * Create new instance of this MCP3008 provider with background monitoring and pin notification events enabled.
	 *
	 * @param channel
	 *            spi channel the MCP3008 is connected to
	 * @param speed
	 *            spi speed to communicate with MCP3008
	 * @throws IOException
	 *             if an error occurs during initialization of the SpiDevice
	 */
	public MCP3008GpioProvider(final SpiChannel channel, final int speed) throws IOException {
		this(channel, speed, SpiDevice.DEFAULT_SPI_MODE, true);
	}

	/**
	 * Create new instance of this MCP3008 provider with background monitoring and pin notification events enabled.
	 *
	 * @param channel
	 *            spi channel the MCP3008 is connected to
	 * @param mode
	 *            spi mode to communicate with MCP3008
	 * @throws IOException
	 *             if an error occurs during initialization of the SpiDevice
	 */
	public MCP3008GpioProvider(final SpiChannel channel, final SpiMode mode) throws IOException {
		this(channel, SpiDevice.DEFAULT_SPI_SPEED, mode, true);
	}

	/**
	 * Create new instance of this MCP3008 provider with background monitoring and pin notification events enabled.
	 *
	 * @param channel
	 *            spi channel the MCP3008 is connected to
	 * @param speed
	 *            spi speed to communicate with MCP3008
	 * @param mode
	 *            spi mode to communicate with MCP3008
	 * @throws IOException
	 *             if an error occurs during initialization of the SpiDevice
	 */
	public MCP3008GpioProvider(final SpiChannel channel, final int speed, final SpiMode mode) throws IOException {
		this(channel, speed, mode, true);
	}

	/**
	 * Create new instance of this MCP3008 provider. Optionally enable or disable background monitoring and pin
	 * notification events.
	 *
	 * @param channel
	 *            spi channel the MCP3008 is connected to
	 * @param speed
	 *            spi speed to communicate with MCP3008
	 * @param mode
	 *            spi mode to communicate with MCP3008
	 * @param enableBackgroundMonitoring
	 *            if enabled, then a background thread will be created to constantly acquire the ADC input values and
	 *            publish pin change listeners if the value change is beyond the configured threshold.
	 * @throws IOException
	 *             if an error occurs during initialization of the SpiDevice
	 */
	public MCP3008GpioProvider(final SpiChannel channel, final int speed, final SpiMode mode,
			final boolean enableBackgroundMonitoring) throws IOException {
		super(MCP3008Pin.ALL, channel, speed, RESOLUTION, mode);

		// default background monitoring interval
		setMonitorInterval(DEFAULT_MONITOR_INTERVAL);

		// enable|disable background monitoring
		if (enableBackgroundMonitoring) {
			setMonitorEnabled(enableBackgroundMonitoring);
		}
	}

	// ------------------------------------------------------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------------------------------------------------------
	@Override
	public String getName() {
		return NAME;
	}
}
