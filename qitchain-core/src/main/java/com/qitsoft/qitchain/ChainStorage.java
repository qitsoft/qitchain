/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author serj
 */
public class ChainStorage implements Serializable {
    
    private final Map<Class, List> mapByClass = new Hashtable<Class, List>();
    private final Map<String, Object> mapByName = new Hashtable<String, Object>();
    
    private ChainWorker chainWorker;
    
    private Object[] parameters;
    
    private Object[] stepParameters;
    
    private Object result;

    public ChainStorage(ChainWorker worker, Object[] parameters) {
        if (worker == null) {
            throw new IllegalArgumentException("The ChainWorker cannot be null");
        }
        
        this.chainWorker = worker;
        this.parameters = parameters;
    }
    
    public <T> T get(Class<T> type) {
        List list = internalList(type);
        if (list == null || list.isEmpty()) {
            return null;
        }
        
        return (T) list.get(0);
    } 
    
    public Object get(String name) {
        if (name == null) {
            return null;
        }
        return mapByName.get(name);
    }
    
    public <T> T get(String name, Class<T> type) {
        T resultItem = (T) get(name);
        if (resultItem == null) {
            return null;
        }
        
        if (type.isAssignableFrom(resultItem.getClass())) {
            return resultItem;
        } else {
            return null;
        }
    }
    
    public <T> List<T> list(Class<T> type) {
        List list = internalList(type);
        if (list.isEmpty()) {
            list = listByDescendants(type);
        }
        return Collections.unmodifiableList(list);
    }
    
    public void put(String name, Object data) {
        if (name == null) {
            put(data);
            return;
        }
        
        Object prevObject;

        if (data == null) {
            prevObject = mapByName.remove(name);
            if (prevObject != null) {
                removeObject(prevObject, prevObject.getClass());
            }
        } else {
            prevObject = mapByName.put(name, data);
            removeObject(prevObject, data.getClass());
            put(data);
        }
    }
    
    public void put(Object data) {
        if (data == null) {
            return;
        }
        
        List list = mapByClass.get(data.getClass());
        if (list == null) {
            list = new ArrayList();
            mapByClass.put(data.getClass(), list);
        }
        list.add(0, data);
    }
    
    public ChainWorker getChainWorker() {
        return chainWorker;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Object getResult() {
        return result;
    }

    public Object[] getStepParameters() {
        return stepParameters;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setStepParameters(Object[] stepParameters) {
        this.stepParameters = stepParameters;
    }
    
    private <T> List<T> internalList(Class<T> type) {
        List list = mapByClass.get(type);
        if (list == null) {
            list = new ArrayList();
        }

        return list;
    }
    
    private void removeObject(Object prev, Class type) {
        List list = internalList(type);
        Iterator i = list.iterator();
        while(i.hasNext()) {
            Object o = i.next();
            if (o == prev) {
                i.remove();
                return;
            }
        }
    }
    
    private List listByDescendants(Class type) {
        List resultItem = new ArrayList();
        
        for(Map.Entry<Class, List> entry : mapByClass.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                resultItem.addAll(entry.getValue());
            }
        }
        
        return resultItem;
    }
    
}
