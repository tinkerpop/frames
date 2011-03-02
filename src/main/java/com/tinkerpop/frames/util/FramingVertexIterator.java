package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Vertex;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramingVertexIterator<T> implements Iterator<T> {

    private Iterator<Vertex> iterator;
    private FramingVertexIterable framing;

    public FramingVertexIterator(final FramingVertexIterable<T> framing, final Iterator<Vertex> iterator) {
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
        return (T) this.framing.manager.frame(this.iterator.next(), this.framing.kind);
    }
}