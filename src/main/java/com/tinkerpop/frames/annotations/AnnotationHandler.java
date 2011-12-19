package com.tinkerpop.frames.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramesManager;

public interface AnnotationHandler<T extends Annotation> {
    public Class<T> getAnnotationType();

    public Object processVertex(final T annotation, final Method method, final Object[] arguments, final FramesManager manager, final Vertex element);

    public Object processEdge(final T annotation, final Method method, final Object[] arguments, final FramesManager manager, final Edge element, final Direction direction);
}
