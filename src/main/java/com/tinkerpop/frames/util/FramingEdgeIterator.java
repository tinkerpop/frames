package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramingEdgeIterator<T> implements Iterator<T> {

    private Iterator<Edge> iterator;
    private FramingEdgeIterable framing;

    public FramingEdgeIterator(FramingEdgeIterable<T> framing, Iterator<Edge> iterator) {
        this.iterator = iterator;
        this.framing = framing;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public T next() {
        return (T) this.framing.manager.frame(this.iterator.next(), this.framing.kind, framing.direction);
    }
}