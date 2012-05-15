package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.annotations.IncidentAnnotationHandler;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.annotations.DomainAnnotationHandler;
import com.tinkerpop.frames.annotations.PropertyAnnotationHandler;
import com.tinkerpop.frames.annotations.RangeAnnotationHandler;
import com.tinkerpop.frames.annotations.AdjacencyAnnotationHandler;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovyAnnotationHandler;
import com.tinkerpop.frames.util.FramingEdgeIterable;
import com.tinkerpop.frames.util.FramingVertexIterable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * The primary class for interpreting/framing elements of a graph in terms of particulate annotated interfaces.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramesManager {

    private final Graph graph;
    private final Map<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>> annotationHandlers;

    /**
     * Construct a FramesManager that will frame elements of the provided graph.
     *
     * @param graph the graph whose elements to frame
     */
    public FramesManager(final Graph graph) {
        this.graph = graph;
        this.annotationHandlers = new HashMap<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>>();

        registerAnnotationHandler(new PropertyAnnotationHandler());
        registerAnnotationHandler(new AdjacencyAnnotationHandler());
        registerAnnotationHandler(new IncidentAnnotationHandler());
        registerAnnotationHandler(new DomainAnnotationHandler());
        registerAnnotationHandler(new RangeAnnotationHandler());
        registerAnnotationHandler(new GremlinGroovyAnnotationHandler());
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
        return (T) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind}, new FramedElement(this, vertex));
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
        return (T) Proxy.newProxyInstance(kind.getClassLoader(), new Class[]{kind}, new FramedElement(this, edge, direction));
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
     * Create a vertex in the underlying graph and frame it according to provided annotated interface.
     *
     * @param kind the annotated interface to frame the created vertex as
     * @param <T>  the type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <T> T createFramedVertex(final Class<T> kind) {
        return this.frame(graph.addVertex(null), kind);
    }

    /**
     * Create a vertex (with provided id) in the underlying graph and frame it according to provided annotated interface.
     *
     * @param id   the id to use when creating the underlying vertex
     * @param kind the annotated interface to frame the created vertex as
     * @param <T>  the type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <T> T createFramedVertex(final Object id, final Class<T> kind) {
        return this.frame(graph.addVertex(id), kind);
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
     * @param vertices an iterable of vertices to frame as T
     * @param kind     the annotated interface to frame the vertices as
     * @param <T>      the type of the annotated interface
     * @return an iterable of proxy objects backed by the vertices and interpreted from the perspective of the annotate interface
     */
    public <T> Iterable<T> frameVertices(Iterable<Vertex> vertices, final Class<T> kind) {
        return new FramingVertexIterable<T>(this, vertices, kind);
    }

    /**
     * Frame edges pulled from an index according to a particular kind of annotated interface.
     *
     * @param edges     an iterable of edges to frame as T
     * @param direction the direction of the edges
     * @param kind      the annotated interface to frame the edges as
     * @param <T>       the type of the annotated interface
     * @return an iterable of proxy objects backed by the edges and interpreted from the perspective of the annotate interface
     */
    public <T> Iterable<T> frameEdges(Iterable<Edge> edges, final Direction direction, final Class<T> kind) {
        return new FramingEdgeIterable<T>(this, edges, direction, kind);
    }

    /**
     * The method used to register a new annotation handler
     * for every new annotation a new annotation handler has to be registered in the manager
     *
     * @param handler the annotation handler
     */
    public void registerAnnotationHandler(AnnotationHandler<? extends Annotation> handler) {
        annotationHandlers.put(handler.getAnnotationType(), handler);
    }

    /**
     * @param annotationType the type of annotation handled by the annotation handler
     * @return the annotation handler associated with the specified type
     */
    public AnnotationHandler getAnnotationHandler(Class<? extends Annotation> annotationType) {
        return annotationHandlers.get(annotationType);
    }

    /**
     * @param annotationType the type of annotation handled by the annotation handler
     * @return a boolean indicating if the manager has registered an annotation handler for the specified type
     */
    public boolean hasAnnotationHandler(Class<? extends Annotation> annotationType) {
        return annotationHandlers.containsKey(annotationType);
    }
}
