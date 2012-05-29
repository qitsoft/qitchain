/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

/**
 *
 * @author serj
 */
public interface ChainStepExecutor {
    
    public static enum Status {
        EXECUTE,
        POSTPONE,
        CANCEL;
    }
    
    Status execute(ChainStorage storage);
    
}
