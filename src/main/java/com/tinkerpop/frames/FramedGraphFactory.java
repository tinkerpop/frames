package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.annotations.AdjacencyAnnotationHandler;
import com.tinkerpop.frames.annotations.DomainAnnotationHandler;
import com.tinkerpop.frames.annotations.IncidenceAnnotationHandler;
import com.tinkerpop.frames.annotations.InitialAnnotationHandler;
import com.tinkerpop.frames.annotations.PropertyAnnotationHandler;
import com.tinkerpop.frames.annotations.RangeAnnotationHandler;
import com.tinkerpop.frames.annotations.TerminalAnnotationHandler;

/**
 * Creates a factory for creating {@link FramedGraph}s using a set of modules to
 * configure each graph. Note that by default all Framed graphs have the
 * following handlers registered: {@link PropertyAnnotationHandler}
 * {@link AdjacencyAnnotationHandler} {@link IncidenceAnnotationHandler}
 * {@link DomainAnnotationHandler} {@link RangeAnnotationHandler}
 * 
 * @author Bryn Cooke
 * 
 */
public class FramedGraphFactory {

	private Module[] modules;


	/**
	 * Create a {@link FramedGraphFactory} with a set of modules.
	 * 
	 * @param modules
	 *            The modules used to configure each {@link FramedGraph} created
	 *            by the factory.
	 */
	public FramedGraphFactory(Module... modules) {
		this.modules = modules;

	}
	
	/**
	 * Create a new {@link FramedGraph}.
	 * 
	 * @param baseGraph
	 *            The graph whose elements to frame.
	 * @return The {@link FramedGraph}
	 */
	public <T extends Graph> FramedGraph<T> create(T baseGraph) {
		FramedGraphConfiguration config = getConfiguration(Graph.class, baseGraph);
		FramedGraph<T> framedGraph = new FramedGraph<T>(baseGraph, config);
		return framedGraph;
	}

	/**
	 * Create a new {@link FramedGraph}.
	 * 
	 * @param baseGraph
	 *            The graph whose elements to frame.
	 * @return The {@link FramedGraph}
	 */
	public <T extends TransactionalGraph> FramedTransactionalGraph<T> create(T baseGraph) {
		FramedGraphConfiguration config = getConfiguration(TransactionalGraph.class, baseGraph);
		FramedTransactionalGraph<T> framedGraph = new FramedTransactionalGraph<T>(baseGraph, config);
		return framedGraph;
	}
	
	/**
	 * Returns a configuration that can be used when constructing a framed graph.
	 * @param requiredType The type of graph required after configuration e.g. {@link TransactionalGraph}
	 * @param baseGraph The base graph to get a configuration for.
	 * @return The configuration.
	 */
	protected <T extends Graph> FramedGraphConfiguration getConfiguration(Class<T> requiredType, T baseGraph) {
		FramedGraphConfiguration config = getBaseConfig();
		for (Module module : modules) {
			baseGraph = module.configure(baseGraph, config);
			if(!(requiredType.isInstance(baseGraph))) {
				throw new UnsupportedOperationException("Module '" + module.getClass() + "' returned a '" + baseGraph.getClass().getName() + "' but factory requires '" + requiredType.getName() + "'");
			}
		}
		config.setConfiguredGraph(baseGraph);
		return config;
	}

	private FramedGraphConfiguration getBaseConfig() {
		FramedGraphConfiguration config = new FramedGraphConfiguration();
		config.addAnnotationhandler(new PropertyAnnotationHandler());
		config.addAnnotationhandler(new AdjacencyAnnotationHandler());
		config.addAnnotationhandler(new IncidenceAnnotationHandler());
		config.addAnnotationhandler(new DomainAnnotationHandler());
		config.addAnnotationhandler(new RangeAnnotationHandler());
		config.addAnnotationhandler(new InitialAnnotationHandler());
		config.addAnnotationhandler(new TerminalAnnotationHandler());
		return config;
	}

}
