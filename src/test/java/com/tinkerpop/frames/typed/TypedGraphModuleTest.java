package com.tinkerpop.frames.typed;

import junit.framework.TestCase;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;


public class TypedGraphModuleTest extends TestCase {
	public static @TypeField("type")
	interface Base {
		@Property("label")
		String getLabel();
	};

	public static @TypeValue("A")
	interface A extends Base {
	};

	public static @TypeValue("B")
	interface B extends Base {
	};

	public static @TypeValue("C")
	interface C extends B {
		@Property("label")
		void setLabel(String label);
	};

	public void testSerializeVertexType() {
		Graph graph = new TinkerGraph();
		FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
				.withClass(C.class).build());
		FramedGraph<Graph> framedGraph = factory.create(graph);
		A a = framedGraph.addVertex(null, A.class);
		C c = framedGraph.addVertex(null, C.class);
		assertEquals("A", ((VertexFrame) a).asVertex().getProperty("type"));
		assertEquals("C", ((VertexFrame) c).asVertex().getProperty("type"));
	}

	public void testDeserializeVertexType() {
		Graph graph = new TinkerGraph();
		FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
				.withClass(C.class).build());
		FramedGraph<Graph> framedGraph = factory.create(graph);
		Vertex cV = graph.addVertex(null);
		cV.setProperty("type", "C");
		cV.setProperty("label", "C Label");

		Base c = framedGraph.getVertex(cV.getId(), Base.class);
		assertTrue(c instanceof C);
		assertEquals("C Label", c.getLabel());
		((C) c).setLabel("new label");
		assertEquals("new label", cV.getProperty("label"));
	}

	public void testSerializeEdgeType() {
		Graph graph = new TinkerGraph();
		FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
				.withClass(C.class).build());
		FramedGraph<Graph> framedGraph = factory.create(graph);
		Vertex v1 = graph.addVertex(null);
		Vertex v2 = graph.addVertex(null);
		A a = framedGraph.addEdge(null, v1, v2, "label", A.class);
		C c = framedGraph.addEdge(null, v1, v2, "label", C.class);
		assertEquals("A", ((EdgeFrame) a).asEdge().getProperty("type"));
		assertEquals("C", ((EdgeFrame) c).asEdge().getProperty("type"));
	}

	public void testDeserializeEdgeType() {
		Graph graph = new TinkerGraph();
		FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
				.withClass(C.class).build());
		FramedGraph<Graph> framedGraph = factory.create(graph);
		Vertex v1 = graph.addVertex(null);
		Vertex v2 = graph.addVertex(null);
		Edge cE = graph.addEdge(null, v1, v2, "label");
		cE.setProperty("type", "C");
		Base c = framedGraph.getEdge(cE.getId(), Base.class);
		assertTrue(c instanceof C);
	}
}
