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

import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainInfo {
    
    private String name;
    
    private Class<? extends Annotation> annotationType;
    
    private Set<Class> types = new HashSet<Class>();
    
    private SortedSet<ChainStepInfo> steps = new TreeSet<ChainStepInfo>(new ChainStepComparator());

    private Set<ChainListenerInfo> listeners = new HashSet<ChainListenerInfo>();

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addType(Class type) {
        types.add(type);
    }
    
    public Set<Class> getTypes() {
        return types;
    }

    public List<ChainStepInfo> getSteps() {
        return Lists.newArrayList(steps);
    }

    public void addSteps(List<ChainStepInfo> steps) {
        boolean found = false;
        for(ChainStepInfo step : steps) {
            for(ChainStepInfo stepInfo : this.steps) {
                if (step.equals(stepInfo)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.steps.add(step);
            }
        }
    }

    public List<ChainListenerInfo> getListeners() {
        return Lists.newArrayList(listeners);
    }
    
    public void addListeners(List<ChainListenerInfo> listeners) {
        this.listeners.addAll(listeners);
    }

    protected static class ChainStepComparator implements Comparator<ChainStepInfo> {

        public ChainStepComparator() {
        }

        public int compare(ChainStepInfo a, ChainStepInfo b) {
            if (a.equals(b)) {
                return 0;
            }
            
            int result;
            if (b.getOrder() == -1 && a.getOrder() == -1) {
                result = 0;
            } else if (b.getOrder() < 0) {
                result = 0;
            } else if (a.getOrder() < 0) {
                result = 1;
            } else {
                result = a.getOrder() - b.getOrder();
            }
            
            if (result == 0) {
                result = a.getStepClass().getName().compareTo(b.getStepClass().getName());
            }
            if (result == 0) {
                result = a.getMethod().getName().compareTo(b.getMethod().getName());
            }
            
            return result;
        }
    }
}
