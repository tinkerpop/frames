package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;

public class AbstractModule implements Module {

	@Override
	public Graph configure(Graph baseGraph, FramedGraphConfiguration config) {
		configure(config);
		return baseGraph;
	}

	@Override
	public TransactionalGraph configure(TransactionalGraph baseGraph,
			FramedGraphConfiguration config) {
		configure(config);
		return baseGraph;
	}
	
	public void configure(FramedGraphConfiguration config) {
		
	}

}
