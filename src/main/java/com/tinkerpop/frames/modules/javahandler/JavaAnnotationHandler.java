package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.util.ExceptionUtils;

public class JavaAnnotationHandler implements AnnotationHandler<JavaHandler> {

	private JavaHandlerFactory factory;

	public JavaAnnotationHandler(JavaHandlerFactory factory) {
		this.factory = factory;
	}

	@Override
	public Class<JavaHandler> getAnnotationType() {
		return JavaHandler.class;
	}

	@Override
	public Object processElement(JavaHandler annotation, Method method, Object[] arguments, FramedGraph framedGraph,
			Element element, Direction direction) {

		try {
			Object handler = factory.create(framedGraph, element, method, direction);
			return method.invoke(handler, arguments);
		} catch (IllegalArgumentException e) {
			throw new JavaHandlerException("Problem calling Java handler", e);
		} catch (IllegalAccessException e) {
			throw new JavaHandlerException("Problem calling Java handler", e);
		} catch (InvocationTargetException e) {
			ExceptionUtils.sneakyThrow(e.getTargetException());
			return null;
		}
	}

}
