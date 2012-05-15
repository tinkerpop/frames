package com.tinkerpop.frames.annotations.gremlin;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.util.ClassUtilities;
import com.tinkerpop.frames.util.FramingVertexIterable;
import com.tinkerpop.frames.util.IterableCollection;
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
    public Object processVertex(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramesManager manager, final Vertex vertex) {
        return process(annotation, method, arguments, manager, vertex);
    }

    @Override
    public Object processEdge(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramesManager manager, final Edge edge, final Direction direction) {
        return process(annotation, method, arguments, manager, edge);
    }

    private Object process(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramesManager manager, final Element element) {
        if (ClassUtilities.isGetMethod(method)) {
            if (element instanceof Vertex) {
                final Pipe pipe = Gremlin.compile(annotation.value());
                pipe.setStarts(new SingleIterator<Element>(element));
                return new IterableCollection(new FramingVertexIterable(manager, pipe, ClassUtilities.getGenericClass(method)));
            } else {
                throw new UnsupportedOperationException("This method only works for vertices");
            }
        }
        return null;
    }
}
