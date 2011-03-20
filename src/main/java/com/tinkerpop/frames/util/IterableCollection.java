package com.tinkerpop.frames.util;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class IterableCollection<T> extends AbstractCollection<T> {

    private final Iterable<T> iterable;

    public IterableCollection(final Iterable<T> iterable) {
        this.iterable = iterable;
    }

    public int size() {
        int counter = 0;
        for (final T t : iterable) {
            counter++;
        }
        return counter;
    }

    public Iterator<T> iterator() {
        return this.iterable.iterator();
    }


}
