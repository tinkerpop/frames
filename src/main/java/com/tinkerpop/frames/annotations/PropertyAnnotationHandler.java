package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.util.ClassUtils;

public class PropertyAnnotationHandler implements AnnotationHandler<Property> {

	@Override
	public Class<Property> getAnnotationType() {
		return Property.class;
	}

	@Override
	public Object processVertex(Property annotation, Method method, Object[] arguments, FramesManager manager, Vertex element) {
		 return process(annotation, method, arguments, element);
	}

	@Override
	public Object processEdge(Property annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
		return process(annotation, method, arguments, element);
	}
	
	private Object process(Property annotation, Method method, Object[] arguments, Element element){
		if (ClassUtils.isGetMethod(method)) {
            return element.getProperty(((Property) annotation).value());
        } else if (ClassUtils.isSetMethod(method)) {
            element.setProperty(((Property) annotation).value(), arguments[0]);
            return null;
        } else if (ClassUtils.isRemoveMethod(method)) {
            element.removeProperty(((Property) annotation).value());
            return null;
        }
		 
		return null;
	}
	
}
