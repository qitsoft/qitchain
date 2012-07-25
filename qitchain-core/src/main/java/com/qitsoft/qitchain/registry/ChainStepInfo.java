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

import java.lang.reflect.Method;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainStepInfo {
    
    private final Class stepClass;
    
    private final Method method;
    
    private final int order;

    public ChainStepInfo(Class stepClass, Method method, int order) {
        this.stepClass = stepClass;
        this.method = method;
        this.order = order;
    }

    public Class getStepClass() {
        return stepClass;
    }

    public Method getMethod() {
        return method;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChainStepInfo other = (ChainStepInfo) obj;
        if (this.stepClass != other.stepClass && (this.stepClass == null || !this.stepClass.equals(other.stepClass))) {
            return false;
        }
        if (this.method != other.method && (this.method == null || !this.method.getName().equals(other.method.getName()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.stepClass != null ? this.stepClass.hashCode() : 0);
        hash = 29 * hash + (this.method != null ? this.method.getName().hashCode() : 0);
        return hash;
    }
    
}
