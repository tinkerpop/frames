package com.tinkerpop.frames.util;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.FrameManager;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Relations<T> implements Iterable<T> {

    final Iterable<? extends Element> elements;
    final Class<T> clazz;
    final FrameManager manager;

    public Relations(FrameManager manager, Iterable<? extends Element> elements, Class<T> clazz) {
        this.elements = elements;
        this.clazz = clazz;
        this.manager = manager;
    }

    public Iterator<T> iterator() {
        return new RelationsIterator<T>(this.manager, this.elements.iterator(), this.clazz);
    }

    private class RelationsIterator<T> implements Iterator<T> {
        final Iterator<? extends Element> elements;
        final Class<T> clazz;
        final FrameManager manager;

        public RelationsIterator(final FrameManager manager, final Iterator<? extends Element> elements, final Class<T> clazz) {
            this.elements = elements;
            this.clazz = clazz;
            this.manager = manager;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return this.elements.hasNext();
        }

        public T next() {
            try {
                return manager.load(clazz, (Vertex) this.elements.next());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
