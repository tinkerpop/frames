package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.util.AdjacencyCollection;
import com.tinkerpop.frames.util.ClassUtilities;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

public class AdjacencyAnnotationHandler implements AnnotationHandler<Adjacency> {

    @Override
    public Class<Adjacency> getAnnotationType() {
        return Adjacency.class;
    }

    @Override
    public Object processVertex(final Adjacency adjacency, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        if (ClassUtilities.isGetMethod(method)) {
            final AdjacencyCollection r = new AdjacencyCollection(framedGraph, vertex, adjacency.label(), adjacency.direction(), ClassUtilities.getGenericClass(method));
            if (ClassUtilities.returnsCollection(method)) {
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
            if (ClassUtilities.acceptsCollection(method)) {
                for (Object o : (Collection) arguments[0]) {
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

    @Override
    public Object processEdge(final Adjacency annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge element, final Direction direction) {
        throw new UnsupportedOperationException();
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
