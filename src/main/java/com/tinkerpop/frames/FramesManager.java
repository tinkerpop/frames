package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;

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

    public <T> T frame(final Vertex vertex, final Class<T> clazz) {
        final FramedElement handler = new FramedVertex(this, vertex);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }

    public <T> T frame(final Edge edge, final Class<T> clazz, final Relation.Direction direction) {
        final FramedElement handler = new FramedEdge(this, edge, direction);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }
}
