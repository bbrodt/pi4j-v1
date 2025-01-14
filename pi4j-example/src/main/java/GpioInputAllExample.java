/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  GpioInputAllExample.java
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
import com.pi4j.io.gpio.*;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.SystemInfo;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import com.pi4j.util.ConsoleColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This example code demonstrates how to perform simple GPIO
 * pin state reading on the RaspberryPi platform fro all pins.
 *
 * @author Robert Savage
 */
public class GpioInputAllExample {

    /**
     * [ARGUMENT/OPTION "--pull (up|down|off)" | "-l (up|down|off)" | "--up" | "--down" ]
     * This example program accepts an optional argument for specifying pin pull resistance.
     * Supported values: "up|down" (or simply "1|0").   If no value is specified in the command
     * argument, then the pin pull resistance will be set to PULL_UP by default.
     * -- EXAMPLES: "--pull up", "-pull down", "--pull off", "--up", "--down", "-pull 0", "--pull 1", "-l up", "-l down".
     *
     * @param args
     * @throws InterruptedException
     * @throws PlatformAlreadyAssignedException
     */
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException, IOException {

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "GPIO Input (ALL PINS) Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // by default we will use gpio pin PULL-UP; however, if an argument
        // has been provided, then use the specified pull resistance
        PinPullResistance pull = CommandArgumentParser.getPinPullResistance(
                PinPullResistance.PULL_UP,  // default pin pull resistance if no pull argument found
                args);                      // argument array to search in

        List<GpioPinDigitalInput> provisionedPins = new ArrayList<>();
        Pin[] pins;

        // get a collection of raw pins based on the board type (model)
        SystemInfo.BoardType board = SystemInfo.getBoardType();
        if(board == SystemInfo.BoardType.RaspberryPi_ComputeModule ||
                board == SystemInfo.BoardType.RaspberryPi_ComputeModule3 ||
                board == SystemInfo.BoardType.RaspberryPi_ComputeModule3_Plus) {
            // get all pins for compute module 1 & 3 (CM4 uses 'RaspiPin' pin provider)
            pins = RCMPin.allPins();
        }
        else {
            // get exclusive set of pins based on RaspberryPi model (board type)
            pins = RaspiPin.allPins(board);
        }

        // provision GPIO input pins for the target platform and SoC board/system
        for (Pin pin : pins) {
            try {
                GpioPinDigitalInput provisionedPin = gpio.provisionDigitalInputPin(pin, pull);
                provisionedPin.setShutdownOptions(true); // unexport pin on program shutdown
                provisionedPins.add(provisionedPin);     // add provisioned pin to collection
            }
            catch (Exception ex){
                System.err.println(ex.getMessage());
            }
        }

        // prompt user that we are ready
        console.println(" ... Successfully provisioned all GPIO input pins");
        console.emptyLine();
        console.box("The GPIO input pins states will be displayed below.");
        console.emptyLine();

        // display pin states for all pins
        for(GpioPinDigitalInput input : provisionedPins) {
            console.println(" [" + input.toString() + "] digital state is: " + ConsoleColor.conditional(
                    input.getState().isHigh(), // conditional expression
                    ConsoleColor.GREEN,        // positive conditional color
                    ConsoleColor.RED,          // negative conditional color
                    input.getState()));
        }

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
    }
}
