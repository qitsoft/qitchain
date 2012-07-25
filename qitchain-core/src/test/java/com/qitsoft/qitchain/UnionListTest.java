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
import java.util.*;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class UnionListTest {
    
    private List<ChainStepExecutor> list1 = new ArrayList<ChainStepExecutor>();
    private List<ChainStepExecutor> list2 = new ArrayList<ChainStepExecutor>();
    
    private List<ChainStepExecutor> list = new UnionList(list1, list2);
    
    @Mock
    private ChainStepExecutor executor1;
    
    @Mock
    private ChainStepExecutor executor2;
    
    @Test
    public void testSize() {
        assertEquals(0, list.size());
        
        list1.add(executor1);
        assertEquals(1, list.size());
        
        list2.add(executor2);
        assertEquals(2, list.size());
        
        list1.clear();
        assertEquals(1, list.size());
    }
    
    @Test
    public void testEmpty() {
        assertTrue(list.isEmpty());
        
        list1.add(executor1);
        assertFalse(list.isEmpty());
        
        list2.add(executor2);
        assertFalse(list.isEmpty());
        
        list1.clear();
        assertFalse(list.isEmpty());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testThrowOnNull() {
        new UnionList(null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testThrowOnNull1() {
        new UnionList(null, list1);
    }
    
    @Test
    public void testContains() {
        assertFalse(list.contains(executor1));
        
        list1.add(executor1);
        assertTrue(list.contains(executor1));
        
        list2.add(executor2);
        assertTrue(list.contains(executor2));
        
        list1.clear();
        assertTrue(list.contains(executor2));
    }
    
    @Test
    public void testContainsAll() {
        List<ChainStepExecutor> col = Arrays.asList(new ChainStepExecutor[] {executor1, executor2});
        assertFalse(list.containsAll(col));
        
        clearLists();
        list1.add(executor1);
        assertFalse(list.containsAll(col));
        
        clearLists();
        list2.add(executor1);
        assertFalse(list.containsAll(col));

        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertTrue(list.containsAll(col));
        
        clearLists();
        list1.add(executor1);
        list1.add(executor2);
        assertTrue(list.containsAll(col));

        clearLists();
        list2.add(executor1);
        list2.add(executor2);
        assertTrue(list.containsAll(col));
    }
    
    @Test
    public void testIndexOf() {
        assertEquals(-1, list.indexOf(executor1));

        clearLists();
        list1.add(executor1);
        assertEquals(0, list.indexOf(executor1));

        clearLists();
        list2.add(executor1);
        assertEquals(0, list.indexOf(executor1));

        clearLists();
        list1.add(executor2);
        list2.add(executor1);
        assertEquals(1, list.indexOf(executor1));

        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertEquals(0, list.indexOf(executor1));
        
        clearLists();
        list1.add(executor2);
        assertEquals(-1, list.indexOf(executor1));
        
        clearLists();
        list2.add(executor2);
        assertEquals(-1, list.indexOf(executor1));
        
        clearLists();
        list1.add(executor1);
        list2.add(executor1);
        assertEquals(0, list.indexOf(executor1));
    }
    
    @Test
    public void testLastIndexOf() {
        assertEquals(-1, list.lastIndexOf(executor1));

        clearLists();
        list1.add(executor1);
        assertEquals(0, list.lastIndexOf(executor1));

        clearLists();
        list2.add(executor1);
        assertEquals(0, list.lastIndexOf(executor1));

        clearLists();
        list1.add(executor2);
        list2.add(executor1);
        assertEquals(1, list.lastIndexOf(executor1));

        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertEquals(0, list.lastIndexOf(executor1));
        
        clearLists();
        list1.add(executor2);
        assertEquals(-1, list.lastIndexOf(executor1));
        
        clearLists();
        list2.add(executor2);
        assertEquals(-1, list.lastIndexOf(executor1));
        
        clearLists();
        list1.add(executor1);
        list2.add(executor1);
        assertEquals(1, list.lastIndexOf(executor1));
    }
    
    @Test
    public void testIterator() {
        assertThat(list.iterator(), Matchers.instanceOf(ListIterator.class));
        assertThat(list.iterator(), Matchers.instanceOf(ListIterator.class));
    }
    
    @Test
    public void testSimpleIteratorForward() {
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor1, executor2}, simpleIterateForward());
        
        clearLists();
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor2}, simpleIterateForward());
        
        clearLists();
        list1.add(executor1);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, simpleIterateForward());
        
        clearLists();
        assertArrayEquals(new ChainStepExecutor[]{}, simpleIterateForward());
    }
    
    @Test
    public void testSimpleListIteratorForward() {
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor1, executor2}, simpleListIterateForward());
        
        clearLists();
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor2}, simpleListIterateForward());
        
        clearLists();
        list1.add(executor1);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, simpleListIterateForward());
        
        clearLists();
        assertArrayEquals(new ChainStepExecutor[]{}, simpleListIterateForward());
    }
    
    @Test
    public void testIteratorForward() {
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor1, executor2}, iterateForward(0));
        
        clearLists();
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor2}, iterateForward(0));
        
        clearLists();
        list1.add(executor1);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, iterateForward(0));
        
        clearLists();
        assertArrayEquals(new ChainStepExecutor[]{}, iterateForward(0));
        
        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor2}, iterateForward(1));
        
        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{}, iterateForward(2));
    }
    
    @Test
    public void testIteratorBackward() {
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor2, executor1}, iterateBackward(list.size()));
        
        clearLists();
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor2}, iterateBackward(list.size()));
        
        clearLists();
        list1.add(executor1);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, iterateBackward(list.size()));
        
        clearLists();
        assertArrayEquals(new ChainStepExecutor[]{}, iterateBackward(list.size()));
        
        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, iterateBackward(list.size()-1));
        
        clearLists();
        list1.add(executor1);
        list2.add(executor2);
        assertArrayEquals(new ChainStepExecutor[]{}, iterateBackward(list.size() - 2));
    }
    
    @Test
    public void testToArrayWithGenerics() {
        list1.add(executor1);
        list2.add(executor2);
        
        assertArrayEquals(new ChainStepExecutor[]{executor1, executor2}, list.toArray(new ChainStepExecutor[0]));
        
        list1.clear();
        assertArrayEquals(new ChainStepExecutor[]{executor2}, list.toArray(new ChainStepExecutor[0]));
        
        list2.clear();
        assertArrayEquals(new ChainStepExecutor[]{}, list.toArray(new ChainStepExecutor[0]));
        
        list1.add(executor1);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, list.toArray(new ChainStepExecutor[0]));
        
        ChainStepExecutor[] arr = new ChainStepExecutor[0];
        assertNotSame(arr, list.toArray(arr));
        
        arr = new ChainStepExecutor[1];
        assertSame(arr, list.toArray(arr));
        
        arr = new ChainStepExecutor[3];
        ChainStepExecutor[] resArr = list.toArray(arr);
        assertSame(arr, resArr);
        assertNull(resArr[1]);
        assertNull(resArr[2]);
    }
    
    @Test
    public void testToArray() {
        list1.add(executor1);
        list2.add(executor2);
        
        assertArrayEquals(new ChainStepExecutor[]{executor1, executor2}, list.toArray());
        
        list1.clear();
        assertArrayEquals(new ChainStepExecutor[]{executor2}, list.toArray());
        
        list2.clear();
        assertArrayEquals(new ChainStepExecutor[]{}, list.toArray());
        
        list1.add(executor1);
        assertArrayEquals(new ChainStepExecutor[]{executor1}, list.toArray());
        
        assertThat(list.toArray(), Matchers.instanceOf(ChainStepExecutor[].class));
    }
    
    @Test
    public void testGet() {
        list1.add(executor1);
        list2.add(executor2);
        
        assertSame(executor1, list.get(0));
        assertSame(executor2, list.get(1));
        
        list1.clear();
        assertSame(executor2, list.get(0));
        
        list2.clear();
        list1.add(executor1);
        assertSame(executor1, list.get(0));
        
        list1.clear();
        list2.clear();
        try {
            list1.add(executor1);
            list.get(1);
            fail();
        } catch(IndexOutOfBoundsException ex) {
        }
        
        try {
            list1.add(executor1);
            list.get(-1);
            fail();
        } catch(IndexOutOfBoundsException ex) {
        }
        
        try {
            list1.clear();
            list.get(0);
            fail();
        } catch(IndexOutOfBoundsException ex) {
        }
    }
    
    @Test
    public void testModifications() {
        Collection<ChainStepExecutor> col = Arrays.asList(new ChainStepExecutor[] {executor1, executor2 });
        try { list.add(executor1); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.add(0, executor1); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.addAll(col); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.addAll(0, col); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.clear(); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.remove(executor1); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.remove(0); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.removeAll(col); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.retainAll(col); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.subList(0, 1); fail(); } catch(UnsupportedOperationException ex) { }
        try { list.set(0, executor1); fail(); } catch(UnsupportedOperationException ex) { }
        
        ListIterator<ChainStepExecutor> iterator = list.listIterator();
        try { iterator.add(executor1); fail(); } catch(UnsupportedOperationException ex) { }
        try { iterator.remove(); fail(); } catch(UnsupportedOperationException ex) { }
        try { iterator.set(executor1); fail(); } catch(UnsupportedOperationException ex) { }
    }
    
    private Object[] iterateForward(int index) {
        List<ChainStepExecutor> result = new ArrayList<ChainStepExecutor>();
        ListIterator<ChainStepExecutor> i = list.listIterator(index);
        int currentIndex = index;
        while(i.hasNext()) {
            assertEquals(currentIndex, i.nextIndex());
            result.add(i.next());
            currentIndex++;
        }
        return result.toArray();
    }
    
    private Object[] simpleIterateForward() {
        List<ChainStepExecutor> result = new ArrayList<ChainStepExecutor>();
        Iterator<ChainStepExecutor> i = list.iterator();
        while(i.hasNext()) {
            result.add(i.next());
        }
        return result.toArray();
    }
    
    private Object[] simpleListIterateForward() {
        List<ChainStepExecutor> result = new ArrayList<ChainStepExecutor>();
        ListIterator<ChainStepExecutor> i = list.listIterator();
        while(i.hasNext()) {
            result.add(i.next());
        }
        return result.toArray();
    }
    
    private Object[] iterateBackward(int index) {
        List<ChainStepExecutor> result = new ArrayList<ChainStepExecutor>();
        ListIterator<ChainStepExecutor> i = list.listIterator(index);
        int currentIndex = index - 1; 
        while(i.hasPrevious()) {
            assertEquals(currentIndex, i.previousIndex());
            result.add(i.previous());
            currentIndex--;
        }
        return result.toArray();
    }
    
    private void clearLists() {
        list1.clear();
        list2.clear();
    }
}
