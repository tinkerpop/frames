package com.tinkerpop.frames;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.frames.annotations.AnnotationHandler;

/**
 * A configuration for a {@link FramedGraph}. These are supplied to
 * {@link Module}s for each {@link FramedGraph} being create by a
 * {@link FramedGraphFactory}.
 * 
 * Allows registration of {@link AnnotationHandler}s, {@link FrameInitializer}s
 * and {@link TypeResolver}s.
 * 
 * @author Bryn Cooke
 * 
 */
public class FramedGraphConfiguration {
	private Map<Class<? extends Annotation>, AnnotationHandler<?>> annotationHandlers = new HashMap<Class<? extends Annotation>, AnnotationHandler<?>>();
	private List<FrameInitializer> frameInitializers = new ArrayList<FrameInitializer>();
	private List<TypeResolver> typeResolvers = new ArrayList<TypeResolver>();

	FramedGraphConfiguration() {

	}

	/**
	 * @param annotationHandler The {@link AnnotationHandler} to add to the {@link FramedGraph}.
	 */
	public void addAnnotationhandler(AnnotationHandler<?> annotationHandler) {
		annotationHandlers.put(annotationHandler.getAnnotationType(),
				annotationHandler);
	}

	/**
	 * @param frameInitializer The {@link FrameInitializer} to add to the {@link FramedGraph}.
	 */
	public void addFrameInitializer(FrameInitializer frameInitializer) {
		frameInitializers.add(frameInitializer);
	}

	/**
	 * @param typeResolver The {@link TypeResolver} to add to the {@link FramedGraph}.
	 */
	public void addTypeResolver(TypeResolver typeResolver) {
		typeResolvers.add(typeResolver);
	}

	List<FrameInitializer> getFrameInitializers() {
		return frameInitializers;
	}

	Map<Class<? extends Annotation>, AnnotationHandler<?>> getAnnotationHandlers() {
		return annotationHandlers;
	}

	List<TypeResolver> getTypeResolvers() {
		return typeResolvers;
	}

}
