/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author serj
 */
public class ChainStorage {
    
    private Map<Class, Object> mapByClass = new Hashtable<Class, Object>();
    private Map<String, Object> mapByName = new Hashtable<String, Object>();
    
    public <T> T get(Class<T> type) {
        return (T) mapByClass.get(type);
    } 
    
    public Object get(String name) {
        return mapByName.get(name);
    }
    
    public <T> T get(String name, Class<T> type) {
        return (T) mapByName.get(name);
    }
    
    public <T> List<T> list(Class<T> type) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public List list(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public <T> List<T> list(String name, Class<T> type) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public void put(String name, Object data) {
        mapByName.put(name, data);
        mapByClass.put(data.getClass(), data);
    }
    
    public void put(Object data) {
        mapByClass.put(data.getClass(), data);
    }
    
    public List<ChainStepExecutor> getExecutedSteps() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
