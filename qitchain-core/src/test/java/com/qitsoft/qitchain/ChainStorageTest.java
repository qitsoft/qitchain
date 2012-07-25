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
import java.io.*;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class ChainStorageTest {
    
    private ChainStorage storage;
    
    @Mock
    private ChainWorker chainWorker;
    
    private Object[] parameters = new Object[] {"test", 1, 3.14 };
    
    @Before
    public void setUp() {
        storage = new ChainStorage(chainWorker, parameters);
    }
    
    @Test
    public void testGetByTypeOneAddedObject() {
        storage.put("Some text");
        assertEquals("Some text", storage.get(String.class));
    }
    
    @Test
    public void testGetByTypeManyAddedObject() {
        storage.put("Some text");
        storage.put("Other text");
        assertEquals("Other text", storage.get(String.class));
    }
    
    @Test
    public void testGetByNameAddedOneObject() {
        storage.put("data","Some text");
        assertEquals("Some text", storage.get("data"));
    }
    
    @Test
    public void testGetByNameAddedMultipleObject() {
        storage.put("data","First text");
        storage.put("data","Some text");
        assertEquals("Some text", storage.get("data"));
        assertEquals(1, storage.list(String.class).size());
    }
    
    @Test
    public void testGetByNameAndClass() {
        storage.put("data","Some text");
        assertEquals("Some text", storage.get("data", String.class));
    }
    
    @Test
    public void testListByTypeOneObject() {
        storage.put("Some text");
        assertArrayEquals(new String[]{"Some text"}, storage.list(String.class).toArray());
    }
    
    @Test
    public void testListByTypeMultipleObject() {
        storage.put("Some text");
        storage.put("Other text");
        assertArrayEquals(new String[]{"Other text", "Some text"}, storage.list(String.class).toArray());
    }
    
    @Test
    public void testGetByNameAddedMultipleTimes() {
        storage.put("data","Some text");
        storage.put("data", "Other text");
        assertEquals("Other text", storage.get("data"));
        assertArrayEquals(new String[]{"Other text"}, storage.list(String.class).toArray());
    }
    
    @Test
    public void testGetByNameAndTypeFails() {
        storage.put("data", "Some text");
        assertNull(storage.get("data", List.class));
    }
    
    @Test
    public void testPutNullByName() {
        storage.put("data", null);
        assertNull(storage.get("data"));
    }
    
    @Test
    public void testPutDataUnderNullName() {
        storage.put(null, "Hello");
        assertNotNull(storage.get(String.class));
        assertNull(storage.get((String)null));
    }
    
    @Test
    public void testPutNullByName1() {
        storage.put("data", "The text");
        storage.put("data", null);
        assertNull(storage.get(String.class));
    }
    
    @Test
    public void testPutNullByClass() {
        storage.put(null);
    }
    
    @Test
    public void testGetByNameNotAdded() {
        assertNull(storage.get("data"));
    }
    
    @Test
    public void testGetByClassNotAdded() {
        assertNull(storage.get(String.class));
    }
    
    @Test
    public void testGetByNameAndClassNotAdded() {
        assertNull(storage.get("data", String.class));
    }
    
    @Test
    public void testListBySuperClass() {
        storage.put(2);
        storage.put(3.4);
        assertThat(storage.list(Number.class), Matchers.hasItem((Number)2));
        assertThat(storage.list(Number.class), Matchers.hasItem((Number)3.4));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructWithNullWorker() {
        storage = new ChainStorage(null, parameters);
    }
    
    @Test
    public void testConstructWithNullParameters() {
        storage = new ChainStorage(chainWorker, null);
        assertNull(storage.getParameters());
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ChainStepExecutor step = new ChainStepExecutorImpl();
        
        ChainWorker worker = new ChainWorker("test", Arrays.asList(new ChainStepExecutor[]{step}));
        storage = new ChainStorage(worker, parameters);
        
        storage.put("The unnamed text");
        storage.put("data", "The named text");
        storage.setResult("The result");
        storage.setStepParameters(new String[]{"stepArg0", "stepArg1"});
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(storage);
        out.close();
        
        
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        
        ChainStorage storage1 = (ChainStorage) in.readObject();
        in.close();
        bout.close();
        bin.close();
        
        assertArrayEquals(storage.list(String.class).toArray(), storage1.list(String.class).toArray());
        assertEquals(storage.get("data"), storage1.get("data"));
        assertEquals(storage.getResult(), storage1.getResult());
        assertArrayEquals(storage.getStepParameters(), storage1.getStepParameters());
        assertArrayEquals(storage.getParameters(), storage1.getParameters());
        assertNotNull(storage1.getChainWorker());
    }

    private static class ChainStepExecutorImpl implements ChainStepExecutor {

        public ChainStepExecutorImpl() {
        }

        public Status execute(ChainStorage storage) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
 }
