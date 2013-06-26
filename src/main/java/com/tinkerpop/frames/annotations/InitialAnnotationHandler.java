package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Initial;

import java.lang.reflect.Method;

public class InitialAnnotationHandler implements AnnotationHandler<Initial> {
    @Override
    public Class<Initial> getAnnotationType() {
        return Initial.class;
    }

    @Override
    public Object processElement(final Initial annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (element instanceof Edge) {
            return framedGraph.frame(((Edge)element).getVertex(Direction.OUT), method.getReturnType());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
