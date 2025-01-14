package com.pi4j.component;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  ObserveableComponentBase.java
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObserveableComponentBase extends ComponentBase implements ObserveableComponent {

    protected final List<ComponentListener> listeners = new ArrayList<>();;

    protected synchronized void addListener(ComponentListener... listener){
        if (listener == null || listener.length == 0) {
            throw new IllegalArgumentException("Missing listener argument.");
        }

        // add new listeners
        Collections.addAll(listeners, listener);
    }

    protected synchronized void removeListener(ComponentListener... listener) {
        if (listener == null || listener.length == 0) {
            throw new IllegalArgumentException("Missing listener argument.");
        }
        for (ComponentListener lsnr : listener) {
            listeners.remove(lsnr);
        }
    }

    @Override
    public synchronized void removeAllListeners() {
        List<ComponentListener> listeners_copy = new ArrayList<>(listeners);
        for (int index = (listeners_copy.size()-1); index >= 0; index --) {
            ComponentListener listener = listeners_copy.get(index);
            removeListener(listener);
        }
    }
}
