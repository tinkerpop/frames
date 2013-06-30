package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Link;

import java.lang.reflect.Method;

public class LinkAnnotationHandler implements AnnotationHandler<Link> {
    @Override
    public Class<Link> getAnnotationType() {
        return Link.class;
    }

    @Override
    public Object processElement(final Link annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (element instanceof Edge) {
        	return framedGraph.frame(((Edge)element).getVertex(annotation.value()), method.getReturnType());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
