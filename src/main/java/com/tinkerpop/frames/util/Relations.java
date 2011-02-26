package com.tinkerpop.frames.util;

import com.tinkerpop.frames.FrameManager;
import com.tinkerpop.frames.Vertex;

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Relations<T> extends AbstractCollection<T> {
    final FrameManager manager;

    final com.tinkerpop.blueprints.pgm.Vertex vertex;
    final Direction direction;
    final String label;
    final Class<T> clazz;
    final Field vertexField;


    public Relations(final FrameManager manager, final com.tinkerpop.blueprints.pgm.Vertex vertex, final String label, final Direction direction, final Class<T> clazz) {
        this.vertex = vertex;
        this.label = label;
        this.clazz = clazz;
        this.manager = manager;
        this.direction = direction;
        this.vertexField = FrameManager.getAnnotatedField(clazz, Vertex.class);
        this.vertexField.setAccessible(true);
    }

    public boolean add(T t) {
        try {
            this.manager.getGraph().addEdge(null, this.vertex, (com.tinkerpop.blueprints.pgm.Vertex) this.vertexField.get(t), this.label);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return true;
    }

    public int size() {
        int counter = 0;
        for (T t : this) {
            counter++;
        }
        return counter;
    }

    public Iterator<T> iterator() {
        if (this.direction == Direction.STANDARD)
            return new RelationsIterator<T>(this.manager, this.vertex.getOutEdges(this.label).iterator(), this.direction, this.clazz);
        else
            return new RelationsIterator<T>(this.manager, this.vertex.getInEdges(this.label).iterator(), this.direction, this.clazz);
    }
}
