package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

import java.lang.reflect.Method;

/**
 * Allows new framed vertices and edges to be initialized before they are returned to the user. This can be used for defaulting of properties.
 *
 * @author Bryn Cooke
 */
public interface FrameEventListener {

    /**
     * @param kind          The kind of frame, might be null.
     * @param framedGraph   The graph
     * @param outVertex     outVertex of the new edge
     * @param inVertex      inVertex of the new edge
     */
    public void preCreateEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final String label, final Vertex outVertex, final Vertex inVertex);
    /**
     * @param kind          The kind of frame, might be null.
     * @param framedGraph   The graph
     * @param edge          The edge being inserted
     */
    public void postCreateEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final Edge edge);
    /**
     * @param kind          The kind of frame, might be null.
     * @param framedGraph   The graph
     * @param vertex        The vertex being inserted
     */
    public void postCreateVertex(final Class<?> kind, final FramedGraph<?> framedGraph, final Vertex vertex);

    /**
     * @param framedGraph   The graph
     * @param element       The Elements whom property is being updated
     * @param method        The Method invoked
     * @param fieldName     The property name in the graph
     * @param newValue      The new value
     */
    public void preUpdateProperty(final FramedGraph<?> framedGraph, final Element element, final Method method, final Object fieldName, final Object newValue );

    /**
     * @param framedGraph   The graph
     * @param element       The Elements whom property is being updated
     * @param method        The Method invoked
     * @param fieldName     The property name in the graph
     */
    public void preDeleteProperty(final FramedGraph<?> framedGraph, final Element element,final Method method, final Object fieldName);

    /**
     * @param kind          The kind of frame, might be null.
     * @param framedGraph   The graph
     * @param vertexFrame   The vertex being deleted
     */
    public void preDeleteVertex(final Class<?> kind, final FramedGraph<?> framedGraph, final Vertex vertexFrame);
    /**
     * @param kind          The kind of frame, might be null.
     * @param framedGraph   The graph
     * @param edge          The edge being deleted
     */
    public void preDeleteEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final Edge edge);
}
