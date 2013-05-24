package com.tinkerpop.frames;

import com.tinkerpop.blueprints.TransactionalGraph;

public class TransactionalFramedGraph<T extends TransactionalGraph> extends FramedGraph<T> implements TransactionalGraph {


	TransactionalFramedGraph(T baseGraph, FramedGraphConfiguration config,
			TransactionalGraph configuredBaseGraph) {
		super(baseGraph, config, configuredBaseGraph);
		
	}

	@Override
	public void commit() {
		getBaseGraph().commit();
	}
	
	@Override
	public void rollback() {
		getBaseGraph().rollback();
	}

	@Override
	@Deprecated
	public void stopTransaction(Conclusion conclusion) {
		getBaseGraph().stopTransaction(conclusion);
	}
	
	
	
	
}
