package com.pi4j.component.button;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  ButtonStateChangeEvent.java
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


public class ButtonStateChangeEvent extends ButtonEvent {

    private static final long serialVersionUID = 7611552098849283947L;
	protected final ButtonState oldState;
    protected final ButtonState newState;

    public ButtonStateChangeEvent(Button buttonComponent, ButtonState oldState, ButtonState newState) {
        super(buttonComponent);
        this.oldState = oldState;
        this.newState = newState;
    }

    public ButtonState getOldState() {
        return oldState;
    }

    public ButtonState getNewState() {
        return newState;
    }

    @Override
    public boolean isPressed(){
        return newState == ButtonState.PRESSED;
    }

    @Override
    public boolean isReleased(){
        return newState == ButtonState.RELEASED;
    }

    @Override
    public boolean isState(ButtonState state){ return getNewState() == state; }
}
