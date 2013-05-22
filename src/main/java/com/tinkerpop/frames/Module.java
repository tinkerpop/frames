package com.tinkerpop.frames;

import com.tinkerpop.frames.annotations.AnnotationHandler;

/**
 * A module is a group of functionality that must be configured on a
 * FramedGraph. They are used by {@link FramedGraphFactory} to create a
 * configuration for each graphs produced by that factory.
 * 
 * Modules may add {@link FrameInitializer}s {@link TypeResolver}s and
 * {@link AnnotationHandler}s to the configuration.
 * 
 * Modules should be fast and light weight as configure will be called for each {@link FramedGraph} created. 
 * 
 * @see FramedGraphFactory
 * 
 * @author Bryn Cooke
 */
public interface Module {
	/**
	 * @param config The configuration for the new graph.
	 */
	void configure(FramedGraphConfiguration config);
}
