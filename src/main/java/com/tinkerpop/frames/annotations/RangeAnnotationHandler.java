package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.Range;

public class RangeAnnotationHandler implements AnnotationHandler<Range> {

    @Override
    public Class<Range> getAnnotationType() {
        return Range.class;
    }

    @Override
    public Object processVertex(Range annotation, Method method, Object[] arguments, FramesManager manager, Vertex element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object processEdge(Range annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
        if (direction.equals(Direction.STANDARD)) {
            return manager.frame(((Edge) element).getInVertex(), method.getReturnType());
        } else {
            return manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
        }
    }

}
