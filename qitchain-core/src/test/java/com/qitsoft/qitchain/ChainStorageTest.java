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

import org.junit.Before;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainStorageTest {
    
    private ChainStorage storage;
    
    @Before
    public void setUp() {
        storage = new ChainStorage();
    }
    
    @Test
    public void testAddUnnamedObject() {
        storage.put("Some text");
        assertEquals("Some text", storage.get(String.class));
    }
    
    @Test
    public void testAddNamedObject() {
        storage.put("data","Some text");
        assertEquals("Some text", storage.get("data"));
    }
    
    @Test
    public void testAddNamedObjectAndGetByClass() {
        storage.put("data","Some text");
        assertEquals("Some text", storage.get(String.class));
    }
    
    @Test
    public void testGetByNameAndClass() {
        storage.put("data","Some text");
        assertEquals("Some text", storage.get("data", String.class));
    }
    
    @Test
    public void testGetByNameAndClass() {
        storage.put("data","Some text");
        assertEquals("Some text", storage.get("data", String.class));
    }
    
}
