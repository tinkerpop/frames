package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.Method;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;

/**
 * {@link JavaHandlerModule} uses this interface to create the concrete classes that will handle the method calls.
 * @author Bryn Cooke
 */
public interface JavaHandlerFactory {
	public <T> T create(FramedGraph<?> graph, Element element, Method method, Direction direction);
}
