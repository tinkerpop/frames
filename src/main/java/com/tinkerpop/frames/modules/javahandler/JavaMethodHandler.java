package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutionException;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;

import com.google.common.cache.LoadingCache;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.util.ExceptionUtils;

public class JavaMethodHandler implements com.tinkerpop.frames.modules.MethodHandler<JavaHandler> {

	private JavaHandlerFactory factory;
	private LoadingCache<Class<?>, Class<?>> handlerClassCache;

	public JavaMethodHandler(JavaHandlerFactory factory, LoadingCache<Class<?>, Class<?>> handlerClassCache) {
		this.factory = factory;
		this.handlerClassCache = handlerClassCache;
	}

	@Override
	public Class<JavaHandler> getAnnotationType() {
		return JavaHandler.class;
	}

	

	public <T> T create(final Object framedElement, final FramedGraph<?> graph, final Element element,
			final Method method) {

		try {
			final Class<?> frameClass = method.getDeclaringClass();

			Class<T> handlerClass = (Class<T>) getHandlerClass(frameClass);
			Class<T> implClass = (Class<T>) handlerClassCache.get(handlerClass);
			T handler = factory.create(implClass);
			((Proxy) handler).setHandler(new MethodHandler() {
				private JavaHandlerContextImpl<Element> defaultJavahandlerImpl = new JavaHandlerContextImpl<Element>(
						graph, method, element);
				

				@Override
				public Object invoke(Object o, Method m, Method proceed,
						Object[] args) throws Throwable {
					if (!Modifier.isAbstract(m.getModifiers())) {
						return proceed.invoke(o, args);
					} else {
						if(m.getAnnotation(JavaHandler.class) != null) {
							throw new JavaHandlerException("Method " + m.getDeclaringClass().getName() + "." + m.getName() + " is marked with @JavaHandler but is not implemented");
						}
						if (m.getDeclaringClass() == JavaHandlerContext.class) {
							return m.invoke(defaultJavahandlerImpl, args);
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
	public Object processElement(Object framedElement, Method method,
			Object[] arguments, JavaHandler annotation,
			FramedGraph<?> framedGraph, Element element) {
		try {
			Object handler = create(framedElement, framedGraph, element, method);
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
