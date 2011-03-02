package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.frames.Direction;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class AdjacencyIterator<T> implements Iterator<T> {

    private AdjacencyCollection collection;
    private Iterator<Edge> itty;

    public AdjacencyIterator(final AdjacencyCollection collection) {
        this.collection = collection;
        if (this.collection.getDirection().equals(Direction.STANDARD))
            this.itty = collection.getSource().getOutEdges(collection.getLabel()).iterator();
        else
            this.itty = collection.getSource().getInEdges(collection.getLabel()).iterator();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public T next() {
        return (T) this.collection.getManager().frame(this.itty.next(), this.collection.getKind(), this.collection.getDirection());
    }

    public boolean hasNext() {
        return this.itty.hasNext();
    }
}
