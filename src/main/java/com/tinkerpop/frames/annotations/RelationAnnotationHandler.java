package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.util.ClassUtils;
import com.tinkerpop.frames.util.RelationCollection;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelationAnnotationHandler implements AnnotationHandler<Relation> {

    @Override
    public Class<Relation> getAnnotationType() {
        return Relation.class;
    }

    @Override
    public Object processVertex(Relation relation, Method method, Object[] arguments, FramesManager manager, Vertex element) {
        if (ClassUtils.isGetMethod(method)) {
            RelationCollection r = new RelationCollection(manager, (Vertex) element, relation.label(), relation.direction(), ClassUtils.getGenericClass(method));
            if (ClassUtils.returnsCollection(method)) {
                return r;
            } else {
                return r.iterator().hasNext() ? r.iterator().next() : null;
            }
        } else if (ClassUtils.isAddMethod(method)) {
            if (relation.direction().equals(Direction.OUT))
                manager.getGraph().addEdge(null, element, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), relation.label());
            else
                manager.getGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), (Vertex) element, relation.label());
            return null;
        } else if (ClassUtils.isRemoveMethod(method)) {
            removeEdges(relation.direction(), relation.label(), element, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), manager);
            return null;
        } else if (ClassUtils.isSetMethod(method)) {
            removeEdges(relation.direction(), relation.label(), element, null, manager);
            if (ClassUtils.acceptsCollection(method)) {
                for (Object o : (Collection) arguments[0]) {
                    Vertex v = (Vertex) ((FramedElement) Proxy.getInvocationHandler(o)).getElement();
                    if (relation.direction().equals(Direction.OUT)) {
                        manager.getGraph().addEdge(null, (Vertex) element, v, relation.label());
                    } else {
                        manager.getGraph().addEdge(null, v, (Vertex) element, relation.label());
                    }
                }
                return null;
            } else {
                if (null != arguments[0]) {
                    if (relation.direction().equals(Direction.OUT)) {
                        manager.getGraph().addEdge(null, (Vertex) element, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), relation.label());
                    } else {
                        manager.getGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), (Vertex) element, relation.label());
                    }
                }
                return null;
            }
        }

        return null;
    }

    @Override
    public Object processEdge(Relation annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
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
