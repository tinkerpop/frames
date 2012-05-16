package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramesManager;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class IncidenceCollection<T> extends AbstractAnnotationCollection<T> {

    public IncidenceCollection(final FramesManager manager, final Vertex source, final String label, final Direction direction, final Class<T> kind) {
        super(manager, source, label, direction, kind);
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<Edge> itty = source.getEdges(direction, label).iterator();

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public T next() {
                return manager.frame(this.itty.next(), direction, kind);
            }

            public boolean hasNext() {
                return this.itty.hasNext();
            }
        };
    }
}
