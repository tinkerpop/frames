package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AnnotationHandler<T extends Annotation> {
    public Class<T> getAnnotationType();

    public Object processVertex(final T annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex element);

    public Object processEdge(final T annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge element, final Direction direction);
}
