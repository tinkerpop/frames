package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.pgm.EdgeVertexPipe;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FullRelationIterator<T> implements Iterator<T> {

    private FullRelationCollection collection;
    private Iterator<Vertex> itty;

    public FullRelationIterator(final FullRelationCollection collection) {
        this.collection = collection;
        if (this.collection.getDirection().equals(Relation.Direction.STANDARD)) {
            Pipe<Edge, Vertex> pipe = new EdgeVertexPipe(EdgeVertexPipe.Step.IN_VERTEX);
            pipe.setStarts(this.collection.getSource().getOutEdges(collection.getLabel()));
            this.itty = pipe.iterator();
        } else {
            Pipe<Edge, Vertex> pipe = new EdgeVertexPipe(EdgeVertexPipe.Step.OUT_VERTEX);
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
