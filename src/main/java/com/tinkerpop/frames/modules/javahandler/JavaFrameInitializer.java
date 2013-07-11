package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FrameInitializer;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.util.ExceptionUtils;

/**
 * Calls the methods annotated with {@link Initializer} on frame
 * implementations.
 * 
 * @author Bryn Cooke
 * 
 */
class JavaFrameInitializer implements FrameInitializer {

	private JavaHandlerModule module;

	JavaFrameInitializer(JavaHandlerModule module) {
		this.module = module;
	}

	@Override
	public void initElement(Class<?> kind, FramedGraph<?> framedGraph, Element element) {

		Object framedElement;
		if (element instanceof Vertex) {
			framedElement = framedGraph.frame((Vertex) element, kind);
		} else {
			framedElement = framedGraph.frame((Edge) element, kind);
		}

		// We have to order this correctly. Dependencies should be initialised
		// first so we first recursively collect an an array of classes to call
		// and then reverse the array before putting them in a linked hash set.
		// That way the classes discovered last will be called first.
		List<Class<?>> classes = new ArrayList<Class<?>>();
		depthFirstClassSearch(classes, kind);

		Collections.reverse(classes);
		LinkedHashSet<Class<?>> hierarcy = new LinkedHashSet<Class<?>>(classes);

		
		// Now we can call the methods
		for (Class<?> h : hierarcy) {
			try {
				
				
				try {
					Class<?> implKind = module.getHandlerClass(h);
					for (Method method : implKind.getDeclaredMethods()) {
						if (method.isAnnotationPresent(Initializer.class)) {
							if (method.getParameterTypes().length != 0) {
								throw new JavaHandlerException("Java handler initializer " + method + "cannot have parameters");
							}
							Object handler = module.createHandler(framedElement, framedGraph, element, h, method);
							method.invoke(handler);
						}

					}
				} catch (ClassNotFoundException e) {
					// There was no impl class to check
				}
				
				
				
			} catch (IllegalArgumentException e) {
				throw new JavaHandlerException("Problem calling Java handler", e);
			} catch (IllegalAccessException e) {
				throw new JavaHandlerException("Problem calling Java handler", e);
			} catch (InvocationTargetException e) {
				ExceptionUtils.sneakyThrow(e.getTargetException());
			}
		}

	}

	private void depthFirstClassSearch(List<Class<?>> initializers, Class<?> kind) {

		if (kind == null || kind == Object.class) {
			return;
		}

		initializers.add(kind);

		for (Class<?> i : kind.getInterfaces()) {
			depthFirstClassSearch(initializers, i);
		}
		depthFirstClassSearch(initializers, kind.getSuperclass());

	}

}
