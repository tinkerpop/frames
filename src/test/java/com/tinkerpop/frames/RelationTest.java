package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.example.Person;
import com.tinkerpop.frames.example.Project;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RelationTest extends TestCase {

    public void testGettingRelations() throws Exception {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FrameManager manager = new FrameManager(graph);

        Person person = manager.load(Person.class, 1);
        assertEquals(person.getKnows().size(), 2);
        assertEquals(person.getCreated().size(), 1);
        assertEquals(person.getKnows().size(), 2);
        assertEquals(person.getCreated().size(), 1);

        person = manager.load(Person.class, 6);
        assertEquals(person.getKnows().size(), 0);
        assertEquals(person.getCreated().size(), 1);
        assertEquals(person.getKnows().size(), 0);
        assertEquals(person.getCreated().size(), 1);

        Project project = manager.load(Project.class, 3);
        assertEquals(project.getCreatedBy().size(), 3);
        assertEquals(project.getCreatedBy().size(), 3);
    }

    public void testAddingRelations() throws Exception {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FrameManager manager = new FrameManager(graph);

        Person marko = manager.load(Person.class, 1);
        Person peter = manager.load(Person.class, 6);
        Project ripple = manager.load(Project.class, 5);

        assertEquals(marko.getKnows().size(), 2);
        marko.addKnows(peter);
        assertEquals(marko.getKnows().size(), 3);
        for (Person person : marko.getKnows()) {
            assertTrue(person.getName().equals("vadas") || person.getName().equals("josh") || person.getName().equals("peter"));
        }

        assertEquals(marko.getCreated().size(), 1);
        assertEquals(ripple.getCreatedBy().size(), 1);
        marko.addCreated(ripple);
        assertEquals(marko.getCreated().size(), 2);
        assertEquals(ripple.getCreatedBy().size(), 2);
        for (Project project : marko.getCreated()) {
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
    }
}
