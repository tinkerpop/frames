package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.Created;
import com.tinkerpop.frames.domain.relations.CreatedBy;
import com.tinkerpop.frames.domain.relations.Knows;
import junit.framework.TestCase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class FramedVertexTest extends TestCase {
    private Graph graph;
    private FramesManager manager;

    public void setUp() {
        graph = TinkerGraphFactory.createTinkerGraph();
        manager = new FramesManager(graph);
    }

    public void tearDown() {
    }

    public void testGettingRelations() {
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
        for (Person person : ripple.getCreatedByPeople()) {
            counter++;
            assertEquals(person.getName(), "josh");
        }
        assertEquals(counter, 1);

    }

    public void testSettingRelations() {
        int counter;

        Person josh = manager.frame(graph.getVertex(4), Person.class);
        Person marko = manager.frame(graph.getVertex(1), Person.class);
        Person peter = manager.frame(graph.getVertex(6), Person.class);

        assertEquals(josh.getKnowsPeople().size(), 0);

        Collection<Person> knows = new LinkedList<Person>();
        knows.add(peter);
        knows.add(marko);
        josh.setKnowsPeople(knows);

        counter = 0;
        for (Person person : josh.getKnowsPeople()) {
            counter++;
            assertTrue(person.getName().equals("marko") || person.getName().equals("peter"));
        }
        assertEquals(counter, 2);

        knows.clear();
        knows.add(josh);
        josh.setKnowsPeople(knows);
        counter = 0;
        for (Person person : josh.getKnowsPeople()) {
            counter++;
            assertTrue(person.getName().equals("josh"));
        }
        assertEquals(counter, 1);
    }

    public void testGettingAndSettingFunctionalRelations() {
        Person josh = manager.frame(graph.getVertex(4), Person.class);

        Project rdfAgents = manager.frame(graph.addVertex(null), Project.class);
        Project tinkerNotes = manager.frame(graph.addVertex(null), Project.class);

        assertNull(josh.getLatestProject());

        josh.setLatestProject(rdfAgents);
        assertEquals(rdfAgents.asVertex().getId(), josh.getLatestProject().asVertex().getId());

        josh.setLatestProject(tinkerNotes);
        assertEquals(tinkerNotes.asVertex().getId(), josh.getLatestProject().asVertex().getId());

        josh.setLatestProject(null);
        assertNull(josh.getLatestProject());

        // It's safe to set an already-null object to null.
        josh.setLatestProject(null);
        assertNull(josh.getLatestProject());
    }

    public void testImproperSettingRelations() {
        Person josh = manager.frame(graph.getVertex(4), Person.class);
        boolean good = false;
        try {
            josh.setKnowsPeople(null);
        } catch (NullPointerException e) {
            good = true;
        }
        assertTrue(good);
    }

    public void testGettingAdjacencies() {
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

    public void testAddingAdjacencies() {
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

    public void testAddingRelations() {
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

    public void testRemoveAdjacencies() {
        Person marko = manager.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        List<Knows> toRemove = new LinkedList<Knows>();
        for (Knows knows : marko.getKnows()) {
            counter++;
            if (knows.getRange().getName().equals("josh")) {
                toRemove.add(knows);
            }
        }
        assertEquals(counter, 2);
        for (Knows knows : toRemove) {
            marko.removeKnows(knows);
        }
        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertEquals(knows.getRange().getName(), "vadas");
        }
        assertEquals(counter, 1);


        Project lop = manager.frame(graph.getVertex(3), Project.class);
        counter = 0;
        List<CreatedBy> toRemove2 = new LinkedList<CreatedBy>();
        for (CreatedBy createdBy : lop.getCreatedBy()) {
            counter++;
            toRemove2.add(createdBy);
        }
        assertEquals(counter, 3);
        for (CreatedBy createdBy : toRemove2) {
            lop.removeCreatedBy(createdBy);
        }
        counter = 0;
        for (CreatedBy createdBy : lop.getCreatedBy()) {
            counter++;

        }
        assertEquals(counter, 0);
    }

    public void testRemovingRelations() {
        Person marko = manager.frame(graph.getVertex(1), Person.class);
        Person vadas = manager.frame(graph.getVertex(2), Person.class);
        Project lop = manager.frame(graph.getVertex(3), Project.class);

        marko.removeKnowsPerson(vadas);
        int counter = 0;
        for (Edge edge : graph.getVertex(1).getEdges(Direction.OUT, "knows")) {
            if (edge.getLabel().equals("knows")) {
                counter++;
                assertEquals(edge.getInVertex().getProperty("name"), "josh");
            }
        }
        assertEquals(counter, 1);
        counter = 0;
        for (Person person : marko.getKnowsPeople()) {
            counter++;
            assertEquals(person.getName(), "josh");
        }
        assertEquals(counter, 1);

        lop.removeCreatedByPerson(marko);
        counter = 0;
        for (Edge edge : graph.getVertex(3).getEdges(Direction.IN, "created")) {
            if (edge.getLabel().equals("created")) {
                counter++;
                assertTrue(edge.getOutVertex().getProperty("name").equals("josh") || edge.getOutVertex().getProperty("name").equals("peter"));
            }
        }
        assertEquals(counter, 2);
        counter = 0;
        for (Person person : lop.getCreatedByPeople()) {
            counter++;
            assertTrue(person.getName().equals("josh") || person.getName().equals("peter"));
        }
        assertEquals(counter, 2);

    }

    public void testGetGremlinGroovy() {
        Person marko = manager.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Person coCreator : marko.getCoCreators()) {
            counter++;
            assertTrue(coCreator.getName().equals("josh") || coCreator.getName().equals("peter"));
        }
        assertEquals(counter, 2);
    }
}
