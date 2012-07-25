/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

import java.io.Serializable;

/**
 *
 * @author serj
 */
public interface ChainStepExecutor extends Serializable {
    
    public static enum Status {
        DONE,
        POSTPONE,
        SKIP;
    }
    
    Status execute(ChainStorage storage);
    
}
