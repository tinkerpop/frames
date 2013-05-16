package com.tinkerpop.frames.typed;

import junit.framework.TestCase;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;


public class TypedFramedGraphTest extends TestCase {
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
		FramedGraph<Graph> framedGraph = new TypedFramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class)
				.add(C.class).build());
		A a = framedGraph.addVertex(null, A.class);
		C c = framedGraph.addVertex(null, C.class);
		assertEquals("A", ((VertexFrame) a).asVertex().getProperty("type"));
		assertEquals("C", ((VertexFrame) c).asVertex().getProperty("type"));
	}

	public void testDeserializeVertexType() {
		Graph graph = new TinkerGraph();
		FramedGraph<Graph> framedGraph = new TypedFramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class)
				.add(C.class).build());
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
		FramedGraph<Graph> framedGraph = new TypedFramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class)
				.add(C.class).build());
		Vertex v1 = graph.addVertex(null);
		Vertex v2 = graph.addVertex(null);
		A a = framedGraph.addEdge(null, v1, v2, "label", Direction.OUT, A.class);
		C c = framedGraph.addEdge(null, v1, v2, "label", Direction.OUT, C.class);
		assertEquals("A", ((EdgeFrame) a).asEdge().getProperty("type"));
		assertEquals("C", ((EdgeFrame) c).asEdge().getProperty("type"));
	}

	public void testDeserializeEdgeType() {
		Graph graph = new TinkerGraph();
		FramedGraph<Graph> framedGraph = new TypedFramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class)
				.add(C.class).build());
		Vertex v1 = graph.addVertex(null);
		Vertex v2 = graph.addVertex(null);
		Edge cE = graph.addEdge(null, v1, v2, "label");
		cE.setProperty("type", "C");
		Base c = framedGraph.getEdge(cE.getId(), Direction.OUT, Base.class);
		assertTrue(c instanceof C);
	}
}
