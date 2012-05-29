/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

/**
 *
 * @author serj
 */
public abstract class ChainPool {
    
    private ChainFactory factory;

    public ChainPool(ChainFactory factory) {
        this.factory = factory;
    }
    
    public abstract ChainWorker get();
    
    protected ChainFactory getFactory() {
        return factory;
    }
    
}
