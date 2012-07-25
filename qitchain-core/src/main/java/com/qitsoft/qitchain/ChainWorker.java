/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qitsoft.qitchain;

import com.qitsoft.qitchain.listeners.ChainAfterListener;
import com.qitsoft.qitchain.listeners.ChainAfterStepListener;
import com.qitsoft.qitchain.listeners.ChainBeforeListener;
import com.qitsoft.qitchain.listeners.ChainBeforeStepListener;
import com.qitsoft.qitchain.listeners.BaseChainListener;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author serj
 */
public class ChainWorker implements Serializable {
    
    private final List<ChainStepExecutor> steps;
    
    private final String name;
    
    private String id;
    
    private final List<ChainBeforeListener> beforeListeners = new ArrayList<ChainBeforeListener>();
    private final List<ChainAfterListener> afterListeners = new ArrayList<ChainAfterListener>();
    private final List<ChainBeforeStepListener> beforeStepListeners = new ArrayList<ChainBeforeStepListener>();
    private final List<ChainAfterStepListener> afterStepListeners = new ArrayList<ChainAfterStepListener>();
    
    private List<ChainStepExecutor> executedSteps;
    private List<ChainStepExecutor> skippedSteps;
    private List<ChainStepExecutor> processedSteps;
    
    private Deque<ChainStepExecutor> notExecutedSteps;
    
    private ChainStorage storage;
    
    public ChainWorker(String name, List<ChainStepExecutor> steps) {
        this.steps = steps;
        this.name = name;
    }
    
    public ChainWorker(String name, ChainStepExecutor[] steps) {
        this(name, Arrays.asList(steps));
    }

    public synchronized Object execute(Object... params) {
        initExecution(params);
        
        while(internalExecuteNextStep());
        
        Object result = storage.getResult();
        finishExecution();
        return result;
    }

    public synchronized Object executeNextStep(Object... params) {
        if (!isInProgress()) {
            initExecution(null);
        }
        storage.setStepParameters(params);
        boolean continueStatus = internalExecuteNextStep();
        
        Object result = storage.getResult();
        if (!continueStatus) {
            finishExecution();
        }
        return result;
    }
    
    public ChainStorage getStorage() {
        return storage;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void addListener(BaseChainListener listener) {
        if (listener instanceof ChainBeforeListener) {
            beforeListeners.add((ChainBeforeListener)listener);
        } else if (listener instanceof ChainAfterListener) {
            afterListeners.add((ChainAfterListener)listener);
        } else if (listener instanceof ChainBeforeStepListener) {
            beforeStepListeners.add((ChainBeforeStepListener)listener);
        } else if (listener instanceof ChainAfterStepListener) {
            afterStepListeners.add((ChainAfterStepListener)listener);
        }
    }
    
    public void removeListener(BaseChainListener listener) {
        if (listener instanceof ChainBeforeListener) {
            beforeListeners.remove((ChainBeforeListener)listener);
        } else if (listener instanceof ChainAfterListener) {
            afterListeners.remove((ChainAfterListener)listener);
        } else if (listener instanceof ChainBeforeStepListener) {
            beforeStepListeners.remove((ChainBeforeStepListener)listener);
        } else if (listener instanceof ChainAfterStepListener) {
            afterStepListeners.remove((ChainAfterStepListener)listener);
        }
    }
    
    public boolean isInProgress() {
        return storage != null;
    }

    public List<ChainStepExecutor> getSteps() {
        return Collections.unmodifiableList(steps);
    }
    
    public List<ChainStepExecutor> getExecutedSteps() {
        if (executedSteps == null) {
            return null;
        }
        return Collections.unmodifiableList(executedSteps);
    }
    
    public List<ChainStepExecutor> getSkippedSteps() {
        if (skippedSteps == null) {
            return null;
        }
        return Collections.unmodifiableList(skippedSteps);
    }
    
    public List<ChainStepExecutor> getProcessedSteps() {
        if (processedSteps == null) {
            return null;
        }
        return Collections.unmodifiableList(processedSteps);
    }
    
    private void initExecution(Object... params) {
        storage = new ChainStorage(this, params);
        id = name + "-" + UUID.randomUUID().toString();
        executedSteps = new ArrayList<ChainStepExecutor>();
        skippedSteps = new ArrayList<ChainStepExecutor>();
        processedSteps = new UnionList(executedSteps, skippedSteps);
        notExecutedSteps = new LinkedList<ChainStepExecutor>(steps);
        
        for(ChainBeforeListener listener : beforeListeners) listener.onBefore(this, storage);
    }

    private void finishExecution() {
        for(ChainAfterListener listener : afterListeners) listener.onAfter(this, storage);
        storage = null;
        id = null;
        executedSteps = null;
        skippedSteps = null;
        processedSteps = null;
        notExecutedSteps = null;
    }

    private boolean internalExecuteNextStep() {
        int lastSize = notExecutedSteps.size();
            
        ChainStepExecutor step;
        int count = 0;
        while( (step = notExecutedSteps.pollFirst()) != null && count < lastSize) {
            for(ChainBeforeStepListener listener : beforeStepListeners) listener.onBeforeStep(this, step, storage);

            ChainStepExecutor.Status status = step.execute(storage);

            for(ChainAfterStepListener listener : afterStepListeners) listener.onAfterStep(this, step, storage, status);

            if (ChainStepExecutor.Status.POSTPONE == status) {
                notExecutedSteps.addLast(step);
            }
            if (ChainStepExecutor.Status.SKIP == status) {
                skippedSteps.add(step);
            } else if (status == null || ChainStepExecutor.Status.DONE == status) {
                executedSteps.add(step);
                return notExecutedSteps.size() > 0;
            }
            count++;
        }
        
        return false;
    }
    
}
