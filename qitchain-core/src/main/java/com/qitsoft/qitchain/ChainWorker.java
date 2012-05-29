/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

import java.util.List;

/**
 *
 * @author serj
 */
public class ChainWorker {
    
    private List<ChainStepExecutor> steps;
    
    private List<ChainInterceptor> interceptors;

    public ChainWorker(List<ChainStepExecutor> steps, List<ChainInterceptor> interceptors) {
        this.steps = steps;
        this.interceptors = interceptors;
    }
    
    public void execute() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public void execute(ChainStorage storage) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public List<ChainInterceptor> getInterceptors() {
        return interceptors;
    }

    public List<ChainStepExecutor> getSteps() {
        return steps;
    }
    
    
    
}
