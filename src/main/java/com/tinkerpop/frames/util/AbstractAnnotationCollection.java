package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;

import java.util.AbstractCollection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class AbstractAnnotationCollection<T> extends AbstractCollection<T> {

    protected final FramedGraph<? extends Graph> framedGraph;
    protected final Vertex source;
    protected final String label;
    protected final Direction direction;
    protected final Class<T> kind;

    public AbstractAnnotationCollection(final FramedGraph framedGraph, final Vertex source, final String label, final Direction direction, final Class<T> kind) {
        this.framedGraph = framedGraph;
        this.source = source;
        this.label = label;
        this.direction = direction;
        this.kind = kind;
    }

    public int size() {
        int counter = 0;
        for (final Edge edge : this.source.getEdges(this.direction, this.label)) {
            counter++;
        }
        return counter;
    }
}
