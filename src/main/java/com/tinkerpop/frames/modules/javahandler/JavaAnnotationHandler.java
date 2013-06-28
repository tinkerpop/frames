package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutionException;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.util.ExceptionUtils;

public class JavaAnnotationHandler implements AnnotationHandler<JavaHandler> {

	private JavaHandlerFactory factory;
	private LoadingCache<Class<?>, Class<?>> handlerClassCache;

	public JavaAnnotationHandler(JavaHandlerFactory factory, LoadingCache<Class<?>, Class<?>> handlerClassCache) {
		this.factory = factory;
		this.handlerClassCache = handlerClassCache;
	}

	@Override
	public Class<JavaHandler> getAnnotationType() {
		return JavaHandler.class;
	}

	

	public <T> T create(final FramedGraph<?> graph, final Element element,
			final Method method, final Direction direction) {

		try {
			final Class<?> frameClass = method.getDeclaringClass();

			Class<T> handlerClass = (Class<T>) getHandlerClass(frameClass);
			Class<T> implClass = (Class<T>) handlerClassCache.get(handlerClass);
			T handler = factory.create(implClass);
			((Proxy) handler).setHandler(new MethodHandler() {
				private DefaultJavaHandlerImpl<Element> defaultJavahandlerImpl = new DefaultJavaHandlerImpl<Element>(
						graph, method, element);
				private Object framedElement;

				@Override
				public Object invoke(Object o, Method m, Method proceed,
						Object[] args) throws Throwable {
					if (!Modifier.isAbstract(m.getModifiers())) {
						return proceed.invoke(o, args);
					} else {
						if (m.getDeclaringClass() == JavaHandlerImpl.class) {
							return m.invoke(defaultJavahandlerImpl, args);
						}
						if (framedElement == null) {
							if (element instanceof Vertex) {
								framedElement = graph.frame((Vertex) element,
										frameClass);
							} else {
								framedElement = graph.frame((Edge) element,
										direction, frameClass);
							}
						}
						return m.invoke(framedElement, args);
					}
				}

			});
			return handler;
		} catch (ExecutionException e) {
			throw new JavaHandlerException(
					"Cannot create class for handling framed method", e);
		} catch (InstantiationException e) {
			throw new JavaHandlerException(
					"Problem instantiating handler class", e);
		} catch (IllegalAccessException e) {
			throw new JavaHandlerException(
					"Problem instantiating handler class", e);
		} catch (ClassNotFoundException e) {
			throw new JavaHandlerException("Problem location handler class", e);
		}

	}

	public Class<?> getHandlerClass(Class<?> frameClass)
			throws ClassNotFoundException {
		JavaHandlerClass handlerClass = frameClass
				.getAnnotation(JavaHandlerClass.class);
		if (handlerClass != null) {
			return handlerClass.value();
		}
		return frameClass.getClassLoader().loadClass(
				frameClass.getName() + "$Impl");
	}

	@Override
	public Object processElement(JavaHandler annotation, Method method,
			Object[] arguments, FramedGraph framedGraph, Element element,
			Direction direction) {

		try {
			Object handler = create(framedGraph, element, method, direction);
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
