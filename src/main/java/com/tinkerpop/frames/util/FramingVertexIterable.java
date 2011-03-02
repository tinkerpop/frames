package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.FramesManager;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramingVertexIterable<T> implements Iterable<T> {
    protected final Class<T> kind;
    protected final Iterable<Vertex> iterable;
    protected final FramesManager manager;

    public FramingVertexIterable(final FramesManager manager, final Class<T> kind, Iterable<Vertex> iterable) {
        this.kind = kind;
        this.iterable = iterable;
        this.manager = manager;
    }

    public Iterator<T> iterator() {
        return new FramingVertexIterator<T>(this, this.iterable.iterator());
    }
}
