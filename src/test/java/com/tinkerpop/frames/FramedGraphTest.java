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
        assertEquals(framedGraph.frame(graph.getEdge(7), Knows.class), framedGraph.getEdge(7, Knows.class));
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
