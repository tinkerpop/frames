package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.annotations.AdjacencyAnnotationHandler;
import com.tinkerpop.frames.annotations.DomainAnnotationHandler;
import com.tinkerpop.frames.annotations.IncidenceAnnotationHandler;
import com.tinkerpop.frames.annotations.PropertyAnnotationHandler;
import com.tinkerpop.frames.annotations.RangeAnnotationHandler;

/**
 * Creates a factory for creating {@link FramedGraph}s using a set of modules to configure each graph.
 * Note that by default all Framed graphs have the following handlers registered:
 * {@link PropertyAnnotationHandler}
 * {@link AdjacencyAnnotationHandler}
 * {@link IncidenceAnnotationHandler}
 * {@link DomainAnnotationHandler}
 * {@link RangeAnnotationHandler}
 * 
 * @author Bryn Cooke
 *
 */
public class FramedGraphFactory {
	
	private Module[] modules;
	private FramedGraphFactory(Module... modules) {
		this.modules = modules;
		
	}

	
	/**
	 * Create a new {@link FramedGraph}.
	 * @param baseGraph The graph whose elements to frame.
	 * @return The {@link FramedGraph}
	 */
	public <T extends Graph> FramedGraph<T> create(T baseGraph) {
		FramedGraphConfiguration config = getBaseConfig();
		Graph graph = baseGraph;
		for(Module module : modules) {
			graph = module.configure(graph, config);
		}
		FramedGraph<T> framedGraph = new FramedGraph<T>(baseGraph, config, graph);
		return framedGraph;
	}
	
	
	/**
	 * Create a new {@link FramedGraph}.
	 * @param baseGraph The graph whose elements to frame.
	 * @return The {@link FramedGraph}
	 */
	public <T extends TransactionalGraph> TransactionalFramedGraph<T> create(T baseGraph) {
		FramedGraphConfiguration config = getBaseConfig();
		TransactionalGraph graph = baseGraph;
		for(Module module : modules) {
			graph = module.configure(graph, config);
		}
		TransactionalFramedGraph<T> framedGraph = new TransactionalFramedGraph<T>(baseGraph, config, graph);
		return framedGraph;
	}


	private FramedGraphConfiguration getBaseConfig() {
		FramedGraphConfiguration config = new FramedGraphConfiguration();
		config.addAnnotationhandler(new PropertyAnnotationHandler());
		config.addAnnotationhandler(new AdjacencyAnnotationHandler());
		config.addAnnotationhandler(new IncidenceAnnotationHandler());
		config.addAnnotationhandler(new DomainAnnotationHandler());
		config.addAnnotationhandler(new RangeAnnotationHandler());
		return config;
	}
	
	/**
	 * Create a {@link FramedGraphFactory} with a set of modules.
	 * @param modules The modules used to configure each {@link FramedGraph} created by the factory.
	 * @return The {@link FramedGraphFactory}.
	 */
	public static FramedGraphFactory createFactory(Module... modules) {
		return new FramedGraphFactory(modules);
	}
	
}
