package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedVertexIterable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AdjacencyAnnotationHandler implements AnnotationHandler<Adjacency> {

    @Override
    public Class<Adjacency> getAnnotationType() {
        return Adjacency.class;
    }

    @Override
    public Object processElement(final Adjacency annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (element instanceof Vertex) {
            return processVertex(annotation, method, arguments, framedGraph, (Vertex) element);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Object processVertex(final Adjacency adjacency, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        if (ClassUtilities.isGetMethod(method)) {
            final FramedVertexIterable r = new FramedVertexIterable(framedGraph, vertex.getVertices(adjacency.direction(), adjacency.label()), ClassUtilities.getGenericClass(method));
            if (ClassUtilities.returnsIterable(method)) {
                return r;
            } else {
                return r.iterator().hasNext() ? r.iterator().next() : null;
            }
        } else if (ClassUtilities.isAddMethod(method)) {
            if (adjacency.direction().equals(Direction.OUT))
                framedGraph.getBaseGraph().addEdge(null, vertex, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), adjacency.label());
            else
                framedGraph.getBaseGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), vertex, adjacency.label());
            return null;
        } else if (ClassUtilities.isRemoveMethod(method)) {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), framedGraph);
            return null;
        } else if (ClassUtilities.isSetMethod(method)) {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, null, framedGraph);
            if (ClassUtilities.acceptsIterable(method)) {
                for (Object o : (Iterable) arguments[0]) {
                    Vertex v = (Vertex) ((FramedElement) Proxy.getInvocationHandler(o)).getElement();
                    if (adjacency.direction().equals(Direction.OUT)) {
                        framedGraph.getBaseGraph().addEdge(null, vertex, v, adjacency.label());
                    } else {
                        framedGraph.getBaseGraph().addEdge(null, v, vertex, adjacency.label());
                    }
                }
                return null;
            } else {
                if (null != arguments[0]) {
                    if (adjacency.direction().equals(Direction.OUT)) {
                        framedGraph.getBaseGraph().addEdge(null, vertex, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), adjacency.label());
                    } else {
                        framedGraph.getBaseGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), vertex, adjacency.label());
                    }
                }
                return null;
            }
        }

        return null;
    }

    private void removeEdges(final Direction direction, final String label, final Vertex element, final Vertex otherVertex, final FramedGraph framedGraph) {
        final Graph graph = framedGraph.getBaseGraph();
        for (final Edge edge : element.getEdges(direction, label)) {
            if (null == otherVertex || edge.getVertex(direction.opposite()).equals(otherVertex)) {
                graph.removeEdge(edge);
            }
        }
    }
}
