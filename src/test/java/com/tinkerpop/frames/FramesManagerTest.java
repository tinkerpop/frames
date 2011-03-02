package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Index;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.Knows;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramesManagerTest extends TestCase {

    public void testFrameVertices() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        int counter = 0;
        for (Person person : manager.frameVertices(Index.VERTICES, "name", "marko", Person.class)) {
            counter++;
            assertEquals(person.getName(), "marko");
        }
        assertEquals(counter, 1);

        counter = 0;
        for (Project project : manager.frameVertices(Index.VERTICES, "lang", "java", Project.class)) {
            counter++;
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
        assertEquals(counter, 2);

    }

    public void testFrameEdges() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        int counter = 0;
        for (Knows knows : manager.frameEdges(Index.EDGES, AutomaticIndex.LABEL, "knows", Knows.class, Direction.STANDARD)) {
            counter++;
            assertEquals(knows.getDomain().getName(), "marko");
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas"));
        }
        assertEquals(counter, 2);
    }
}
