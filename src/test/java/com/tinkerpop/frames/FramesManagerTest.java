package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.Knows;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramesManagerTest extends TestCase {

    public void testFrameEquality() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        assertEquals(manager.frame(graph.getVertex(1), Person.class), manager.frameVertex(1, Person.class));
        assertEquals(manager.frame(graph.getEdge(7), Direction.STANDARD, Knows.class), manager.frameEdge(7, Direction.STANDARD, Knows.class));
    }

    public void testFrameVertices() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        int counter = 0;
        for (Person person : manager.frameVertices(graph.getVertices("name", "marko"), Person.class)) {
            counter++;
            assertEquals(person.getName(), "marko");
        }
        assertEquals(counter, 1);

        counter = 0;
        for (Project project : manager.frameVertices(graph.getVertices("lang", "java"), Project.class)) {
            counter++;
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
        assertEquals(counter, 2);

    }

    /*public void testFrameEdges() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        int counter = 0;
        for (Knows knows : manager.frameEdges(Index.EDGES, AutomaticIndex.LABEL, "knows", Direction.STANDARD, Knows.class)) {
            counter++;
            assertEquals(knows.getDomain().getName(), "marko");
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas"));
        }
        assertEquals(counter, 2);
    }*/

    public void testCreateFrame() {
        Graph graph = new TinkerGraph();
        FramesManager manager = new FramesManager(graph);
        Person person = manager.createFramedVertex(Person.class);
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
        Person person2 = manager.createFramedVertex("aPerson", Person.class);
        assertEquals(person2.asVertex().getId(), "aPerson");
        counter = 0;
        for (Vertex v : graph.getVertices()) {
            counter++;
        }
        assertEquals(counter, 2);


    }

}
