package com.tinkerpop.frames;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

/**
 * @author Bryn Cooke
 */
public class ClassUtilitiesTest {

	@Test()
	public void testReturnsFramedType() throws NoSuchMethodException, SecurityException {
		FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(new TinkerGraph());

		assertTrue(ClassUtilities.returnsFramedType(MyAccessFrame.class.getMethod("getMyVertex"), framedGraph));
		assertTrue(ClassUtilities.returnsFramedType(MyAccessFrame.class.getMethod("getMyEdge"), framedGraph));
		assertTrue(ClassUtilities.returnsFramedType(MyAccessFrame.class.getMethod("getFrame"), framedGraph));
		assertFalse(ClassUtilities.returnsFramedType(MyAccessFrame.class.getMethod("getNotFrame"), framedGraph));
	}

	public static interface MyAccessFrame extends VertexFrame {

		MyVertex getMyVertex();

		MyEdge getMyEdge();

		MyFrame getFrame();

		NotFrame getNotFrame();

	}

	public static interface MyVertex extends VertexFrame {

	}

	public static interface MyEdge extends EdgeFrame {

	}

	public static interface MyFrame {
		@Adjacency(label = "test")
		MyFrame getFrame();
	}

	public static interface NotFrame {
		MyFrame getFrame();
	}

}
