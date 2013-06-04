package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.FramedGraph;

import java.lang.reflect.Method;

public class DomainAnnotationHandler implements AnnotationHandler<Domain> {

    @Override
    public Class<Domain> getAnnotationType() {
        return Domain.class;
    }

    @Override
    public Object processElement(final Domain annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element) {
        if (element instanceof Edge) {
            return processEdge(annotation, method, arguments, framedGraph, (Edge) element);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Object processEdge(final Domain annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge edge) {
        return framedGraph.frame(edge.getVertex(Direction.OUT), method.getReturnType());
    }

}
