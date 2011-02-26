package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.frames.FrameManager;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RelationsIterator<T> implements Iterator<T> {
    final Iterator<Edge> edges;
    final Class<T> clazz;
    final FrameManager manager;
    final Direction direction;

    public RelationsIterator(final FrameManager manager, final Iterator<Edge> edges, final Direction direction, final Class<T> clazz) {
        this.edges = edges;
        this.clazz = clazz;
        this.manager = manager;
        this.direction = direction;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.edges.hasNext();
    }

    public T next() {
        try {
            if (this.direction == Direction.INVERSE)
                return manager.load(this.clazz, this.edges.next().getOutVertex());
            else
                return manager.load(this.clazz, this.edges.next().getInVertex());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
