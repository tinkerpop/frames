package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.Method;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;

/**
 * {@link JavaHandlerModule} uses this interface to create the concrete classes that will handle the method calls.
 * Typically factories will use method.getDeclaringClass() to obtain the concrete class that is being called.
 * @author Bryn Cooke
 */
public interface JavaHandlerFactory {
	/**
	 * @param graph The graph of the framed element
	 * @param element The element being handled
	 * @param method The method called
	 * @param direction The drection of the edge if applicable
	 * @return An object that contains the method being called. 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public <T> T create(Class<T> handlerClass) throws InstantiationException, IllegalAccessException;
}
