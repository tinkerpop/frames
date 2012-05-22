package com.tinkerpop.frames.annotations.gremlin;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedVertexIterable;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.util.iterators.SingleIterator;

import java.lang.reflect.Method;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GremlinGroovyAnnotationHandler implements AnnotationHandler<GremlinGroovy> {

    @Override
    public Class<GremlinGroovy> getAnnotationType() {
        return GremlinGroovy.class;
    }

    @Override
    public Object processVertex(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        if (ClassUtilities.isGetMethod(method)) {
            final Pipe pipe = Gremlin.compile(annotation.value());
            pipe.setStarts(new SingleIterator<Element>(vertex));
            return new FramedVertexIterable(framedGraph, pipe, ClassUtilities.getGenericClass(method));
        } else {
            throw new UnsupportedOperationException("Gremlin only works with getters");
        }
    }

    @Override
    public Object processEdge(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge edge, final Direction direction) {
        throw new UnsupportedOperationException("This method only works for vertices");
    }
}
