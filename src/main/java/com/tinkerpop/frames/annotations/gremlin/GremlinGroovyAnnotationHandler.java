package com.tinkerpop.frames.annotations.gremlin;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.util.ClassUtils;
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
    public Object processVertex(GremlinGroovy annotation, Method method, Object[] arguments, FramesManager manager, Vertex element) {
        return process(annotation, method, arguments, manager, element);
    }

    @Override
    public Object processEdge(GremlinGroovy annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
        return process(annotation, method, arguments, manager, element);
    }

    private Object process(GremlinGroovy annotation, Method method, Object[] arguments, FramesManager manager, Element element) {
        Pipe pipe = Gremlin.compile(annotation.value());
        if (ClassUtils.isGetMethod(method)) {
            pipe.setStarts(new SingleIterator<Element>(element));
            if (element instanceof Vertex) {
                return new IterableCollection(new FramingVertexIterable(manager, pipe, ClassUtils.getGenericClass(method)));
            } else {
                throw new UnsupportedOperationException("This method only works for vertices");
            }
        }

        return null;
    }
}
