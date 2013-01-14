package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;

import java.lang.reflect.Method;

public class PropertyAnnotationHandler implements AnnotationHandler<Property> {

    @Override
    public Class<Property> getAnnotationType() {
        return Property.class;
    }

    @Override
    public Object processElement(final Property annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (ClassUtilities.isGetMethod(method)) {
            return element.getProperty(annotation.value());
        } else if (ClassUtilities.isSetMethod(method)) {
            Object value = arguments[0];
            if (null == value) {
                element.removeProperty(annotation.value());
            } else {
                element.setProperty(annotation.value(), value);
            }
            return null;
        } else if (ClassUtilities.isRemoveMethod(method)) {
            element.removeProperty(annotation.value());
            return null;
        }

        return null;
    }

}
