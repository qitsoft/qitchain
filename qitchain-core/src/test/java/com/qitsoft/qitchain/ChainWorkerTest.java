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
package com.qitsoft.qitchain;

import com.qitsoft.qitchain.ChainStepExecutor.Status;
import com.qitsoft.qitchain.listeners.ChainAfterListener;
import com.qitsoft.qitchain.listeners.ChainAfterStepListener;
import com.qitsoft.qitchain.listeners.ChainBeforeListener;
import com.qitsoft.qitchain.listeners.ChainBeforeStepListener;
import java.io.*;
import java.util.Arrays;
import java.util.Stack;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class ChainWorkerTest {
    private ArgumentCaptor<ChainStorage> storageCaptor;
    
    private ChainWorker worker;
    
    @Mock
    private ChainStepExecutor executor1;
    
    @Mock
    private ChainStepExecutor executor2;
    
    @Mock
    private ChainStepExecutor executor3;
    
    @Mock
    private ChainBeforeListener beforeListener;
    
    @Mock
    private ChainAfterListener afterListener;
    
    @Mock
    private ChainBeforeStepListener beforeStepListener;
    
    @Mock
    private ChainAfterStepListener afterStepListener;
    
    @Before
    public void setUp() {
        storageCaptor = ArgumentCaptor.forClass(ChainStorage.class);
        
        worker = spy(new ChainWorker("test", new ChainStepExecutor[]{executor1, executor2, executor3}));
    }
    
    @Test
    public void testInstantiation() {
        assertEquals("test", worker.getName());
        assertArrayEquals(new ChainStepExecutor[]{executor1, executor2, executor3}, worker.getSteps().toArray());
    }
    
    @Test
    public void testExecute() {
        worker.execute();
        InOrder executions = inOrder(executor1, executor2, executor3);
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verify(executor2).execute(any(ChainStorage.class));
        executions.verify(executor3).execute(any(ChainStorage.class));
    }
    
    @Test
    public void testExecuteGenerateStorage() {
        worker.execute();
        verify(executor1).execute(notNull(ChainStorage.class));
    }
    
    @Test
    public void testExecuteSetStorageChainWorker() {
        when(executor1.execute(any(ChainStorage.class))).thenAnswer(new Answer<ChainStepExecutor.Status>(){

            public Status answer(InvocationOnMock invocation) throws Throwable {
                ChainStorage storage = (ChainStorage) invocation.getArguments()[0];
                assertEquals(worker, storage.getChainWorker());
                return Status.DONE;
            }
        });
        worker.execute();
    }
    
    @Test
    public void testExecuteSetStorageParameters() {
        final Object[] params = new Object[] {"arg0", 2, 6.28};
        
        when(executor1.execute(any(ChainStorage.class))).thenAnswer(new Answer<ChainStepExecutor.Status>(){

            public Status answer(InvocationOnMock invocation) throws Throwable {
                ChainStorage storage = (ChainStorage) invocation.getArguments()[0];
                assertNotNull(storage.getParameters());
                assertTrue(storage.getParameters().length > 0);
                assertArrayEquals(params, storage.getParameters());
                return Status.DONE;
            }
        });
        worker.execute(params);
    }
    
    @Test
    public void testExecuteGenerateId() {
        final Stack<String> id = new Stack<String>();
        when(executor1.execute(any(ChainStorage.class))).thenAnswer(new Answer<ChainStepExecutor.Status>() {

            public Status answer(InvocationOnMock invocation) throws Throwable {
                ChainStorage storage = (ChainStorage) invocation.getArguments()[0];
                id.push(storage.getChainWorker().getId());
                return Status.DONE;
            }
            
        });
        worker.execute();
        worker.execute();
        String id1 = id.pop();
        assertNotNull(id1);
        String id2 = id.pop();
        assertNotNull(id2);
        assertThat(id1, Matchers.not(Matchers.equalTo(id2)));
    }
    
    @Test
    public void testExecuteCleanupStorage() {
        worker.execute();
        assertNull(worker.getStorage());
    }
    
    @Test
    public void testExecuteCleanupId() {
        worker.execute();
        assertNull(worker.getId());
    }
    
    @Test
    public void testIsActive() {
        when(executor1.execute(any(ChainStorage.class))).thenAnswer(new Answer<ChainStepExecutor.Status>() {

            public Status answer(InvocationOnMock invocation) throws Throwable {
                ChainStorage storage = (ChainStorage) invocation.getArguments()[0];
                assertTrue(storage.getChainWorker().isInProgress());
                return Status.DONE;
            }
        });
        assertFalse(worker.isInProgress());
    } 
    
    
    @Test
    public void testExecuteNullStepParameters() {
        final Object[] params = new Object[] {"arg0", 2, 6.28};
        
        when(executor1.execute(any(ChainStorage.class))).thenAnswer(new Answer<ChainStepExecutor.Status>(){

            public Status answer(InvocationOnMock invocation) throws Throwable {
                ChainStorage storage = (ChainStorage) invocation.getArguments()[0];
                assertNull(storage.getStepParameters());
                return Status.DONE;
            }
        });
        worker.execute(params);
    }
    
    @Test
    public void testExecuteReturnResult() {
        when(executor1.execute(any(ChainStorage.class))).then(new Answer<ChainStepExecutor.Status>() {

            public Status answer(InvocationOnMock invocation) throws Throwable {
                ChainStorage storage = (ChainStorage) invocation.getArguments()[0];
                storage.setResult("the method result");
                return Status.DONE;
            }
        });
        
        assertEquals("the method result", worker.execute());
    }
    
    @Test
    public void testSetupExecutedAndSkippedSteps() {
        when(executor2.execute(any(ChainStorage.class))).then(new Answer<ChainStepExecutor.Status>() {

            public Status answer(InvocationOnMock invocation) throws Throwable {
                assertNotNull(worker.getExecutedSteps());
                assertNotNull(worker.getSkippedSteps());
                assertNotNull(worker.getProcessedSteps());
                
                return Status.DONE;
            }
        });
        worker.execute();        
    }
    
    @Test
    public void testClearExecutedAndSkippedSteps() {
        worker.execute();        
        assertNull(worker.getExecutedSteps());
        assertNull(worker.getSkippedSteps());
        assertNull(worker.getProcessedSteps());
    }
    
    @Test
    public void testGetExecutedSteps() {
        when(executor2.execute(any(ChainStorage.class))).then(new Answer<ChainStepExecutor.Status>() {

            public Status answer(InvocationOnMock invocation) throws Throwable {
                assertThat(worker.getExecutedSteps(), Matchers.hasItem(executor1));
                assertTrue(worker.getSkippedSteps().isEmpty());
                
                return Status.DONE;
            }
        });
        worker.execute();
    }
    
    @Test
    public void testGetSkippedSteps() {
        when(executor1.execute(any(ChainStorage.class))).thenReturn(Status.SKIP);

        final Stack stackExecutions = new Stack();
        Answer<Status> answer = new Answer<ChainStepExecutor.Status>() {

            public Status answer(InvocationOnMock invocation) throws Throwable {
                assertThat(worker.getSkippedSteps(), Matchers.hasItem(executor1));
                stackExecutions.push("1");
                
                return Status.DONE;
            }
        };

        when(executor2.execute(any(ChainStorage.class))).then(answer);
        when(executor3.execute(any(ChainStorage.class))).then(answer);
        
        worker.execute();

        assertEquals(2, stackExecutions.size());
    }
        
    @Test
    public void testPostponeStep() {
        when(executor1.execute(any(ChainStorage.class))).thenReturn(Status.POSTPONE).thenReturn(Status.DONE);
        when(executor2.execute(any(ChainStorage.class))).thenReturn(Status.DONE);
        worker.execute();
        
        InOrder executions = inOrder(executor1, executor2);
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verify(executor2).execute(any(ChainStorage.class));
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verifyNoMoreInteractions();
    }
        
    @Test
    public void testInfinitePostponeStep() {
        when(executor1.execute(any(ChainStorage.class))).thenReturn(Status.POSTPONE);
        when(executor2.execute(any(ChainStorage.class))).thenReturn(Status.DONE);
        when(executor3.execute(any(ChainStorage.class))).thenReturn(Status.POSTPONE);
        worker.execute();
        
        InOrder executions = inOrder(executor1, executor2, executor3);
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verify(executor2).execute(any(ChainStorage.class));
        executions.verify(executor3).execute(any(ChainStorage.class));
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verifyNoMoreInteractions();
    }
    
    @Test
    public void testListenBeforeAndAfter() {
        worker.addListener(beforeListener);
        worker.addListener(afterListener);
        worker.execute();
        
        InOrder executions = inOrder(beforeListener, afterListener, executor1, executor2);
        
        executions.verify(beforeListener).onBefore(eq(worker), any(ChainStorage.class));
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verify(executor2).execute(any(ChainStorage.class));
        executions.verify(afterListener).onAfter(eq(worker), any(ChainStorage.class));
    }
    
    
    
    @Test
    public void testListenBeforeAndAfterStep() {
        worker.addListener(beforeStepListener);
        worker.addListener(afterStepListener);
        worker.execute();
        
        InOrder executions = inOrder(beforeStepListener, afterStepListener, executor1, executor2);
        executions.verify(beforeStepListener).onBeforeStep(eq(worker), eq(executor1), any(ChainStorage.class));
        executions.verify(executor1).execute(any(ChainStorage.class));
        executions.verify(afterStepListener).onAfterStep(eq(worker), eq(executor1), any(ChainStorage.class), any(ChainStepExecutor.Status.class));
        
        executions.verify(beforeStepListener).onBeforeStep(eq(worker), eq(executor2), any(ChainStorage.class));
        executions.verify(executor2).execute(any(ChainStorage.class));
        executions.verify(afterStepListener).onAfterStep(eq(worker), eq(executor2), any(ChainStorage.class), any(ChainStepExecutor.Status.class));
    }
    
    
    @Test
    public void testRemoveListeners() {
        worker.addListener(beforeListener);
        worker.addListener(afterListener);
        worker.addListener(beforeStepListener);
        worker.addListener(afterStepListener);

        worker.removeListener(beforeListener);
        worker.removeListener(afterListener);
        worker.removeListener(beforeStepListener);
        worker.removeListener(afterStepListener);
        
        worker.execute();
        
        verifyNoMoreInteractions(beforeListener);
        verifyNoMoreInteractions(afterListener);
        verifyNoMoreInteractions(beforeStepListener);
        verifyNoMoreInteractions(afterStepListener);
    }
    
    
    @Test
    public void testFirstExecuteNextStep() {
        worker.executeNextStep();
        
        assertNotNull(worker.getId());
        assertNotNull(worker.getStorage());
        assertEquals(1, worker.getExecutedSteps().size());
        assertEquals(executor1, worker.getExecutedSteps().get(0));
        assertEquals(0, worker.getSkippedSteps().size());
    }
    
    @Test
    public void testExecuteNextStep() {
        worker.executeNextStep();
        worker.executeNextStep();
        
        assertEquals(2, worker.getExecutedSteps().size());
        assertEquals(executor2, worker.getExecutedSteps().get(1));
        assertEquals(0, worker.getSkippedSteps().size());
    }
    
    @Test
    public void testExecuteNextPostponedStep() {
        when(executor2.execute(any(ChainStorage.class))).thenReturn(Status.POSTPONE);
        
        worker.executeNextStep();
        worker.executeNextStep();
        
        assertEquals(2, worker.getExecutedSteps().size());
        assertEquals(executor3, worker.getExecutedSteps().get(1));
        assertEquals(0, worker.getSkippedSteps().size());
        
        verify(executor2, only()).execute(any(ChainStorage.class));
    }
    
    @Test
    public void testExecuteLastStep() {
        worker.executeNextStep();
        worker.executeNextStep();
        worker.executeNextStep();
        
        assertFalse(worker.isInProgress());
        assertNull(worker.getExecutedSteps());
        assertNull(worker.getSkippedSteps());
        assertNull(worker.getProcessedSteps());
        assertNull(worker.getStorage());
        assertNull(worker.getId());
    }

    @Test
    public void testExecuteStepWithArguments() {
        when(executor1.execute(any(ChainStorage.class))).then(new Answer<ChainStepExecutor.Status>(){

            public Status answer(InvocationOnMock invocation) throws Throwable {
                assertNotNull(worker.getStorage().getStepParameters());
                assertArrayEquals(new Object[]{"arg0", 4, 3.14}, worker.getStorage().getStepParameters());
                assertNull(worker.getStorage().getParameters());
                
                return Status.DONE;
            }
        });
        
        worker.executeNextStep("arg0", 4, 3.14);
    }

    @Test
    public void testExecuteStepReturnsResult() {
        when(executor1.execute(any(ChainStorage.class))).then(new Answer<ChainStepExecutor.Status>(){

            public Status answer(InvocationOnMock invocation) throws Throwable {
                worker.getStorage().setResult("the result");
                
                return Status.DONE;
            }
        });
        
        assertEquals("the result", worker.executeNextStep());
    }
    
    @Test
    public void testExecuteStepWithSkipped() {
        when(executor1.execute(any(ChainStorage.class))).thenReturn(Status.SKIP);
        
        worker.executeNextStep();
        worker.executeNextStep();
        assertFalse(worker.isInProgress());
    }
    
    @Test
    public void testExecuteWithSkipped() {
        when(executor1.execute(any(ChainStorage.class))).thenReturn(Status.SKIP);
        
        worker.execute();
        assertFalse(worker.isInProgress());
    }
    
    @Test
    public void testSerializationNotInWork() throws IOException, ClassNotFoundException {
        worker = new ChainWorker("chainName", Arrays.asList(new ChainStepExecutor[]{new ChainStepExecutorImpl()}));
        ChainWorker worker1 = serializeDeserialize();
        
        assertNotNull(worker1.getSteps());
        assertEquals(1, worker1.getSteps().size());
        assertEquals(worker.getSteps().get(0).getClass(), worker1.getSteps().get(0).getClass());
        assertFalse(worker1.isInProgress());
        assertEquals(worker.getName(), worker1.getName());
    }
    
    @Test
    public void testSerializationInWork() throws IOException, ClassNotFoundException {
        worker = new ChainWorker("chainName", Arrays.asList(new ChainStepExecutor[]{new ChainStepExecutorImpl(), new ChainStepExecutorImpl1()}));
        worker.executeNextStep();
        
        ChainWorker worker1 = serializeDeserialize();
        
        assertNotNull(worker1.getSteps());
        assertEquals(2, worker1.getSteps().size());
        assertEquals(worker.getSteps().get(0).getClass(), worker1.getSteps().get(0).getClass());
        assertEquals(worker.getSteps().get(1).getClass(), worker1.getSteps().get(1).getClass());
        assertTrue(worker1.isInProgress());
        assertEquals(worker.getName(), worker1.getName());
        
        worker1.executeNextStep();
        assertFalse(worker1.isInProgress());
    }
    
    

    private ChainWorker serializeDeserialize() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(worker);
        out.close();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        ChainWorker worker1 = (ChainWorker) in.readObject();
        in.close();
        bout.close();
        bin.close();
        return worker1;
    }

    private static class ChainStepExecutorImpl implements ChainStepExecutor {

        public ChainStepExecutorImpl() {
        }

        public Status execute(ChainStorage storage) {
            return Status.DONE;
        }
    }    

    private static class ChainStepExecutorImpl1 implements ChainStepExecutor {

        public ChainStepExecutorImpl1() {
        }

        public Status execute(ChainStorage storage) {
            return Status.DONE;
        }
    }    
}
