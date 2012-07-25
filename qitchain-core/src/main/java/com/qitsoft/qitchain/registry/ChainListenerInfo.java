/*
 * Copyright 2012 QitSoft LLC.
 *
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
 */
package com.qitsoft.qitchain.registry;

import com.qitsoft.qitchain.annotation.ChainListenerType;
import java.lang.reflect.Method;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainListenerInfo {
    
    private final Class listenerClass;
    
    private final Method method;
    
    private final ChainListenerType type;

    public ChainListenerInfo(Class listenerClass, Method method, ChainListenerType type) {
        this.listenerClass = listenerClass;
        this.method = method;
        this.type = type;
    }

    public Class getListenerClass() {
        return listenerClass;
    }

    public Method getMethod() {
        return method;
    }

    public ChainListenerType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChainListenerInfo other = (ChainListenerInfo) obj;
        if (this.listenerClass != other.listenerClass && (this.listenerClass == null || !this.listenerClass.equals(other.listenerClass))) {
            return false;
        }
        if (this.method != other.method && (this.method == null || !this.method.equals(other.method))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.listenerClass != null ? this.listenerClass.hashCode() : 0);
        hash = 11 * hash + (this.method != null ? this.method.hashCode() : 0);
        hash = 11 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
}
