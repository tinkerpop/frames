package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
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
        if (this.collection.getDirection().equals(Direction.STANDARD)) {
            Pipe<Edge, Vertex> pipe = new InVertexPipe();
            String label = collection.getLabel();
            Vertex source = this.collection.getSource();
            Iterable<Edge> starts = source.getOutEdges(label);
            pipe.setStarts(starts);
            this.itty = pipe.iterator();
        } else {
            Pipe<Edge, Vertex> pipe = new OutVertexPipe();
            pipe.setStarts(this.collection.getSource().getInEdges(collection.getLabel()));
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
