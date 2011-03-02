package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.*;
import com.tinkerpop.frames.util.FramingEdgeIterable;
import com.tinkerpop.frames.util.FramingVertexIterable;

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

    public <T> T frame(final Vertex vertex, final Class<T> kind) {
        return (T) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind}, new FramedVertex(this, vertex));
    }

    public <T> T frame(final Edge edge, final Class<T> kind, final Direction direction) {
        return (T) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind}, new FramedEdge(this, edge, direction));
    }

    public <T> Iterable<T> frameVertices(final String indexName, final String key, final Object value, final Class<T> kind) {
        final Index<Vertex> index = ((IndexableGraph) this.graph).getIndex(indexName, Vertex.class);
        return new FramingVertexIterable<T>(this, index.get(key, value), kind);
    }

    public <T> Iterable<T> frameEdges(final String indexName, final String key, final Object value, final Class<T> kind, final Direction direction) {
        final Index<Edge> index = ((IndexableGraph) this.graph).getIndex(indexName, Edge.class);
        return new FramingEdgeIterable<T>(this, index.get(key, value), kind, direction);
    }


}
