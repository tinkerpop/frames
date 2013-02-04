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
    public Object processElement(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (element instanceof Vertex) {
            return processVertex(annotation, method, arguments, framedGraph, (Vertex) element);
        } else {
            throw new UnsupportedOperationException("This method only works for vertices");
        }
    }

    public Object processVertex(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        if (ClassUtilities.isGetMethod(method)) {
            final Pipe pipe = Gremlin.compile(annotation.value());
            pipe.setStarts(new SingleIterator<Element>(vertex));
            FramedVertexIterable r = new FramedVertexIterable(framedGraph, pipe, ClassUtilities.getGenericClass(method));
            if (ClassUtilities.returnsIterable(method)) {
                return r;
            } else {
                return r.iterator().hasNext() ? r.iterator().next() : null;
            }
        } else {
            throw new UnsupportedOperationException("Gremlin only works with getters");
        }
    }

}
