package com.tinkerpop.frames;


import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

import java.lang.reflect.Method;

/**
 *  Skeleton implementation for {@see FrameEventListener}
 */
public abstract class AbstractEventListener implements FrameEventListener {
    
    @Override
    public void preCreateEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final String label, final Vertex outVertex, final Vertex inVertex) { }

    @Override
    public void postCreateEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final Edge edge) {}

    @Override
    public void postCreateVertex(final Class<?> kind, final FramedGraph<?> framedGraph, final Vertex vertex) {}

    @Override
    public void preUpdateProperty(final FramedGraph<?> framedGraph, final Element element, final Method method, final Object fieldName, final Object newValue) {}

    @Override
    public void preDeleteProperty(final FramedGraph<?> framedGraph, final Element element, final Method method, final Object fieldName) {}

    @Override
    public void preDeleteVertex(final Class<?> kind, final FramedGraph<?> framedGraph, final Vertex vertexFrame) {}

    @Override
    public void preDeleteEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final Edge edge) {}
}
