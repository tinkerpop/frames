package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.Created;
import com.tinkerpop.frames.domain.relations.CreatedBy;
import com.tinkerpop.frames.domain.relations.Knows;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedVertexTest extends TestCase {

    public void testGettingFullRelations() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Project project : marko.getCreatedProjects()) {
            counter++;
            assertEquals(project.getName(), "lop");
        }
        assertEquals(counter, 1);

        counter = 0;
        for (Person person : marko.getKnowsPeople()) {
            counter++;
            assertTrue(person.getName().equals("josh") || person.getName().equals("vadas"));
        }
        assertEquals(counter, 2);

        counter = 0;
        Project ripple = manager.frame(graph.getVertex(5), Project.class);
        for (Person person : ripple.getCreatedByPerson()) {
            counter++;
            assertEquals(person.getName(), "josh");
        }
        assertEquals(counter, 1);

    }

    public void testGettingHalfRelations() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertEquals(created.getRange().getName(), "lop");
            assertEquals(created.getWeight(), 0.4f);
        }
        assertEquals(counter, 1);

        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas"));
        }
        assertEquals(counter, 2);

        counter = 0;
        Project ripple = manager.frame(graph.getVertex(5), Project.class);
        for (CreatedBy createdBy : ripple.getCreatedBy()) {
            counter++;
            assertEquals(createdBy.getRange().getName(), "josh");
            assertEquals(createdBy.getWeight(), 1.0f);
        }
        assertEquals(counter, 1);
    }

    public void testAddingHalfRelations() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        Project ripple = manager.frame(graph.getVertex(5), Project.class);
        Person peter = manager.frame(graph.getVertex(6), Person.class);

        Created markoCreatedRipple = marko.addCreated(ripple);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertTrue(created.getRange().getName().equals("lop") || created.getRange().getName().equals("ripple"));
        }
        assertEquals(counter, 2);
        assertNull(markoCreatedRipple.getWeight());
        markoCreatedRipple.setWeight(0.0f);
        assertEquals(markoCreatedRipple.getWeight(), 0.0f);

        Knows markoKnowsPeter = marko.addKnows(peter);
        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas") || knows.getRange().getName().equals("peter"));
        }
        assertEquals(counter, 3);
        assertNull(markoKnowsPeter.getWeight());
        markoKnowsPeter.setWeight(1.0f);
        assertEquals(markoKnowsPeter.getWeight(), 1.0f);
    }

    public void testAddingFullRelations() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        Project ripple = manager.frame(graph.getVertex(5), Project.class);
        Person peter = manager.frame(graph.getVertex(6), Person.class);

        marko.addKnowsPerson(peter);
        int counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas") || knows.getRange().getName().equals("peter"));
        }
        assertEquals(counter, 3);

        marko.addCreatedProject(ripple);
        counter = 0;
        for (Project project : marko.getCreatedProjects()) {
            counter++;
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
        assertEquals(counter, 2);
    }
}
