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
package com.qitsoft.qitchain.registry;

import com.qitsoft.qitchain.annotation.ChainListenerType;
import fixtures.SampleChain;
import fixtures.SampleChain1;
import fixtures.UnnamedBeforeListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;
/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainInfoTest {
    
    private ChainInfo chainInfo = new ChainInfo();
    
    private ChainInfo.ChainStepComparator comparator = new ChainInfo.ChainStepComparator();
    
    private Method method1;
    private Method method2;
    private Method method1_dup;
    
    @Before
    public void setUp() throws Exception {
        method1 = SampleChain.class.getMethod("step1");
        method1_dup = SampleChain.class.getMethod("step1");
        method2 = SampleChain.class.getMethod("beforeListener");
    }
    
    @Test
    public void testComparator() {
        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain1.class, method2, 0), 
                new ChainStepInfo(SampleChain.class, method2, -1)), 
                Matchers.lessThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 0), 
                new ChainStepInfo(SampleChain1.class, method2, 1)), 
                Matchers.lessThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, -1), 
                new ChainStepInfo(SampleChain1.class, method2, 0)), 
                Matchers.greaterThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 1), 
                new ChainStepInfo(SampleChain1.class, method2, 0)), 
                Matchers.greaterThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 1), 
                new ChainStepInfo(SampleChain1.class, method1, 1)), 
                Matchers.lessThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain1.class, method1, 1), 
                new ChainStepInfo(SampleChain.class, method1, 1)), 
                Matchers.greaterThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method2, 1), 
                new ChainStepInfo(SampleChain.class, method1, 1)), 
                Matchers.lessThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 1), 
                new ChainStepInfo(SampleChain.class, method2, 1)), 
                Matchers.greaterThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 1), 
                new ChainStepInfo(SampleChain.class, method1, 1)), 
                Matchers.equalTo(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, -1), 
                new ChainStepInfo(SampleChain1.class, method1, -1)), 
                Matchers.lessThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain1.class, method1, -1), 
                new ChainStepInfo(SampleChain.class, method1, -1)), 
                Matchers.greaterThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method2, -1), 
                new ChainStepInfo(SampleChain.class, method1, -1)), 
                Matchers.lessThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, -1), 
                new ChainStepInfo(SampleChain.class, method2, -1)), 
                Matchers.greaterThan(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, -1), 
                new ChainStepInfo(SampleChain.class, method1, -1)), 
                Matchers.equalTo(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 1), 
                new ChainStepInfo(SampleChain.class, method1, 0)), 
                Matchers.equalTo(0));

        assertThat(comparator.compare(
                new ChainStepInfo(SampleChain.class, method1, 1), 
                new ChainStepInfo(SampleChain.class, method1, -1)), 
                Matchers.equalTo(0));
    }
    
    
    @Test
    public void testChainStepInfoEquality() {
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 0).equals(new ChainStepInfo(SampleChain1.class, method2, -1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 0).hashCode() == new ChainStepInfo(SampleChain1.class, method2, -1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 0).equals(new ChainStepInfo(SampleChain1.class, method2, 1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 0).hashCode() == new ChainStepInfo(SampleChain1.class, method2, 1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method1, -1).equals(new ChainStepInfo(SampleChain1.class, method2, 0)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, -1).hashCode() == new ChainStepInfo(SampleChain1.class, method2, 0).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 1).equals(new ChainStepInfo(SampleChain1.class, method2, 0)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 1).hashCode() == new ChainStepInfo(SampleChain1.class, method2, 0).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 1).equals(new ChainStepInfo(SampleChain1.class, method1, 1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 1).hashCode() == new ChainStepInfo(SampleChain1.class, method1, 1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain1.class, method1, 1).equals(new ChainStepInfo(SampleChain.class, method1, 1)));
        assertFalse(new ChainStepInfo(SampleChain1.class, method1, 1).hashCode() == new ChainStepInfo(SampleChain.class, method1, 1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method2, 1).equals(new ChainStepInfo(SampleChain.class, method1, 1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method2, 1).hashCode() == new ChainStepInfo(SampleChain.class, method1, 1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 1).equals(new ChainStepInfo(SampleChain.class, method2, 1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, 1).hashCode() == new ChainStepInfo(SampleChain.class, method2, 1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method1, -1).equals(new ChainStepInfo(SampleChain1.class, method1, -1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method1, -1).hashCode() == new ChainStepInfo(SampleChain1.class, method1, -1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain1.class, method1, -1).equals(new ChainStepInfo(SampleChain.class, method1, -1)));
        assertFalse(new ChainStepInfo(SampleChain1.class, method1, -1).hashCode() == new ChainStepInfo(SampleChain.class, method1, -1).hashCode());
        
        assertFalse(new ChainStepInfo(SampleChain.class, method2, -1).equals(new ChainStepInfo(SampleChain.class, method1, -1)));
        assertFalse(new ChainStepInfo(SampleChain.class, method2, -1).hashCode() == new ChainStepInfo(SampleChain.class, method1, -1).hashCode());

        assertTrue(new ChainStepInfo(SampleChain.class, method1, -1).equals(new ChainStepInfo(SampleChain.class, method1, -1)));
        assertTrue(new ChainStepInfo(SampleChain.class, method1, -1).hashCode() == new ChainStepInfo(SampleChain.class, method1, -1).hashCode());
        
        assertTrue(new ChainStepInfo(SampleChain.class, method1, 1).equals(new ChainStepInfo(SampleChain.class, method1, 0)));
        assertTrue(new ChainStepInfo(SampleChain.class, method1, 1).hashCode() == new ChainStepInfo(SampleChain.class, method1, 0).hashCode());
        
        assertTrue(new ChainStepInfo(SampleChain.class, method1, 1).equals(new ChainStepInfo(SampleChain.class, method1, -1)));
        assertTrue(new ChainStepInfo(SampleChain.class, method1, 1).hashCode() == new ChainStepInfo(SampleChain.class, method1, -1).hashCode());
    }
    
    @Test
    public void testSortOnAddSteps() {
        ChainStepInfo step1 = new ChainStepInfo(SampleChain.class, method1, 0);
        ChainStepInfo step2 = new ChainStepInfo(SampleChain1.class, method2, 1);
        List<ChainStepInfo> steps = Arrays.asList(step2, step1);
        chainInfo.addSteps(steps);
        assertArrayEquals(new Object[]{step1, step2}, chainInfo.getSteps().toArray());
    }
    
    @Test
    public void testAvoidStepsDuplication() {
        ChainStepInfo step1 = new ChainStepInfo(SampleChain.class, method1, 0);
        ChainStepInfo step2 = new ChainStepInfo(SampleChain.class, method1, 1);
        ChainStepInfo step3 = new ChainStepInfo(SampleChain.class, method1_dup, 2);
        chainInfo.addSteps(Arrays.asList(step1, step2, step3));
        
        assertEquals(1, chainInfo.getSteps().size());
        assertEquals(step1, chainInfo.getSteps().get(0));
    }
    
    @Test
    public void testAvoidListenersDuplication() {
        ChainListenerInfo listener1 = new ChainListenerInfo(SampleChain.class, method1, ChainListenerType.AFTER);
        ChainListenerInfo listener2 = new ChainListenerInfo(SampleChain.class, method1, ChainListenerType.AFTER);
        ChainListenerInfo listener3 = new ChainListenerInfo(SampleChain.class, method1_dup, ChainListenerType.AFTER);
        chainInfo.addListeners(Arrays.asList(listener1, listener2, listener3));
        
        assertEquals(1, chainInfo.getListeners().size());
        assertEquals(listener1, chainInfo.getListeners().get(0));
    }
    
}
