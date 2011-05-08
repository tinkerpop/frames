package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.*;
import com.tinkerpop.frames.util.FramingEdgeIterable;
import com.tinkerpop.frames.util.FramingVertexIterable;

import java.lang.reflect.Proxy;

/**
 * The primary class for interpreting/framing elements of a graph in terms of particulate annotated interfaces.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramesManager {

    private final Graph graph;

    /**
     * Construct a FramesManager that will frame elements of the provided graph.
     *
     * @param graph the graph whose elements to frame
     */
    public FramesManager(final Graph graph) {
        this.graph = graph;
    }

    /**
     * Get the graph managed by this FrameManager.
     *
     * @return the graph managed by this FrameManager
     */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Frame a vertex according to a particular kind of annotated interface.
     *
     * @param vertex the vertex to frame
     * @param kind   the annotated interface to frame the vertex as
     * @param <T>    the type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <T> T frame(final Vertex vertex, final Class<T> kind) {
        return (T) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind}, new FramedVertex(this, vertex));
    }

    /**
     * Frame an edge according to a particular kind of annotated interface.
     *
     * @param edge      the edge to frame
     * @param direction the direction of the edge
     * @param kind      the annotated interface to frame the edge as
     * @param <T>       the type of the annotated interface
     * @return a proxy object backed by the edge and interpreted from the perspective of the annotate interface
     */
    public <T> T frame(final Edge edge, final Direction direction, final Class<T> kind) {
        return (T) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind}, new FramedEdge(this, edge, direction));
    }

    /**
     * Frame a vertex according to a particular kind of annotated interface.
     *
     * @param id   the id of the vertex
     * @param kind the annotated interface to frame the vertex as
     * @param <T>  the type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <T> T frameVertex(final Object id, final Class<T> kind) {
        return this.frame(this.graph.getVertex(id), kind);
    }

    /**
     * Frame an edge according to a particular kind of annotated interface.
     *
     * @param id        the id of the edge
     * @param direction the direction of the edge
     * @param kind      the annotated interface to frame the edge as
     * @param <T>       the type of the annotated interface
     * @return a proxy object backed by the edge and interpreted from the perspective of the annotate interface
     */
    public <T> T frameEdge(final Object id, final Direction direction, final Class<T> kind) {
        return this.frame(this.graph.getEdge(id), direction, kind);
    }

    /**
     * Frame vertices pulled from an index according to a particular kind of annotated interface.
     *
     * @param indexName the name of the index to pull the vertices from
     * @param key       the key that the vertices are indexed by
     * @param value     the value that the vertices are indexed by
     * @param kind      the annotated interface to frame the vertices as
     * @param <T>       the type of the annotated interface
     * @return an iterable of proxy objects backed by the vertices and interpreted from the perspective of the annotate interface
     */
    public <T> Iterable<T> frameVertices(final String indexName, final String key, final Object value, final Class<T> kind) {
        final Index<Vertex> index = ((IndexableGraph) this.graph).getIndex(indexName, Vertex.class);
        return new FramingVertexIterable<T>(this, index.get(key, value), kind);
    }

    /**
     * Frame edges pulled from an index according to a particular kind of annotated interface.
     *
     * @param indexName the name of the index to pull edges from
     * @param key       the key that the vertices are indexed by
     * @param value     the value that the vertices are indexed by
     * @param direction the direction of the edges
     * @param kind      the annotated interface to frame the edges as
     * @param <T>       the type of the annotated interface
     * @return an iterable of proxy objects backed by the edges and interpreted from the perspective of the annotate interface
     */
    public <T> Iterable<T> frameEdges(final String indexName, final String key, final Object value, final Direction direction, final Class<T> kind) {
        final Index<Edge> index = ((IndexableGraph) this.graph).getIndex(indexName, Edge.class);
        return new FramingEdgeIterable<T>(this, index.get(key, value), direction, kind);
    }


}
