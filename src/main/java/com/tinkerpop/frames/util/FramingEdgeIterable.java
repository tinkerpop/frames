package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramesManager;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramingEdgeIterable<T> implements Iterable<T> {
    protected final Class<T> kind;
    protected final Direction direction;
    protected final Iterable<Edge> iterable;
    protected final FramesManager manager;

    public FramingEdgeIterable(final FramesManager manager, final Class<T> kind, final Direction direction, Iterable<Edge> iterable) {
        this.kind = kind;
        this.iterable = iterable;
        this.manager = manager;
        this.direction = direction;
    }

    public Iterator<T> iterator() {
        return new FramingEdgeIterator<T>(this, this.iterable.iterator());
    }
}
