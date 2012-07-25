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

import com.qitsoft.qitchain.ChainStepExecutor;
import com.qitsoft.qitchain.ChainStorage;
import com.qitsoft.qitchain.ChainWorker;
import com.qitsoft.qitchain.annotation.*;
import com.qitsoft.qitchain.listeners.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainsRegistry {
    
    private static final ChainsRegistry INSTANCE = new ChainsRegistry();
            
    private static final Logger logger = LoggerFactory.getLogger(ChainsRegistry.class);
            
    private final Map<String, ChainInfo> chainsByName = new ConcurrentHashMap<String, ChainInfo>();
    
    public static ChainsRegistry getInstance() {
        return INSTANCE;
    }
    
    public synchronized ChainInfo getChainInfo(String name) {
        ChainInfo result = chainsByName.get(name);
        if (result == null) {
            scan();
            result = chainsByName.get(name);
        }
        
        return result;
    }
    
    public synchronized ChainInfo getChainInfo(Class<? extends Annotation> chainAnnotation) {
        return getChainInfo(chainAnnotation.getName());
    }

    protected void scan() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addUrls(ClasspathHelper.forPackage(""));
        
        Reflections reflections = new Reflections(configurationBuilder);
        
        scanChainsByAnnotation(reflections, Chain.class);
    }
    
    private void scanChainsByAnnotation(Reflections reflections, Class<? extends Annotation> annotation) {
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation);
        
        for(Class type : types) {
            if (type.isAnnotation()) {
                scanChainsByAnnotation(reflections, type);
            } else {
                ChainInfo chainInfo = getChainByClass(type, annotation);
                chainsByName.put(chainInfo.getName(), chainInfo);
            }
        }
    }
    
    private void addChainListenersByMethodAnnotation(Class type, List<ChainListenerInfo> result) {
        Set<Method> methods = ReflectionUtils.getAllMethods(type, ReflectionUtils.withAnnotation(ChainListener.class));

        for(Method method : methods) {
            ChainListener annotation = ((ChainListener) method.getAnnotation(ChainListener.class));
            if (annotation.type() != ChainListenerType.UNKNOWN) {
                result.add(new ChainListenerInfo(type, method, annotation.type()));
            }
        }
    }

    private void addChainStepAsWholeObject(List<ChainStepInfo> result, Class type) {
        try {
            result.add(new ChainStepInfo(type, 
                    type.getMethod("execute", ChainStorage.class), 
                    ((ChainStep)type.getAnnotation(ChainStep.class)).order()));
        } catch (NoSuchMethodException ex) {
            logger.warn("Cannot find the [execute] method in %s class.", type.getName(), ex);
        } catch (SecurityException ex) {
            logger.warn("Cannot access the [execute] method in %s class.", type.getName(), ex);
        }
    }

    private void addChainStepsByMethodAnnotation(Class type, List<ChainStepInfo> result) {
        Set<Method> methods = ReflectionUtils.getAllMethods(type, ReflectionUtils.withAnnotation(ChainStep.class));

        for(Method method : methods) {
            result.add(new ChainStepInfo(type, method, 
                    ((ChainStep)method.getAnnotation(ChainStep.class)).order()));
        }
    }
    
    private ChainInfo getChainByClass(Class type, Class<? extends Annotation> annotation) {
        Annotation chainAnnotation = type.getAnnotation(annotation);
        String chainName;
        
        if (Chain.class.isAssignableFrom(annotation)) {
            chainName = ((Chain)chainAnnotation).value();
        } else {
            chainName = annotation.getName();
        }
        
        ChainInfo chainInfo = chainsByName.get(chainName);
        if (chainInfo == null) {
            logger.info("Found chain with name [%s].", chainName);
            chainInfo = new ChainInfo();
        }
        
        chainInfo.setName(chainName);
        chainInfo.addType(type);
        chainInfo.setAnnotationType(annotation);
        chainInfo.addSteps(scanClassSteps(type));
        chainInfo.addListeners(scanClassListeners(type));
        return chainInfo;
    }
    
    private List<ChainStepInfo> scanClassSteps(Class type) {
        List<ChainStepInfo> result = new ArrayList<ChainStepInfo>();

        if (ChainStepExecutor.class.isAssignableFrom(type) 
                && type.isAnnotationPresent(ChainStep.class)) {
            
            addChainStepAsWholeObject(result, type);
        } else {
            addChainStepsByMethodAnnotation(type, result);
        }
        
        if (type.isAnnotationPresent(ChainSteps.class)) {
            addChainStepByTypeAnnotation(type, result);
        }
        
        return result;
    }

    private void addChainStepByTypeAnnotation(Class type, List<ChainStepInfo> result) {
        ChainSteps chainSteps = (ChainSteps) type.getAnnotation(ChainSteps.class);
        
        int index = chainSteps.order();
        for(Class<? extends ChainStepExecutor> step : chainSteps.steps()) {
            try {
                result.add(new ChainStepInfo(step, step.getMethod("execute", ChainStorage.class), index));
            } catch (NoSuchMethodException ex) {
                logger.warn("Cannot find the method [execute] in chain step [%s]", step.getName(), ex);
            } catch (SecurityException ex) {
                logger.warn("Cannot access the method [execute] in chain step [%s]", step.getName(), ex);
            }
            index++;
        }
    }

    private List<ChainListenerInfo> scanClassListeners(Class type) {
        List<ChainListenerInfo> result = new ArrayList<ChainListenerInfo>();

        if (com.qitsoft.qitchain.listeners.BaseChainListener.class.isAssignableFrom(type)) {
            
            addChainListenerAsWholeObject(result, type);
        } else {
            addChainListenersByMethodAnnotation(type, result);
        }
        
        if (type.isAnnotationPresent(ChainListeners.class)) {
            addChainListenersByTypeAnnotation(type, result);
        }
        
        return result;        
    }

    private void addChainListenerAsWholeObject(List<ChainListenerInfo> result, Class type) {
        addChainListeners(type, result);
    }
    
    private void addChainListeners(Class type, List<ChainListenerInfo> result) {
        if (ChainBeforeListener.class.isAssignableFrom(type)) {
            try {
                result.add(new ChainListenerInfo(type, 
                        type.getMethod("onBefore", ChainWorker.class, ChainStorage.class), 
                        ChainListenerType.BEFORE));
                
            } catch (NoSuchMethodException ex) {
                logger.warn("Cannot find the [onBefore(%s, %s)] method in %s class.", new Object[] {type.getName(), ChainWorker.class.getName(), ChainStorage.class.getName(), ex});
            } catch (SecurityException ex) {
                logger.warn("Cannot access the [onBefore(%s, %s)] method in %s class.", new Object[] {type.getName(), ChainWorker.class.getName(), ChainStorage.class.getName(), ex});
            }
        }
        if (ChainAfterListener.class.isAssignableFrom(type)) {
            try {
                result.add(new ChainListenerInfo(type, 
                        type.getMethod("onAfter", ChainWorker.class, ChainStorage.class), 
                        ChainListenerType.AFTER));
                
            } catch (NoSuchMethodException ex) {
                logger.warn("Cannot find the [onAfter(%s, %s)] method in %s class.", new Object[] {type.getName(), ChainWorker.class.getName(), ChainStorage.class.getName(), ex});
            } catch (SecurityException ex) {
                logger.warn("Cannot access the [onAfter(%s, %s)] method in %s class.", new Object[] {type.getName(), ChainWorker.class.getName(), ChainStorage.class.getName(), ex});
            }
        }
        if (ChainBeforeStepListener.class.isAssignableFrom(type)) {
            try {
                result.add(new ChainListenerInfo(type, 
                        type.getMethod("onBeforeStep", ChainWorker.class, ChainStepExecutor.class, ChainStorage.class), 
                        ChainListenerType.BEFORE_STEP));
                
            } catch (NoSuchMethodException ex) {
                logger.warn("Cannot find the [onBeforeStep(%s, %s, %s)] method in %s class.", new Object[] {type.getName(), ChainWorker.class.getName(), ChainStepExecutor.class, ChainStorage.class.getName(), ex});
            } catch (SecurityException ex) {
                logger.warn("Cannot access the [onBeforeStep(%s, %s, %s)] method in %s class.", new Object[] {type.getName(), ChainWorker.class.getName(), ChainStepExecutor.class, ChainStorage.class.getName(), ex});
            }
        }
        if (ChainAfterStepListener.class.isAssignableFrom(type)) {
            try {
                result.add(new ChainListenerInfo(type, 
                        type.getMethod("onAfterStep", ChainWorker.class, ChainStepExecutor.class, ChainStorage.class, ChainStepExecutor.Status.class), 
                        ChainListenerType.AFTER_STEP));
                
            } catch (NoSuchMethodException ex) {
                logger.warn("Cannot find the [onAfterStep(%s, %s, %s, %s)] method in %s class.", 
                        new Object[] {
                            type.getName(), 
                            ChainWorker.class.getName(), 
                            ChainStepExecutor.class, 
                            ChainStorage.class.getName(), 
                            ChainStepExecutor.Status.class.getName(), 
                            ex});
            } catch (SecurityException ex) {
                logger.warn("Cannot access the [onAfterStep(%s, %s, %s, %s)] method in %s class.", 
                        new Object[] {
                            type.getName(), 
                            ChainWorker.class.getName(), 
                            ChainStepExecutor.class, 
                            ChainStorage.class.getName(), 
                            ChainStepExecutor.Status.class.getName(), 
                            ex});
            }
        }        
    }

    private void addChainListenersByTypeAnnotation(Class type, List<ChainListenerInfo> result) {
        ChainListeners chainListeners = (ChainListeners) type.getAnnotation(ChainListeners.class);
        
        for(Class<? extends BaseChainListener> listener : chainListeners.value()) {
            addChainListeners(listener, result);
        }
    }
}
