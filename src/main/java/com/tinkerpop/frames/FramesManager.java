package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;

import java.lang.reflect.Proxy;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramesManager {

    private final Graph graph;

    public FramesManager(final Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public <T> T frame(final Element element, final Class<T> clazz) {
        final FramedElement handler = new FramedElement(this, element);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }

    public <T> T frame(final Element element, final Class<T> clazz, final Direction direction) {
        final FramedElement handler = new FramedElement(this, element, direction);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }
}
