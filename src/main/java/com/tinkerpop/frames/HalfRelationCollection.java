package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Vertex;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class HalfRelationCollection<T> extends AbstractRelationCollection<T> {


    public HalfRelationCollection(final FramesManager manager, final Vertex source, final String label, final Direction direction, final Class<T> kind) {
        super(manager, source, label, direction, kind);
    }

    public Iterator<T> iterator() {
        return new HalfRelationIterator<T>(this);
    }
}
