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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
class UnionList implements List<ChainStepExecutor>, Serializable {
    
    private final List<ChainStepExecutor> list1;
    private final List<ChainStepExecutor> list2;

    public UnionList(List<ChainStepExecutor> list1, List<ChainStepExecutor> list2) {
        if (list1 == null || list2 == null) {
            throw new IllegalArgumentException("The lists cannot be null.");
        }
        
        this.list1 = list1;
        this.list2 = list2;
        
    }

    public int size() {
        return list1.size() + list2.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Object o) {
        boolean result = list1.contains(o);
        if (!result) {
            result = list2.contains(o);
        }
        return result;
    }

    public Iterator<ChainStepExecutor> iterator() {
        return listIterator();
    }

    public Object[] toArray() {
        return copyToArray(new ChainStepExecutor[size()]);
    }

    public <T> T[] toArray(T[] array) {
        ChainStepExecutor[] result = getArrayToExport(array);
        return (T[]) copyToArray(result);

    }

    public boolean add(ChainStepExecutor e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsAll(Collection<?> collection) {
        for(Object item : collection) {
            if (!contains((ChainStepExecutor)item)) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(Collection<? extends ChainStepExecutor> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(int i, Collection<? extends ChainStepExecutor> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ChainStepExecutor get(int i) {
        if (i < list1.size()) {
            return list1.get(i);
        } else {
            return list2.get(i - list1.size());
        }
    }

    public ChainStepExecutor set(int i, ChainStepExecutor e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void add(int i, ChainStepExecutor e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ChainStepExecutor remove(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int indexOf(Object o) {
        int result = list1.indexOf(o);
        if (result >= 0) {
            return result;
        }
        
        result = list1.size() + list2.indexOf(o);
        if (result >= list1.size()) {
            return result;
        }
        
        return -1;
    }

    public int lastIndexOf(Object o) {
        int result = list2.lastIndexOf(o) + list1.size();
        if (result >= list1.size()) {
            return result;
        }
        
        result = list1.lastIndexOf(o);
        if (result >= 0) {
            return result;
        }
        
        return -1;
    }

    public ListIterator<ChainStepExecutor> listIterator() {
        return new UnionIterator<ChainStepExecutor>(this, 0);
    }

    public ListIterator<ChainStepExecutor> listIterator(int index) {
        return new UnionIterator<ChainStepExecutor>(this, index);
    }

    public List<ChainStepExecutor> subList(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private <T> ChainStepExecutor[] getArrayToExport(T[] array) {
        ChainStepExecutor[] result = (ChainStepExecutor[]) array;
        if (array.length < size()) {
            result = (ChainStepExecutor[]) Array.newInstance(array.getClass().getComponentType(), size());
        }
        return result;
    }
    
    private ChainStepExecutor[] copyToArray(ChainStepExecutor[] array) {
        int i = 0;
        for(ChainStepExecutor item : list1) {
            array[i] = item;
            i++;
        }
        for(ChainStepExecutor item : list2) {
            array[i] = item;
            i++;
        }
        for(;i<array.length;i++) {
            array[i] = null;
        }
            
        return array;        
    }
    
    private static class UnionIterator<ChainStepExecutor> implements ListIterator<ChainStepExecutor> {

        private List<ChainStepExecutor> list;
        private int index;

        public UnionIterator(List<ChainStepExecutor> list, int index) {
            this.list = list;
            this.index = index;
        }
        
        public boolean hasNext() {
            return (index < list.size());
        }

        public ChainStepExecutor next() {
            return list.get(index++);
        }

        public boolean hasPrevious() {
            return index > 0;
        }

        public ChainStepExecutor previous() {
            return list.get(--index);
        }

        public int nextIndex() {
            return index;
        }

        public int previousIndex() {
            return index - 1;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void set(ChainStepExecutor e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void add(ChainStepExecutor e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
}
