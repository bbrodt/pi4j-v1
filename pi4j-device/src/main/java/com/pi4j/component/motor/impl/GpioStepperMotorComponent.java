package com.pi4j.component.motor.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  GpioStepperMotorComponent.java
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

import com.pi4j.component.motor.MotorState;
import com.pi4j.component.motor.StepperMotorBase;
import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.component.switches.impl.GpioSwitchComponent;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GpioStepperMotorComponent extends StepperMotorBase {

    // internal class members
    private GpioPin pins[];
    private PinState onState = PinState.HIGH;
    private PinState offState = PinState.LOW;
    private MotorState currentState = MotorState.STOP;
    private GpioStepperMotorControl controlThread = null;
    private int sequenceIndex = 0;
    private float pulleyDiameter = 40f;
    private long currentStepPosition = 0;
    private boolean directionReversed = false;

	/**
     * using this constructor requires that the consumer
     *  define the STEP ON and STEP OFF pin states
     *
     * @param pins GPIO digital output pins for each controller in the stepper motor
     * @param onState pin state to set when MOTOR STEP is ON
     * @param offState pin state to set when MOTOR STEP is OFF
     */
    public GpioStepperMotorComponent(GpioPin pins[], PinState onState, PinState offState) {
        this.pins = pins;
        this.onState = onState;
        this.offState = offState;
    }

    /**
     * default constructor; using this constructor assumes that:
     *  (1) a pin state of HIGH is MOTOR STEP ON
     *  (2) a pin state of LOW  is MOTOR STEP OFF
     *
     * @param pins GPIO digital output pins for each controller in the stepper motor
     */
    public GpioStepperMotorComponent(GpioPin pins[]) {
        this.pins = pins;
    }

    public void setDirectionReversed(boolean reversedDirection) {
    	this.directionReversed = reversedDirection;
    }

    public boolean getDirectionReversed() {
    	return directionReversed;
    }

    /**
     * Add a limit switch that limits the stepper motor timing belt travel. It is presumed that a
     * mechanical stop pushes against the microswitch's lever, closing the contact. The switch
     * must be wired as a "Normally Open" type, connected to electrical ground; the GPIO input pin
     * is pulled up and transitions from HIGH to LOW when the switch is closed.
     *
     * When the state transition occurs, the motor is immediately stopped and its current step
     * position is reset to the given "position" value which is the location of a reference point
     * on the timing belt in millimeters.
     *
     * @param sw - a GPIO input pin configured with a pull-up resistor
     * @param position - position in millimeters used to reset the current step position value
     */
    public void addLimitSwitch(GpioSwitchComponent sw, final float position) {
    	final long stepPosition = distanceToSteps(position);
    	sw.addListener(new SwitchListener() {

			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
//                System.out.println("Switch State Change Event: "+event.getNewState());

                if (event.getNewState() == SwitchState.OFF) {
                	setState(MotorState.STOP);
                	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	currentStepPosition = stepPosition;
                }
			}

    	});
    }

    public void waitForStop() {
		while (getState()!=MotorState.STOP) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
    }

    @Override
    public void step(long steps)
    {
        // validate parameters
        if (steps == 0) {
            setState(MotorState.STOP);
            return;
        }

        // perform step in positive or negative direction from current position
        if (steps > 0){
            setState(MotorState.FORWARD, steps);
        }
        else {
            setState(MotorState.REVERSE, steps);
        }
    }

    /**
     * change the current stepper motor state
     *
     * @param state new motor state to apply
     */
    @Override
    public void setState(MotorState state) {
    	setState(state, 0);
    }

    /*
     * Move the given number of steps in the given direction using the motor control thread.
     */
    private void setState(MotorState state, long steps) {

    	if (currentState==state)
    		return;

        switch(state) {
            case STOP: {
                // set internal tracking state
                currentState = MotorState.STOP;
                if(controlThread==null || !controlThread.isAlive()) {
                    // if control thread is already dead,
	                // turn all GPIO pins to OFF state here
	                for(GpioPin pin : pins)
	                	((GpioPinDigitalOutput)pin).setState(offState);
                }
                break;
            }
            case FORWARD: {
                // set internal tracking state
                currentState = MotorState.FORWARD;
                startControlThread(steps);
                break;
            }
            case REVERSE: {
                // set internal tracking state
                currentState = MotorState.REVERSE;
                startControlThread(steps);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Cannot set motor state: " + state.toString());
            }
        }
    }

    private void startControlThread(long steps) {
        // start control thread if not already running
        if(controlThread!=null && controlThread.isAlive()) {
       		controlThread.interrupt();
       		controlThread = null;
        }
        if (steps!=0)
        	controlThread = new GpioStepperMotorControl(steps);
        else
        	controlThread = new GpioStepperMotorControl();
        controlThread.start();
    }

    /**
     * Return the current motor state
     *
     * @return MotorState
     */
    @Override
    public MotorState getState() {
        return currentState;
    }

	/**
	 * Return the motor's current net step value. The currentStepPosition is updated
	 * for each movement of the stepper motor; FORWARD increments this value and REVERSE
	 * decrements the value.
	 *
	 * @return - motor's current step position value;
	 */
	public long getCurrentStepPosition() {
		return currentStepPosition;
	}

    public float getPulleyDiameter() {
		return pulleyDiameter;
	}

	/**
	 * Sets the pulley diameter (presumed to be a timing pulley) attached to the
	 * stepper motor. This value is used in steps <-> distance calculations because
	 * it is easier to work with linear timing belt travel distances rather than the
	 * stepper motor's angular distance per step.
	 *
	 * The units of this value can be anything (e.g. inches, furlongs, etc.) but
	 * by convention, should be millimeters.
	 *
	 * @param pulleyDiameter
	 */
	public void setPulleyDiameter(float pulleyDiameter) {
		this.pulleyDiameter = pulleyDiameter;
	}

	public long distanceToSteps(float distance) {
		float circumference = (float)Math.PI * pulleyDiameter;
		float revolutions = distance/circumference;
		long steps = (long) (stepsPerRevolution * revolutions);
		return steps;
	}

	/**
	 * Convert a step value to distance in millimeters, using the pulley diameter provided
	 * to setPulleyDiameter()
	 *
	 * @param steps - a step value
	 * @return distance in millimeters that corresponds to the given step value
	 */
	public float stepsToDistance(long steps) {
		float circumference = (float)Math.PI * pulleyDiameter;
		float distance = (float)((double)steps/(double)stepsPerRevolution) * circumference;
		return distance;
	}

	/**
	 * Move the timing belt driven by a pulley attached to the stepper motor by a given distance.
	 * The location should be specified in millimeters.
	 * @param distance
	 */
	public void moveRelative(float distance) {
		long steps = distanceToSteps(distance);

		if (currentStepPosition + steps<0)
			throw new IllegalArgumentException("Can not move to a negative position in moveRelative()");

        if(controlThread.isAlive()) {
        	controlThread.interrupt();
        	controlThread = null;
        }
        controlThread = new GpioStepperMotorControl(steps);
        controlThread.start();
	}

	/**
	 * Move a reference point on a timing belt that is driven by a pulley attached to the stepper
	 * motor (see above) to a specific location. The location should be specified in millimeters.
	 *
	 * @param position
	 */
	public void moveAbsolute(float position) {
		long steps = distanceToSteps(position) - currentStepPosition;

		if (position<0)
			throw new IllegalArgumentException("Can not move to a negative position in moveAbsolute()");

		startControlThread(steps);
	}

	public void resetStepPosition(long position) {
		currentStepPosition = position;
	}

    private class GpioStepperMotorControl extends Thread {
    	// run continuously until client sends us a different motor state
    	private boolean continuous = true;
    	// if not running continuously and client specified a number of steps to move,
    	// this is the number of steps remaining
    	private long stepsRemaining;

    	public GpioStepperMotorControl() {
    		continuous = true;
    		stepsRemaining = 0;
    	}

    	public GpioStepperMotorControl(long steps) {
    		continuous = false;
    		if (steps<0) {
    			currentState = MotorState.REVERSE;
        		stepsRemaining = -steps;
    		}
    		else if (steps>0) {
    			currentState = MotorState.FORWARD;
        		stepsRemaining = steps;
    		}
    		else {
    			currentState = MotorState.STOP;
        		stepsRemaining = 0;
    		}
    	}

        public void run() {
        	sequenceIndex = 0;
            // continuous loop until stopped
            while(currentState != MotorState.STOP) {

            	if (!continuous) {
            		if (stepsRemaining-- <= 0) {
            			currentState = MotorState.STOP;
            			break;
            		}
            	}
                // control direction
                if(currentState == MotorState.FORWARD) {
                    doStep(true);
                }
                else if(currentState == MotorState.REVERSE) {
                    doStep(false);
                }
            }

            // turn all GPIO pins to OFF state
            for(GpioPin pin : pins)
                ((GpioPinDigitalOutput)pin).setState(offState);
        }

        /**
         * this method performs the calculations and work to control the GPIO pins
         * to move the stepper motor forward or reverse
         * @param forward
         */
        private void doStep(boolean forward) {

            // increment or decrement sequence
        	boolean forwardSequence = directionReversed ? !forward : forward;

            if(forwardSequence)
                sequenceIndex++;
            else
                sequenceIndex--;

            // check sequence bounds; rollover if needed
            if(sequenceIndex >= stepSequence.length)
                sequenceIndex = 0;
            else if(sequenceIndex < 0)
                sequenceIndex = (stepSequence.length - 1);

            // start cycling GPIO pins to move the motor forward or reverse
            for(int pinIndex = 0; pinIndex < pins.length; pinIndex++) {
                // apply step sequence
                double nib = Math.pow(2, pinIndex);
                if((stepSequence[sequenceIndex] & (int)nib) > 0)
                    ((GpioPinDigitalOutput)pins[pinIndex]).setState(onState);
                else
                	((GpioPinDigitalOutput)pins[pinIndex]).setState(offState);
            }
            advanceCurrentStepPosition(forward);

            try {
//                Thread.sleep(stepIntervalMilliseconds, stepIntervalNanoseconds);
                Thread.sleep(2, 0);
            }
            catch (InterruptedException e) {}
        }

        private void advanceCurrentStepPosition(boolean forward) {
        	if (forward)
        		++currentStepPosition;
        	else
        		--currentStepPosition;
        }
    }
}
