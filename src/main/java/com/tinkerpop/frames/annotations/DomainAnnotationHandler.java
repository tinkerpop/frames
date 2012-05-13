package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.FramesManager;

import java.lang.reflect.Method;

public class DomainAnnotationHandler implements AnnotationHandler<Domain> {

    @Override
    public Class<Domain> getAnnotationType() {
        return Domain.class;
    }

    @Override
    public Object processVertex(Domain annotation, Method method, Object[] arguments, FramesManager manager, Vertex element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object processEdge(Domain annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
        if (direction.equals(Direction.STANDARD)) {
            return manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
        } else {
            return manager.frame(((Edge) element).getInVertex(), method.getReturnType());
        }
    }

}
