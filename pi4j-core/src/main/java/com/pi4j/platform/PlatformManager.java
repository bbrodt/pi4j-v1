package com.pi4j.platform;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  PlatformManager.java
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

/**
 * <p>
 * This class provides static methods to configure the Pi4J library's default
 * platform.  Pi4J supports the following platforms:  RaspberryPi
 * </p>
 *
 * @see <a href="https://pi4j.com/">https://pi4j.com/</a>
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 */
public class PlatformManager {

    // selected platform
    private static Platform platform = null;

    /**
     * Get the selected system platform.  If
     * a platform has not explicitly been set, then
     * the default platform: 'RASPBERRY_PI' will be
     * selected and returned.
     *
     * @return selected platform
     */
    public static Platform getPlatform(){
        // if a platform instance has not explicitly been
        // selected, then get the default platform
        if(platform == null){
            try {
                setPlatform(getDefaultPlatform());
            } catch (PlatformAlreadyAssignedException e) {
                // we are eating this exception because it should never be thrown in this case
                e.printStackTrace();
                return null;
            }
        }
        return platform;
    }

    /**
     * Set the runtime platform for Pi4J to use.  This platform selection
     * will be used to determine the default GPIO providers and I2C providers
     * specific to the selected platform.  A platform assignment can only
     * be set once.  If a second attempt to set a platform is attempted,
     * the 'PlatformAlreadyAssignedException' will be thrown.  Please
     * note that platform assignment can be made automatically if you
     * use a provider class prior to manually assigning a platform.
     *
     * @param platform platform to assign
     * @throws PlatformAlreadyAssignedException
     */
    public static void setPlatform(Platform platform) throws PlatformAlreadyAssignedException {
        // prevent changing the platform once it has been initially set
        if(PlatformManager.platform != null){
            throw new PlatformAlreadyAssignedException(PlatformManager.platform);
        }

        // set internal platform instance
        PlatformManager.platform = platform;

        // apply Pi4J platform system property
        System.setProperty("pi4j.platform", platform.id());
    }


    /**
     * Internal method to get the default platform.  It will attempt to first
     * get the platform using the 'PI4J_PLATFORM' environment variable, if the
     * environment variable is not configured, then it will attempt to use the
     * system property "pi4j.platform".  If the system property is not found or
     * the value is not legal, then return the default 'RASPBERRY_PI' platform.
     *
     * @return default platform enumeration
     */
    protected static Platform getDefaultPlatform(){
        // attempt to get assigned platform identifier from the environment variables
        String envPlatformId = System.getenv("PI4J_PLATFORM");

        // attempt to get assigned platform identifier from the system properties
        String platformId = System.getProperty("pi4j.platform", envPlatformId);

        // if a platform id was found in the system properties, then
        // attempt to lookup the platform enumeration using the platform
        // identifier string.  If found then return the platform enum.
        if(platformId != null && !platformId.isEmpty()){
            Platform pltfrm = Platform.fromId(platformId);
            if(pltfrm != null){
                return pltfrm;
            }
        }

        // the RASPBERRY_PI is the default platform;
        // (... in time perhaps some auto-platform detection could be implemented here ...)
        return Platform.RASPBERRYPI;
    }
}
