package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class HalfRelationIterator<T> implements Iterator<T> {

    private HalfRelationCollection collection;
    private Iterator<Edge> itty;

    public HalfRelationIterator(HalfRelationCollection collection) {
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
        T t = (T) this.collection.getManager().frame(this.itty.next(), this.collection.getKind(), this.collection.getDirection());
        return t;
    }

    public boolean hasNext() {
        return this.itty.hasNext();
    }
}
