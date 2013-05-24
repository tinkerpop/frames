package com.tinkerpop.frames;

import org.junit.Test;
import org.mockito.Mockito;

import com.tinkerpop.blueprints.TransactionalGraph;

public class TransactionalFramedGraphTest {
	@Test
	public void testTransactions() {

		TransactionalGraph t = Mockito.mock(TransactionalGraph.class);
		TransactionalFramedGraph<TransactionalGraph> g = new TransactionalFramedGraph<TransactionalGraph>(t, new FramedGraphConfiguration(), t);
		g.commit();
		Mockito.verify(t).commit();
		Mockito.reset(t);
		
		g.rollback();
		Mockito.verify(t).rollback();
	}
}
