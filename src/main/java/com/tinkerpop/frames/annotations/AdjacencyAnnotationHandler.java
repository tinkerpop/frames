package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.util.AdjacencyCollection;
import com.tinkerpop.frames.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AdjacencyAnnotationHandler implements AnnotationHandler<Adjacency> {

    @Override
    public Class<Adjacency> getAnnotationType() {
        return Adjacency.class;
    }

    @Override
    public Object processVertex(Adjacency adjacency, Method method, Object[] arguments, FramesManager manager, Vertex element) {
        if (ClassUtils.isGetMethod(method)) {
            return new AdjacencyCollection(manager, element, adjacency.label(), adjacency.direction(), ClassUtils.getGenericClass(method));
        } else if (ClassUtils.isAddMethod(method)) {
            if (adjacency.direction().equals(Direction.STANDARD))
                return manager.frame(manager.getGraph().addEdge(null, element, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), adjacency.label()), Direction.STANDARD, method.getReturnType());
            else
                return manager.frame(manager.getGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), element, adjacency.label()), Direction.INVERSE, method.getReturnType());
        } else if (ClassUtils.isRemoveMethod(method)) {
            manager.getGraph().removeEdge((Edge) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement());
            return null;
        }

        return null;
    }

    @Override
    public Object processEdge(Adjacency annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
        throw new UnsupportedOperationException();
    }

}
