package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramesManager;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RelationCollection<T> extends AbstractRelationCollection<T> {


    public RelationCollection(final FramesManager manager, final Vertex source, final String label, final Direction direction, final Class<T> kind) {
        super(manager, source, label, direction, kind);
    }

    public Iterator<T> iterator() {
        return new RelationIterator<T>(this);
    }
}
