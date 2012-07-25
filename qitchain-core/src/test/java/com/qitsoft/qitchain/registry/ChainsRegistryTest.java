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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.qitsoft.qitchain.annotation.Chain;
import com.qitsoft.qitchain.annotation.ChainListenerType;
import fixtures.*;
import java.util.Collection;
import java.util.Iterator;
import org.hamcrest.*;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class ChainsRegistryTest {
    private static final String CHAIN_NAME = "sample-chain";

    private ChainsRegistry registry = new ChainsRegistry();
    
    @Test
    public void testScanByAnnotation() throws ClassNotFoundException {
        assertNotNull(registry.getChainInfo(CHAIN_NAME));
    }
    
    @Test
    public void testGetSameChainInfoMultipleTimesByName() {
        registry = Mockito.spy(registry);
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        ChainInfo chainInfo1 = registry.getChainInfo(CHAIN_NAME);
        assertSame(chainInfo, chainInfo1);
        Mockito.verify(registry, Mockito.times(1)).scan();
    }
    
    @Test
    public void testGetSameChainInfoMultipleTimesByAnnotation() {
        registry = Mockito.spy(registry);
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        ChainInfo chainInfo1 = registry.getChainInfo(TestChain.class);
        assertSame(chainInfo, chainInfo1);
        Mockito.verify(registry, Mockito.times(1)).scan();
    }
    
    @Test
    public void testGetChainDataByName() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)SampleChain.class));
        assertEquals(CHAIN_NAME, chainInfo.getName());
        assertEquals(Chain.class, chainInfo.getAnnotationType());
        
        assertNotNull(chainInfo.getSteps());
        assertThat(chainInfo.getSteps().size(), Matchers.greaterThan(0));
    }
    
    @Test
    public void testGetChainDataByNameMerged() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)SampleChain.class));
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)SampleChain1.class));
        assertEquals(CHAIN_NAME, chainInfo.getName());
        assertEquals(Chain.class, chainInfo.getAnnotationType());
        
        assertNotNull(chainInfo.getSteps());
        assertThat(chainInfo.getSteps().size(), Matchers.greaterThan(1));
    }
    
    @Test
    public void testGetChainDataBySubAnnotation() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)TestChainObject.class));
        assertEquals(TestChain.class.getName(), chainInfo.getName());
        assertEquals(TestChain.class, chainInfo.getAnnotationType());
        
        assertNotNull(chainInfo.getSteps());
        assertThat(chainInfo.getSteps().size(), Matchers.greaterThan(0));
    }
    
    @Test
    public void testGetChainDataBySubAnnotationMerged() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)TestChainObject.class));
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)TestChainObject1.class));
        assertEquals(TestChain.class.getName(), chainInfo.getName());
        assertEquals(TestChain.class, chainInfo.getAnnotationType());
        
        assertNotNull(chainInfo.getSteps());
        assertThat(chainInfo.getSteps().size(), Matchers.greaterThan(1));
    }

    @Test
    public void testAddStepFromChainByName() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(SampleChain.class, "step1")));
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(SampleChain1.class, "step2")));
        
        // Add by chain step object
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(SampleChainStep3.class, "execute")));
        
        // Add by ChainSteps annotation
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(UnnannotatedChainStep.class, "execute")));
    }

    @Test
    public void testChainOrderByNamedChain() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        
        Matcher<ChainStepInfo> step1Metcher = chainStep(SampleChain1.class, "step2");
        Matcher<ChainStepInfo> step2Metcher = chainStep(UnnannotatedChainStep.class, "execute");
        Matcher<ChainStepInfo> step3Metcher = chainStep(UnnannotatedChainStep2.class, "execute");
        Matcher<ChainStepInfo> step4Metcher = chainStep(SampleChain.class, "step1");
        Matcher<ChainStepInfo> step5Metcher = chainStep(SampleChainStep3.class, "execute");
        
        assertThat(chainInfo.getSteps(), stepBefore(step1Metcher, step2Metcher));
        assertThat(chainInfo.getSteps(), stepBefore(step2Metcher, step3Metcher));
        assertThat(chainInfo.getSteps(), stepBefore(step3Metcher, step4Metcher));
        assertThat(chainInfo.getSteps(), stepBefore(step4Metcher, step5Metcher));
        
    }

    @Test
    public void testAddStepFromChainByAnnotation() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(TestChainObject.class, "step1")));
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(TestChainObject1.class, "step2")));
        
        // Add by chain step object
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(TestChainStep2.class, "execute")));
        
        // Add by ChainSteps annotation
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(UnnannotatedChainStep.class, "execute")));
    }

    @Test
    public void testChainOrderByAnnotatedChain() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        
        Matcher<ChainStepInfo> step1Metcher = chainStep(TestChainObject1.class, "step2");
        Matcher<ChainStepInfo> step2Metcher = chainStep(UnnannotatedChainStep.class, "execute");
        Matcher<ChainStepInfo> step3Metcher = chainStep(UnnannotatedChainStep2.class, "execute");
        Matcher<ChainStepInfo> step4Metcher = chainStep(TestChainObject.class, "step1");
        Matcher<ChainStepInfo> step5Metcher = chainStep(TestChainStep2.class, "execute");
        
        assertThat(chainInfo.getSteps(), stepBefore(step1Metcher, step2Metcher));
        assertThat(chainInfo.getSteps(), stepBefore(step2Metcher, step3Metcher));
        assertThat(chainInfo.getSteps(), stepBefore(step3Metcher, step4Metcher));
        assertThat(chainInfo.getSteps(), stepBefore(step4Metcher, step5Metcher));
    }
    
    @Test
    public void testChainByNameWithoutChainObject() {
        ChainInfo chainInfo = registry.getChainInfo("chain2");
        assertNotNull(chainInfo);
        assertEquals("chain2", chainInfo.getName());
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)Chain2Step1.class));
        assertThat(chainInfo.getAnnotationType(), Matchers.equalTo((Class)Chain.class));
        assertThat(chainInfo.getSteps().size(), Matchers.greaterThan(0));
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(Chain2Step1.class, "execute")));
    }
    
    @Test
    public void testChainByAnnotationWithoutChainObject() {
        ChainInfo chainInfo = registry.getChainInfo(Chain3.class);
        assertNotNull(chainInfo);
        assertEquals(Chain3.class.getName(), chainInfo.getName());
        assertThat(chainInfo.getTypes(), Matchers.hasItem((Class)Chain3Step1.class));
        assertThat(chainInfo.getAnnotationType(), Matchers.equalTo((Class)Chain3.class));
        assertThat(chainInfo.getSteps().size(), Matchers.greaterThan(0));
        assertThat(chainInfo.getSteps(), Matchers.hasItem(chainStep(Chain3Step1.class, "execute")));
    }
    
    @Test
    public void testAddListenersByMethodsInNamedChain() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        assertThat(chainInfo.getListeners().size(), Matchers.greaterThan(0));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChain.class, "beforeListener", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChain.class, "afterListener", ChainListenerType.AFTER)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChain1.class, "beforeStepListener", ChainListenerType.BEFORE_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChain1.class, "afterStepListener", ChainListenerType.AFTER_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChainBeforeListener1.class, "onBefore", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChainBeforeListener2.class, "onBefore", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChainAfterListener.class, "onAfter", ChainListenerType.AFTER)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChainBeforeStepListener.class, "onBeforeStep", ChainListenerType.BEFORE_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(SampleChainAfterStepListener.class, "onAfterStep", ChainListenerType.AFTER_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(SampleAbstractChainListener.class, null, null))));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(SampleChain.class, "unknownListener1", ChainListenerType.UNKNOWN))));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(SampleChain.class, "unknownListener2", ChainListenerType.UNKNOWN))));
        
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedBeforeListener.class, "onBefore", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedAfterListener.class, "onAfter", ChainListenerType.AFTER)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedBeforeStepListener.class, "onBeforeStep", ChainListenerType.BEFORE_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedAfterStepListener.class, "onAfterStep", ChainListenerType.AFTER_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(UnnamedAbstractListener.class, null, null))));
    }
    
    @Test
    public void testAddListenersByMethodsInAnnotatedChain() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        assertThat(chainInfo.getListeners().size(), Matchers.greaterThan(0));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainObject.class, "beforeListener", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainObject.class, "afterListener", ChainListenerType.AFTER)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainObject1.class, "beforeStepListener", ChainListenerType.BEFORE_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainObject1.class, "afterStepListener", ChainListenerType.AFTER_STEP)));

        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainBeforeListener1.class, "onBefore", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainBeforeListener2.class, "onBefore", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainAfterListener.class, "onAfter", ChainListenerType.AFTER)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainBeforeStepListener.class, "onBeforeStep", ChainListenerType.BEFORE_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(TestChainAfterStepListener.class, "onAfterStep", ChainListenerType.AFTER_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(TestAbstractChainListener.class, null, null))));
        
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(TestChainObject.class, "unknownListener1", ChainListenerType.UNKNOWN))));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(TestChainObject.class, "unknownListener2", ChainListenerType.UNKNOWN))));

        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedBeforeListener.class, "onBefore", ChainListenerType.BEFORE)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedAfterListener.class, "onAfter", ChainListenerType.AFTER)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedBeforeStepListener.class, "onBeforeStep", ChainListenerType.BEFORE_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.hasItem(chainListener(UnnamedAfterStepListener.class, "onAfterStep", ChainListenerType.AFTER_STEP)));
        assertThat(chainInfo.getListeners(), Matchers.not(Matchers.hasItem(chainListener(UnnamedAbstractListener.class, null, null))));
    }
    
    @Test
    public void testGetInstance() {
        assertNotNull(ChainsRegistry.getInstance());
    }
    
    @Test
    public void testAvoidStepsDuplicationForNamedChain() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        Collection<ChainStepInfo> c = Collections2.filter(chainInfo.getSteps(), new Predicate<ChainStepInfo>() {

            public boolean apply(ChainStepInfo input) {
                return input.getStepClass().equals(UnnannotatedChainStep.class);
            }
        });
        for(ChainStepInfo step : c) {
            System.out.println(step.getStepClass().getName() + " " + step.getMethod().getName());
        }
        assertEquals(1, c.size());
    }
    
    @Test
    public void testAvoidStepsDuplicationForAnnotatedChain() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        Collection c = Collections2.filter(chainInfo.getSteps(), new Predicate<ChainStepInfo>() {

            public boolean apply(ChainStepInfo input) {
                return input.getStepClass() == UnnannotatedChainStep.class;
            }
        });
        assertEquals(1, c.size());
    }
    
    @Test
    public void testAvoidListenersDuplicationForNamedChain() {
        ChainInfo chainInfo = registry.getChainInfo(CHAIN_NAME);
        Collection c = Collections2.filter(chainInfo.getListeners(), new Predicate<ChainListenerInfo>() {

            public boolean apply(ChainListenerInfo input) {
                return input.getListenerClass() == UnnamedBeforeListener.class;
            }
        });
        assertEquals(1, c.size());
    }
    
    @Test
    public void testAvoidListenersDuplicationForAnnotatedChain() {
        ChainInfo chainInfo = registry.getChainInfo(TestChain.class);
        Collection c = Collections2.filter(chainInfo.getListeners(), new Predicate<ChainListenerInfo>() {

            public boolean apply(ChainListenerInfo input) {
                return input.getListenerClass() == UnnamedBeforeListener.class;
            }
        });
        assertEquals(1, c.size());
    }
    
    private Matcher<ChainStepInfo> chainStep(final Class klass, final String methodName) {
        return new BaseMatcher<ChainStepInfo>(){

            public boolean matches(Object item) {
                ChainStepInfo info = (ChainStepInfo) item;
                return klass == info.getStepClass()
                        && methodName.equals(info.getMethod().getName());
            }

            public void describeTo(Description description) {
                description.appendText(String.format("chain step from class [%s] and method [%s]", klass.getName(), methodName));
            }

        };
    }
    
    private Matcher<ChainListenerInfo> chainListener(final Class klass, final String methodName, final ChainListenerType type) {
        return new BaseMatcher<ChainListenerInfo>(){

            public boolean matches(Object item) {
                ChainListenerInfo info = (ChainListenerInfo) item;
                return klass == info.getListenerClass()
                        && (methodName == null || methodName.equals(info.getMethod().getName()))
                        && (type == null || type.equals(info.getType()));
            }

            public void describeTo(Description description) {
                description.appendText(String.format("chain listener from class [%s] and method [%s] of type [%s]", klass.getName(), methodName, type.name()));
            }

        };
    }
    
    private Matcher<Collection<ChainStepInfo>> stepBefore(final Matcher<ChainStepInfo> first, final Matcher<ChainStepInfo> second) {
        return new BaseMatcher<Collection<ChainStepInfo>>() {

            private StringBuilder firstChainText = new StringBuilder();
            private StringBuilder secondChainText = new StringBuilder();
            
            public boolean matches(Object item) {
                Collection<ChainStepInfo> list = (Collection<ChainStepInfo>) item;
                
                int firstIndex = getIndex(list, first, firstChainText);
                int secondIndex = getIndex(list, second, secondChainText);
                
                return firstIndex < secondIndex;
            }
            
            private int getIndex(Collection<ChainStepInfo> list, Matcher<ChainStepInfo> stepMatcher, StringBuilder text) {
                Iterator<ChainStepInfo> iterator = list.iterator();
                int i = 0;
                while(iterator.hasNext()) {
                    if (stepMatcher.matches(iterator.next())) {
                        stepMatcher.describeTo(new StringDescription(text));
                        return i;
                    }
                    i++;
                }
                return -1;
            }

            public void describeTo(Description description) {
                description.appendText(String.format("the %s is before the %s", firstChainText, secondChainText));
            }
            
        };
    }
}
