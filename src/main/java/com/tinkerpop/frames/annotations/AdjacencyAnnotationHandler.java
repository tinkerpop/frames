package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.util.AdjacencyCollection;
import com.tinkerpop.frames.util.ClassUtilities;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdjacencyAnnotationHandler implements AnnotationHandler<Adjacency> {

    @Override
    public Class<Adjacency> getAnnotationType() {
        return Adjacency.class;
    }

    @Override
    public Object processVertex(final Adjacency adjacency, final Method method, final Object[] arguments, final FramesManager manager, final Vertex vertex) {
        if (ClassUtilities.isGetMethod(method)) {
            final AdjacencyCollection r = new AdjacencyCollection(manager, vertex, adjacency.label(), adjacency.direction(), ClassUtilities.getGenericClass(method));
            if (ClassUtilities.returnsCollection(method)) {
                return r;
            } else {
                return r.iterator().hasNext() ? r.iterator().next() : null;
            }
        } else if (ClassUtilities.isAddMethod(method)) {
            if (adjacency.direction().equals(Direction.OUT))
                manager.getGraph().addEdge(null, vertex, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), adjacency.label());
            else
                manager.getGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), vertex, adjacency.label());
            return null;
        } else if (ClassUtilities.isRemoveMethod(method)) {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), manager);
            return null;
        } else if (ClassUtilities.isSetMethod(method)) {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, null, manager);
            if (ClassUtilities.acceptsCollection(method)) {
                for (Object o : (Collection) arguments[0]) {
                    Vertex v = (Vertex) ((FramedElement) Proxy.getInvocationHandler(o)).getElement();
                    if (adjacency.direction().equals(Direction.OUT)) {
                        manager.getGraph().addEdge(null, vertex, v, adjacency.label());
                    } else {
                        manager.getGraph().addEdge(null, v, vertex, adjacency.label());
                    }
                }
                return null;
            } else {
                if (null != arguments[0]) {
                    if (adjacency.direction().equals(Direction.OUT)) {
                        manager.getGraph().addEdge(null, vertex, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), adjacency.label());
                    } else {
                        manager.getGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), vertex, adjacency.label());
                    }
                }
                return null;
            }
        }

        return null;
    }

    @Override
    public Object processEdge(final Adjacency annotation, final Method method, final Object[] arguments, final FramesManager manager, final Edge element, final Direction direction) {
        throw new UnsupportedOperationException();
    }

    private void removeEdges(final Direction direction, final String label, final Vertex element, final Vertex otherVertex, final FramesManager manager) {
        final Graph graph = manager.getGraph();
        List<Edge> toRemove = new ArrayList<Edge>();
        if (direction.equals(Direction.OUT)) {
            for (final Edge edge : element.getEdges(Direction.OUT, label)) {
                if (null == otherVertex || edge.getInVertex().equals(otherVertex)) {
                    toRemove.add(edge);
                }
            }
        } else {
            for (final Edge edge : element.getEdges(Direction.IN, label)) {
                if (null == otherVertex || edge.getOutVertex().equals(otherVertex)) {
                    toRemove.add(edge);
                }
            }
        }
        for (final Edge edge : toRemove) {
            graph.removeEdge(edge);
        }
    }
}
