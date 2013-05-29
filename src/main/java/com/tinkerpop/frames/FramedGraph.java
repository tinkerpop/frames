package com.tinkerpop.frames;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.StringFactory;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;
import com.tinkerpop.frames.annotations.AdjacencyAnnotationHandler;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.annotations.DomainAnnotationHandler;
import com.tinkerpop.frames.annotations.IncidenceAnnotationHandler;
import com.tinkerpop.frames.annotations.PropertyAnnotationHandler;
import com.tinkerpop.frames.annotations.RangeAnnotationHandler;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovyAnnotationHandler;
import com.tinkerpop.frames.structures.FramedEdgeIterable;
import com.tinkerpop.frames.structures.FramedVertexIterable;

/**
 * The primary class for interpreting/framing elements of a graph in terms of particulate annotated interfaces.
 * This is a wrapper graph in that it requires an underlying graph from which to add functionality.
 * The standard Blueprints graph methods are exposed along with extra methods to make framing easy.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedGraph<T extends Graph> implements Graph, WrapperGraph<T> {

    protected final T baseGraph;
    private final Map<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>> annotationHandlers;
    private List<FrameInitializer> frameInitializers = new ArrayList<FrameInitializer>();

    /**
     * Construct a FramedGraph that will frame the elements of the underlying graph.
     *
     * @param baseGraph the graph whose elements to frame
     */
    public FramedGraph(final T baseGraph) {
        this.baseGraph = baseGraph;
        this.annotationHandlers = new HashMap<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>>();

        registerAnnotationHandler(new PropertyAnnotationHandler());
        registerAnnotationHandler(new AdjacencyAnnotationHandler());
        registerAnnotationHandler(new IncidenceAnnotationHandler());
        registerAnnotationHandler(new DomainAnnotationHandler());
        registerAnnotationHandler(new RangeAnnotationHandler());
        registerAnnotationHandler(new GremlinGroovyAnnotationHandler());
    }

    /**
     * A helper method for framing a vertex.
     *
     * @param vertex the vertex to frame
     * @param kind   the annotated interface to frame the vertex as
     * @param <F>    the type of the annotated interface
     * @return a proxy objects backed by a vertex and interpreted from the perspective of the annotate interface
     */
    public <F> F frame(final Vertex vertex, final Class<F> kind) {
        return (F) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind, VertexFrame.class}, new FramedElement(this, vertex));
    }

    /**
     * A helper method for framing an edge.
     *
     * @param edge      the edge to frame
     * @param direction the direction of the edges
     * @param kind      the annotated interface to frame the edges as
     * @param <F>       the type of the annotated interface
     * @return an iterable of proxy objects backed by an edge and interpreted from the perspective of the annotate interface
     */
    public <F> F frame(final Edge edge, final Class<F> kind) {
        return (F) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind, EdgeFrame.class}, new FramedElement(this, edge));
    }

    /**
     * A helper method for framing an iterable of vertices.
     *
     * @param vertices the vertices to frame
     * @param kind     the annotated interface to frame the vertices as
     * @param <F>      the type of the annotated interface
     * @return an iterable of proxy objects backed by a vertex and interpreted from the perspective of the annotate interface
     */
    public <F> Iterable<F> frameVertices(final Iterable<Vertex> vertices, final Class<F> kind) {
        return new FramedVertexIterable<F>(this, vertices, kind);
    }

    /**
     * A helper method for framing an iterable of edges.
     *
     * @param edges     the edges to frame
     * @param direction the direction of the edges
     * @param kind      the annotated interface to frame the edges as
     * @param <F>       the type of the annotated interface
     * @return an iterable of proxy objects backed by an edge and interpreted from the perspective of the annotate interface
     */
    public <F> Iterable<F> frameEdges(final Iterable<Edge> edges, final Class<F> kind) {
        return new FramedEdgeIterable<F>(this, edges, kind);
    }

    public Vertex getVertex(final Object id) {
        return this.baseGraph.getVertex(id);
    }

    /**
     * Frame a vertex according to a particular kind of annotated interface.
     *
     * @param id   the id of the vertex
     * @param kind the annotated interface to frame the vertex as
     * @param <F>  the type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <F> F getVertex(final Object id, final Class<F> kind) {
        return this.frame(this.baseGraph.getVertex(id), kind);
    }

    public Vertex addVertex(final Object id) {
        return this.baseGraph.addVertex(id);
    }

    /**
     * Add a vertex to the underlying graph and return it as a framed vertex.
     *
     * @param id   the id of the newly created vertex
     * @param kind the annotated interface to frame the vertex as
     * @param <F>  the type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <F> F addVertex(final Object id, final Class<F> kind) {
        Vertex vertex = this.baseGraph.addVertex(id);
        for (FrameInitializer initializer : frameInitializers) {
            initializer.initElement(kind, this, vertex);
        }
        return this.frame(vertex, kind);
    }

    public Edge getEdge(final Object id) {
        return this.baseGraph.getEdge(id);
    }

    /**
     * Frame an edge according to a particular kind of annotated interface.
     *
     * @param id        the id of the edge
     * @param direction the direction of the edge
     * @param kind      the annotated interface to frame the edge as
     * @param <F>       the type of the annotated interface
     * @return a proxy object backed by the edge and interpreted from the perspective of the annotate interface
     */
    public <F> F getEdge(final Object id, final Class<F> kind) {
        return this.frame(this.baseGraph.getEdge(id), kind);
    }

    public Edge addEdge(final Object id, final Vertex outVertex, final Vertex inVertex, final String label) {
        return this.baseGraph.addEdge(id, outVertex, inVertex, label);
    }

    /**
     * Add an edge to the underlying graph and return it as a framed edge.
     *
     * @param id        the id of the newly created edge
     * @param outVertex the outgoing vertex
     * @param inVertex  the incoming vertex
     * @param label     the label of the edge
     * @param direction the direction of the edge
     * @param kind      the annotated interface to frame the edge as
     * @param <F>       the type of the annotated interface
     * @return a proxy object backed by the edge and interpreted from the perspective of the annotate interface
     */
    public <F> F addEdge(final Object id, final Vertex outVertex, final Vertex inVertex, final String label, final Class<F> kind) {
        Edge edge = this.baseGraph.addEdge(id, outVertex, inVertex, label);
        for (FrameInitializer initializer : frameInitializers) {
            initializer.initElement(kind, this, edge);
        }
        return this.frame(edge, kind);
    }

    public void removeVertex(final Vertex vertex) {
        this.baseGraph.removeVertex(vertex);
    }

    public void removeEdge(final Edge edge) {
        this.baseGraph.removeEdge(edge);
    }

    public Iterable<Vertex> getVertices() {
        return this.baseGraph.getVertices();
    }

    public Iterable<Vertex> getVertices(final String key, final Object value) {
        return this.baseGraph.getVertices(key, value);
    }

    /**
     * Frame vertices according to a particular kind of annotated interface.
     *
     * @param key   the key of the vertices to get
     * @param value the value of the vertices to get
     * @param kind  the annotated interface to frame the vertices as
     * @param <F>   the type of the annotated interface
     * @return an iterable of proxy objects backed by the vertices and interpreted from the perspective of the annotate interface
     */
    public <F> Iterable<F> getVertices(final String key, final Object value, final Class<F> kind) {
        return new FramedVertexIterable<F>(this, this.baseGraph.getVertices(key, value), kind);
    }

    public Iterable<Edge> getEdges() {
        return this.baseGraph.getEdges();
    }

    public Iterable<Edge> getEdges(final String key, final Object value) {
        return this.baseGraph.getEdges(key, value);
    }

    /**
     * Frame edges according to a particular kind of annotated interface.
     *
     * @param key       the key of the edges to get
     * @param value     the value of the edges to get
     * @param direction the direction of the edges
     * @param kind      the annotated interface to frame the edges as
     * @param <F>       the type of the annotated interface
     * @return an iterable of proxy objects backed by the edges and interpreted from the perspective of the annotate interface
     */
    public <F> Iterable<F> getEdges(final String key, final Object value, final Class<F> kind) {
        return new FramedEdgeIterable<F>(this, this.baseGraph.getEdges(key, value), kind);
    }

    public Features getFeatures() {
        Features features = this.baseGraph.getFeatures().copyFeatures();
        features.isWrapper = true;
        return features;
    }

    public void shutdown() {
        this.baseGraph.shutdown();
    }

    public T getBaseGraph() {
        return this.baseGraph;
    }

    public String toString() {
        return StringFactory.graphString(this, this.baseGraph.toString());
    }

    public GraphQuery query() {
        return this.baseGraph.query();
    }

    /**
     * The method used to register a new annotation handler
     * for every new annotation a new annotation handler has to be registered in the framed graph
     *
     * @param handler the annotation handler
     */
    public void registerAnnotationHandler(final AnnotationHandler<? extends Annotation> handler) {
        this.annotationHandlers.put(handler.getAnnotationType(), handler);
    }

    /**
     * @param annotationType the type of annotation handled by the annotation handler
     * @return the annotation handler associated with the specified type
     */
    public AnnotationHandler getAnnotationHandler(final Class<? extends Annotation> annotationType) {
        return this.annotationHandlers.get(annotationType);
    }

    /**
     * @param annotationType the type of annotation handled by the annotation handler
     * @return a boolean indicating if the framedGraph has registered an annotation handler for the specified type
     */
    public boolean hasAnnotationHandler(final Class<? extends Annotation> annotationType) {
        return this.annotationHandlers.containsKey(annotationType);
    }

    /**
     * @param annotationType the type of the annotation handler to remove
     */
    public void unregisterAnnotationHandler(final Class<? extends Annotation> annotationType) {
        this.annotationHandlers.remove(annotationType);
    }

    public Collection<AnnotationHandler<? extends Annotation>> getAnnotationHandlers() {
        return this.annotationHandlers.values();
    }


    /**
     * Register a <code>FrameInitializer</code> that will be called whenever a new vertex or edge is added to the graph.
     * The initializer may mutate the vertex (or graph) before returning the framed element to the user.
     *
     * @param frameInitializer the frame initializer
     */
    public void registerFrameInitializer(FrameInitializer frameInitializer) {
        frameInitializers.add(frameInitializer);
    }

}
