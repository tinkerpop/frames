package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.util.ClassUtilities;

import java.lang.reflect.Method;

public class PropertyAnnotationHandler implements AnnotationHandler<Property> {

    @Override
    public Class<Property> getAnnotationType() {
        return Property.class;
    }

    @Override
    public Object processVertex(final Property annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        return process(annotation, method, arguments, vertex);
    }

    @Override
    public Object processEdge(final Property annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge edge, final Direction direction) {
        return process(annotation, method, arguments, edge);
    }

    private Object process(final Property annotation, final Method method, final Object[] arguments, final Element element) {
        if (ClassUtilities.isGetMethod(method)) {
            return element.getProperty(annotation.value());
        } else if (ClassUtilities.isSetMethod(method)) {
            element.setProperty(annotation.value(), arguments[0]);
            return null;
        } else if (ClassUtilities.isRemoveMethod(method)) {
            element.removeProperty(annotation.value());
            return null;
        }

        return null;
    }

}
