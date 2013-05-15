package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.EdgeTestSuite;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphTestSuite;
import com.tinkerpop.blueprints.TestSuite;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexTestSuite;
import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.typeregistry.TypeRegistryBuilder;
import com.tinkerpop.frames.typeregistry.TypeRegistryBuilderTest.A;
import com.tinkerpop.frames.typeregistry.TypeRegistryBuilderTest.Abstract;
import com.tinkerpop.frames.typeregistry.TypeRegistryBuilderTest.B;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedGraphTest extends GraphTest {

    public void testAnnotationHandlingBasics() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);

        int counter = framedGraph.getAnnotationHandlers().size();
        for (AnnotationHandler a : new HashSet<AnnotationHandler>(framedGraph.getAnnotationHandlers())) {
            assertTrue(framedGraph.hasAnnotationHandler(a.getAnnotationType()));
            counter--;
            framedGraph.unregisterAnnotationHandler(a.getAnnotationType());
            assertEquals(framedGraph.getAnnotationHandlers().size(), counter);

        }
        assertEquals(framedGraph.getAnnotationHandlers().size(), 0);
    }

    public void testFrameEquality() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);

        assertEquals(framedGraph.frame(graph.getVertex(1), Person.class), framedGraph.getVertex(1, Person.class));
        assertEquals(framedGraph.frame(graph.getEdge(7), Direction.OUT, Knows.class), framedGraph.getEdge(7, Direction.OUT, Knows.class));
    }

    public void testFrameVertices() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);

        int counter = 0;
        for (Person person : framedGraph.getVertices("name", "marko", Person.class)) {
            counter++;
            assertEquals(person.getName(), "marko");
        }
        assertEquals(counter, 1);

        counter = 0;
        for (Project project : framedGraph.frameVertices(graph.getVertices("lang", "java"), Project.class)) {
            counter++;
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
        assertEquals(counter, 2);

    }

    public void testCreateFrame() {
        Graph graph = new TinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);
        Person person = framedGraph.addVertex(null, Person.class);
        assertEquals(person.asVertex(), graph.getVertices().iterator().next());
        int counter = 0;
        for (Vertex v : graph.getVertices()) {
            counter++;
        }
        assertEquals(counter, 1);
        counter = 0;
        for (Edge e : graph.getEdges()) {
            counter++;
        }
        assertEquals(counter, 0);
        Person person2 = framedGraph.addVertex("aPerson", Person.class);
        assertEquals(person2.asVertex().getId(), "aPerson");
        counter = 0;
        for (Vertex v : graph.getVertices()) {
            counter++;
        }
        assertEquals(counter, 2);


    }
    
    public static @TypeField("type") interface Base {
    	@Property("label") String getLabel();
    };
	public static @TypeValue("A") interface A extends Base {};
	public static @TypeValue("B") interface B extends Base {};
	public static @TypeValue("C") interface C extends B {
    	@Property("label") void setLabel(String label);
	};
    
    public void testSerializeVertexType() {
        Graph graph = new TinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class).add(C.class).build());
        A a = framedGraph.addVertex(null, A.class);
        C c = framedGraph.addVertex(null, C.class);
        assertEquals("A", ((VertexFrame)a).asVertex().getProperty("type"));
        assertEquals("C", ((VertexFrame)c).asVertex().getProperty("type"));
    }
    
    public void testDeserializeVertexType() {
        Graph graph = new TinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class).add(C.class).build());
        Vertex cV = graph.addVertex(null);
        cV.setProperty("type", "C");
        cV.setProperty("label", "C Label");
        
        Base c = framedGraph.getVertex(cV.getId(), Base.class);
        assertTrue(c instanceof C);
        assertEquals("C Label", c.getLabel());
        ((C)c).setLabel("new label");
        assertEquals("new label", cV.getProperty("label"));
    }
    
    public void testSerializeEdgeType() {
        Graph graph = new TinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class).add(C.class).build());
        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        A a = framedGraph.addEdge(null, v1, v2, "label", Direction.OUT, A.class);
        C c = framedGraph.addEdge(null, v1, v2, "label", Direction.OUT, C.class);
        assertEquals("A", ((EdgeFrame)a).asEdge().getProperty("type"));
        assertEquals("C", ((EdgeFrame)c).asEdge().getProperty("type"));
    }
    
    public void testDeserializeEdgeType() {
        Graph graph = new TinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph, new TypeRegistryBuilder().add(A.class).add(B.class).add(C.class).build());
        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Edge cE = graph.addEdge(null, v1,  v2, "label");    
        cE.setProperty("type", "C");
        Base c = framedGraph.getEdge(cE.getId(), Direction.OUT, Base.class);
        assertTrue(c instanceof C);
    }

    public void testVertexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
    }

    public void testEdgeTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
    }

    public void testGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
    }

    public void testGraphMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
    }

    public void testGMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GMLReaderTestSuite(this));
        printTestPerformance("GMLReaderTestSuite", this.stopWatch());
    }

    public void testGraphSONReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphSONReaderTestSuite(this));
        printTestPerformance("GraphSONReaderTestSuite", this.stopWatch());
    }

    public Graph generateGraph() {
        final TinkerGraph baseGraph = new TinkerGraph();
        baseGraph.getFeatures().isPersistent = false;
        return new FramedGraph<TinkerGraph>(baseGraph);
    }

    public Graph generateGraph(final String directory) {
        return this.generateGraph();
    }

    public void doTestSuite(final TestSuite testSuite) throws Exception {
        for (Method method : testSuite.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("test")) {
                System.out.println("Testing " + method.getName() + "...");
                method.invoke(testSuite);
            }
        }
    }

}
