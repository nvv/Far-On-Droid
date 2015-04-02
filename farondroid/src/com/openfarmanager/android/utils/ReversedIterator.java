package com.openfarmanager.android.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Vlad Namashko.
 */
public class ReversedIterator<T> implements Iterable<T> {

    private List<T> list;

    public ReversedIterator(List<T> list){
        this.list = list;
    }

    @Override
    public Iterator<T> iterator() {
        final ListIterator<T> iterator = list.listIterator(list.size());

        return new Iterator<T>(){

            @Override
            public boolean hasNext(){
                return iterator.hasPrevious();
            }

            @Override
            public T next(){
                return iterator.previous();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

        };

    }
}
