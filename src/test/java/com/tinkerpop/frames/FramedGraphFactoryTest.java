package com.tinkerpop.frames;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class FramedGraphFactoryTest {

	private Module mockModule;


	@Before
	public void setup() {
		mockModule = Mockito.mock(Module.class);
	}
	
	@Test
	public void testFactory() {
		
		Mockito.when(mockModule.configure(Mockito.any(Graph.class), Mockito.any(FramedGraphConfiguration.class))).then(new ReturnsArgumentAt(0));
		FramedGraphFactory graphFactory = FramedGraphFactory.createFactory(mockModule);
		TinkerGraph base = new TinkerGraph();
		FramedGraph<TinkerGraph> framed = graphFactory.create(base);
		Assert.assertEquals(base, framed.getBaseGraph());
		
		Mockito.verify(mockModule, Mockito.times(1)).configure(Mockito.any(Graph.class), Mockito.any(FramedGraphConfiguration.class));
		
		TransactionalTinkerGraph baseTransactional = new TransactionalTinkerGraph();
		FramedTransactionalGraph<TransactionalTinkerGraph> framedTransactional = graphFactory.create(baseTransactional);
		Assert.assertEquals(baseTransactional, framedTransactional.getBaseGraph());

		Mockito.verify(mockModule, Mockito.times(1)).configure(Mockito.any(TransactionalGraph.class), Mockito.any(FramedGraphConfiguration.class));
	}
	
	@Test
	public void testWrapping() {
		Graph wrapper = Mockito.mock(Graph.class);
		Mockito.when(mockModule.configure(Mockito.any(Graph.class), Mockito.any(FramedGraphConfiguration.class))).thenReturn(wrapper);
		FramedGraphFactory graphFactory = FramedGraphFactory.createFactory(mockModule);
		TinkerGraph base = new TinkerGraph();
		FramedGraph<TinkerGraph> framed = graphFactory.create(base);
		
		Mockito.verify(mockModule).configure(Mockito.any(Graph.class), Mockito.any(FramedGraphConfiguration.class));
		
		//Unwrapping the graph should retrieve the base graph.
		Assert.assertEquals(base, framed.getBaseGraph());

		//But using the framed graph should go through the wrappers installed by the module.
		Vertex vertex = Mockito.mock(Vertex.class);
		Mockito.when(wrapper.getVertex(1)).thenReturn(vertex);
		Assert.assertEquals(vertex, framed.getVertex(1));
		
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
