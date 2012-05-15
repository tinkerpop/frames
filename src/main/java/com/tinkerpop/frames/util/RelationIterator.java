package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.pipes.transform.InVertexPipe;
import com.tinkerpop.gremlin.pipes.transform.OutVertexPipe;
import com.tinkerpop.pipes.Pipe;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RelationIterator<T> implements Iterator<T> {

    private final RelationCollection collection;
    private final Iterator<Vertex> itty;

    public RelationIterator(final RelationCollection collection) {
        this.collection = collection;
        if (this.collection.getDirection().equals(Direction.OUT)) {
            Pipe<Edge, Vertex> pipe = new InVertexPipe();
            String label = collection.getLabel();
            Vertex source = this.collection.getSource();
            Iterable<Edge> starts = source.getEdges(Direction.OUT, label);
            pipe.setStarts(starts);
            this.itty = pipe.iterator();
        } else {
            Pipe<Edge, Vertex> pipe = new OutVertexPipe();
            pipe.setStarts(this.collection.getSource().getEdges(Direction.IN, collection.getLabel()));
            this.itty = pipe.iterator();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public T next() {
        return (T) this.collection.getManager().frame(this.itty.next(), this.collection.getKind());
    }

    public boolean hasNext() {
        return this.itty.hasNext();
    }

}
