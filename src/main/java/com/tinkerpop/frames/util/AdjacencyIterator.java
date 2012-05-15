package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class AdjacencyIterator<T> implements Iterator<T> {

    private AdjacencyCollection collection;
    private Iterator<Edge> itty;

    public AdjacencyIterator(final AdjacencyCollection collection) {
        this.collection = collection;
        if (this.collection.getDirection().equals(Direction.OUT))
            this.itty = collection.getSource().getEdges(Direction.OUT, collection.getLabel()).iterator();
        else
            this.itty = collection.getSource().getEdges(Direction.IN, collection.getLabel()).iterator();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public T next() {
        return (T) this.collection.getManager().frame(this.itty.next(), this.collection.getDirection(), this.collection.getKind());
    }

    public boolean hasNext() {
        return this.itty.hasNext();
    }
}
