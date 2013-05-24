package com.tinkerpop.frames;

import junit.framework.Assert;

import org.junit.Test;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class FramedGraphFactoryTest {

	@Test
	public void testFactory() {
		FramedGraphFactory graphFactory = FramedGraphFactory.createFactory(new GraphModule());
		TinkerGraph base = new TinkerGraph();
		FramedGraph<TinkerGraph> framed = graphFactory.create(base);
		Assert.assertEquals(base, framed.getBaseGraph());
		
		TransactionalTinkerGraph baseTransactional = new TransactionalTinkerGraph();
		TransactionalFramedGraph<TransactionalTinkerGraph> framedTransactional = graphFactory.create(baseTransactional);
		Assert.assertEquals(baseTransactional, framedTransactional.getBaseGraph());
		
		

	}
	
	
	public static class GraphModule implements Module {

		@Override
		public Graph configure(Graph baseGraph, FramedGraphConfiguration config) {
			return baseGraph;
		}

		@Override
		public TransactionalGraph configure(TransactionalGraph baseGraph,
				FramedGraphConfiguration config) {
			return baseGraph;
		}

		
	}
	

	public static class TransactionalTinkerGraph extends TinkerGraph implements TransactionalGraph {

		@Override
		@Deprecated
		public void stopTransaction(Conclusion conclusion) {
		}

		@Override
		public void commit() {
		}

		@Override
		public void rollback() {
		}
		
	}
}
