package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramesManager;

import java.util.AbstractCollection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class AbstractAnnotationCollection<T> extends AbstractCollection<T> {

    protected final FramesManager manager;
    protected final Vertex source;
    protected final String label;
    protected final Direction direction;
    protected final Class<T> kind;

    public AbstractAnnotationCollection(final FramesManager manager, final Vertex source, final String label, final Direction direction, final Class<T> kind) {
        this.manager = manager;
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
