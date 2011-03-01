package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.Relation;

import java.util.AbstractCollection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class AbstractRelationCollection<T> extends AbstractCollection<T> {

    protected final FramesManager manager;
    protected final Vertex source;
    protected final String label;
    protected final Relation.Direction direction;
    protected final Class<T> kind;

    public AbstractRelationCollection(final FramesManager manager, final Vertex source, final String label, final Relation.Direction direction, final Class<T> kind) {
        this.manager = manager;
        this.source = source;
        this.label = label;
        this.direction = direction;
        this.kind = kind;
    }

    public int size() {
        int counter = 0;
        final Iterable<Edge> iterable;
        if (direction.equals(Relation.Direction.STANDARD))
            iterable = this.source.getOutEdges(this.label);
        else
            iterable = this.source.getInEdges(this.label);

        for (Edge edge : iterable) {
            counter++;
        }
        return counter;
    }

    protected Vertex getSource() {
        return this.source;
    }

    protected String getLabel() {
        return this.label;
    }

    protected Relation.Direction getDirection() {
        return this.direction;
    }

    protected Class<T> getKind() {
        return this.kind;
    }

    protected FramesManager getManager() {
        return this.manager;
    }
}
