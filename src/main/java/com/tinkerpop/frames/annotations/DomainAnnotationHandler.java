package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.FramedGraph;

import java.lang.reflect.Method;

public class DomainAnnotationHandler implements AnnotationHandler<Domain> {

    @Override
    public Class<Domain> getAnnotationType() {
        return Domain.class;
    }

    @Override
    public Object processVertex(final Domain annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object processEdge(final Domain annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge edge, final Direction direction) {
        return framedGraph.frame(edge.getVertex(direction), method.getReturnType());
    }

}
