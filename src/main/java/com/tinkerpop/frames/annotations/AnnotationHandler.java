package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AnnotationHandler<T extends Annotation> {
    public Class<T> getAnnotationType();

    public Object processElement(final T annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element);
}
