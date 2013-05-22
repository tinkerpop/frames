package com.tinkerpop.frames.annotations.gremlin;

import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.Module;

/**
 * Adds <code>@GremlinGroovy</code> support to the framed graph.
 * @author Bryn Cooke
 *
 */
public class GremlinGroovyModule implements Module {
	private GremlinGroovyAnnotationHandler handler = new GremlinGroovyAnnotationHandler(); //Factory will share handler.

	@Override
	public void configure(FramedGraphConfiguration config) {
		config.addAnnotationhandler(handler);
	}


}
