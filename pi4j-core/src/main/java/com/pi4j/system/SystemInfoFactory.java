package com.pi4j.system;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  SystemInfoFactory.java
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


import com.pi4j.concurrent.DefaultExecutorServiceFactory;
import com.pi4j.concurrent.ExecutorServiceFactory;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.impl.GpioControllerImpl;
import com.pi4j.platform.PlatformManager;

/**
 * <p>This factory class provides a static method to create new 'GpioController' instances. </p>
 *
 * <p>
 * Before using the Pi4J library, you need to ensure that the Java VM in configured with access to
 * the following system libraries:
 * <ul>
 * <li>pi4j</li>
 * <li>wiringPi</li>
 * </ul>
 *
 * <blockquote> This library depends on the wiringPi native system library. (developed by
 * Gordon Henderson @ <a href="http://wiringpi.com/">http://wiringpi.com/</a>)
 * </blockquote>
 * </p>
 *
 * @see GpioController
 * @see GpioProvider
 *
 * @see <a href="https://pi4j.com/">https://pi4j.com/</a>
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 */
@SuppressWarnings("unused")
public class SystemInfoFactory {

    // we only allow a single default provider to exists
    private static SystemInfoProvider provider = null;

    // private constructor
    private SystemInfoFactory() {
        // forbid object construction
    }

    /**
     * <p>Return default instance of {@link SystemInfoProvider}.</p>
     *
     * @return Return a new SystemInfoProvider impl instance.
     */
    public static SystemInfoProvider getProvider() {
        // if a provider has not been created, then create a new instance
        if (provider == null) {
            // create the provider based on the PlatformManager's selected platform
            provider = PlatformManager.getPlatform().getSystemInfoProvider();
        }

        // return the provider instance
        return provider;
    }

    /**
     * Sets default {@link SystemInfoProvider}.
     *
     * @param provider default system info provider
     */
    public static void setProvider(SystemInfoProvider provider) {
        // set the default provider instance
        SystemInfoFactory.provider = provider;
    }
}
