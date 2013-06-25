package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Terminal;

import java.lang.reflect.Method;

public class TerminalAnnotationHandler implements AnnotationHandler<Terminal> {
    @Override
    public Class<Terminal> getAnnotationType() {
        return Terminal.class;
    }

    @Override
    public Object processElement(final Terminal annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (element instanceof Edge) {
        	return framedGraph.frame(((Edge)element).getVertex(Direction.IN), method.getReturnType());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
