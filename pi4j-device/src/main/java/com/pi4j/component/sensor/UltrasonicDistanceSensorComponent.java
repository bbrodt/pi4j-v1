package com.pi4j.component.sensor;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  UltrasonicDistanceSensorComponent.java
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

import com.pi4j.component.ComponentBase;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of the CrowPi ultrasonic distance sensor (HC-SR04) using GPIO with Pi4J
 */
public class UltrasonicDistanceSensorComponent extends ComponentBase {
    /**
     * Scheduler instance for running the poller thread.
     */
    private final ScheduledExecutorService scheduler;
    /**
     * Active poller thread or null if currently not running.
     */
    private ScheduledFuture<?> poller;

    /**
     * Event handlers for "objectFound" and "objectDisappeared" event.
     */
    private Callable objectFoundHandler;
    private Callable objectDisappearedHandler;
    /**
     * Variables used to generate SimpleEvents to provide object found and disappeared
     */
    private final AtomicBoolean state;
    private volatile double minRange = 2;
    private volatile double maxRange = 300;
    private volatile double temperature;

    /**
     * Pi4J digital output instance used by this component
     */
    private final GpioPinDigitalOutput digitalOutputTrigger;

    /**
     * Pi4J digital input instance used by this component
     */
    private final GpioPinDigitalInput digitalInputEcho;

    /**
     * Default BCM pins of ultrasonic distance sensor for CrowPi
     */
    protected static final int DEFAULT_PIN_TRIGGER = 2;
    protected static final int DEFAULT_PIN_ECHO = 3;
    /**
     * Default temperature setting to calculate distances
     */
    protected static final double DEFAULT_TEMPERATURE = 20.0;

    /**
     * Default period in milliseconds of button state poller.
     * The poller will be run in a separate thread and executed every X milliseconds.
     */
    protected static final long DEFAULT_POLLER_PERIOD_MS = 100;

    /**
     * Pulse length measured
     */
    private volatile double pulseLength;


    /**
     * Creates a new ultrasonic distance sensor component with custom GPIO addresses
     *
     * @param triggerAddress GPIO pin of trigger output
     * @param echoAddress    GPIO pin of echo input
     */
    public UltrasonicDistanceSensorComponent(GpioPin trigger, GpioPin echo) {
        digitalInputEcho = (GpioPinDigitalInput)echo;
        digitalOutputTrigger = (GpioPinDigitalOutput)trigger;

        state = new AtomicBoolean(false);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        temperature = DEFAULT_TEMPERATURE;
    }

    /**
     * With this Method the range where a object should be recognized is defined.
     *
     * @param minRange Minimal distance to the object in cm
     * @param maxRange Maximal distance to the object in cm
     */
    public void setDetectionRange(double minRange, double maxRange) {
        if (minRange >= maxRange) {
            throw new IllegalArgumentException("minRange has to be smaller than maxRange");
        }

        if (minRange < 2 || minRange > 300) {
            throw new IllegalArgumentException("minRange out of allowed range. Allowed 2 - 300cm. Actual value was: " + minRange);
        }

        if (maxRange < 2 || maxRange > 300) {
            throw new IllegalArgumentException("maxRange out of allowed range. Allowed 2 - 300cm. Actual value was: " + maxRange);
        }

        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    /**
     * Sets the currently used measurement temperature by this ultrasonic sensor.
     *
     * @param temperature Temperature the sensor is operating at in [°C].
     */
    public void setMeasurementTemperature(double temperature) {
        if (temperature > 40 || temperature < -20) {
            throw new IllegalArgumentException("Temperature out of range. Allowed only -20°C to 40°C");
        }

        this.temperature = temperature;
    }

    /**
     * (Re-)starts the poller with the desired time period in milliseconds.
     * If the poller is already running, it will be cancelled and rescheduled with the given time.
     * The first poll happens immediately in a separate thread and does not get delayed.
     *
     * @param pollerPeriodMs Polling period in milliseconds
     */
    protected synchronized void startPoller(long pollerPeriodMs) {
    	if (true)return;
        if (poller != null) {
            poller.cancel(true);
        }
        poller = scheduler.scheduleAtFixedRate(new Poller(), 0, pollerPeriodMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the poller immediately, therefore causing the button states to be no longer refreshed.
     * If the poller is already stopped, this method will silently return and do nothing.
     */
    protected synchronized void stopPoller() {
        if (poller != null && objectDisappearedHandler == null && objectFoundHandler == null) {
            poller.cancel(true);
            poller = null;
        }
    }

    /**
     * Returns the internal scheduled future for the poller thread or null if currently stopped.
     *
     * @return Active poller instance or null
     */
    protected ScheduledFuture<?> getPoller() {
        return poller;
    }

    /**
     * Start a measurement with default temperature setting
     *
     * @return Measured distance [cm]
     */
    public double measure() throws TimeoutException {
        return measure(temperature);
    }

    /**
     * Start a measurement with custom temperature setting. Use this to have a temperature compensation.
     *
     * @param temperature Current environment temperature the ultra sonic sensor is working in. Range -20°C to 40°C
     * @return Measured distance [cm]
     */
    public double measure(double temperature) throws TimeoutException {
        double pulseLength = measurePulse();
        return calculateDistance(pulseLength, temperature);
    }

    /**
     * Sets or disables the handler for the object found recognition.
     * This event gets triggered whenever a object comes into the specified range
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void onObjectFound(Callable handler) {
        objectFoundHandler = handler;

        if (handler != null) {
            startPoller(DEFAULT_POLLER_PERIOD_MS);
        } else {
            stopPoller();
        }
    }

    /**
     * Sets or disables the handler for the object disappear recognition.
     * This event gets triggered whenever a object leaves the specified range
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void onObjectLost(Callable handler) {

        objectDisappearedHandler = handler;

        if (handler != null) {
            startPoller(DEFAULT_POLLER_PERIOD_MS);
        } else {
            stopPoller();
        }
    }

    /**
     * Triggers the ultrasonic sensor to start a measurement. Measures the time until the ECHO is recognized.
     *
     * @return Time which the ultrasonic signal needs to travel to the next object and return to the sensor
     */
    protected synchronized double measurePulse() throws TimeoutException {
        // Threading is used to compensate Java delays. The sensor is just a little to fast.
        var measurementTask = new Thread( () -> {
            Thread triggerTask = new Thread( () -> {
            	digitalOutputTrigger.pulse(10, TimeUnit.MILLISECONDS);
            });

            long startTime = 0;
            long endTime = 0;

            triggerTask.start();
            while (digitalInputEcho.isLow() && !Thread.currentThread().isInterrupted()) {
                startTime = System.nanoTime();
            }

            while (digitalInputEcho.isHigh() && !Thread.currentThread().isInterrupted()) {
                endTime = System.nanoTime();
            }

            pulseLength = (double) (endTime - startTime) / 1_000_000;
        });

        measurementTask.start();
        try {
            // Sometimes a measurement can fail. Timeout and return invalid measurement value.
            measurementTask.join(500);
        } catch (InterruptedException timeout) {
            measurementTask.interrupt();
//            throw new TimeoutException("Timed out while retrieving measurement");
        }

        return pulseLength;
    }

    /**
     * Calculates measured distance from pulse length with temperature compensation.
     *
     * @param pulseLength pulse duration in milliseconds
     * @param temperature temperature during the measurement. Range -20°C to 40°C
     * @return distance in centimeters
     */
    protected double calculateDistance(double pulseLength, double temperature) throws TimeoutException {
        if (temperature > 40 || temperature < -20) {
            throw new IllegalArgumentException("Temperature out of range. Allowed only -20°C to 40°C");
        }

        if (pulseLength >= 25 || pulseLength < 0.1) {
//            throw new TimeoutException("Invalid pulse length " + pulseLength + " found, expected value in range 0" + ".1 - 25");
        }

        double sonicSpeed = (331.5 + (0.6 * temperature)) / 10; // [cm/ms]
        double distance = pulseLength * sonicSpeed / 2;

        return (double) Math.round(distance * 100) / 100;
    }

    /**
     * Get the instance of the echo input
     *
     * @return A digital input configuration
     */
    public GpioPin getDigitalInputEcho() {
        return digitalInputEcho;
    }

    /**
     * Get the instance of the trigger output
     *
     * @return A digital output configuration
     */
    public GpioPin getDigitalOutputTrigger() {
        return digitalOutputTrigger;
    }

    /**
     * Poller class which implements {@link Runnable} to be used with {@link ScheduledExecutorService} for repeated execution.
     * This poller consecutively starts a measurement and checks if it's in range of object
     * Additionally, simple event handlers will be triggered during state transitions.
     */
    private final class Poller implements Runnable {
        @Override
        public void run() {
            double result;

            try {
                // Start a measurement
                result = measure(temperature);
            } catch (TimeoutException e) {
                return;
            }

            // Evaluate there is an object in the range by previous measured value
            final var newState = result <= maxRange && result >= minRange;
            final var oldState = state.getAndSet(newState);

            // Everything done if the state didn't change
            if (oldState == newState) {
                return;
            }

            // Fire events if the state changed
			try {
				if (newState) {
					objectFoundHandler.call();
				} else {
					objectDisappearedHandler.call();
				}
			} catch (Exception e) {
			}
        }
    }
}
