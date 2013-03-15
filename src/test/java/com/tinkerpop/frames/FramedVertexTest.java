package com.tinkerpop.frames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.NamedObject;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.Knows;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class FramedVertexTest {
    private Graph graph = TinkerGraphFactory.createTinkerGraph();
    private FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);

    @Test
    public void testGettingAdjacencies() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
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
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        for (Person person : ripple.getCreatedByPeople()) {
            counter++;
            assertEquals(person.getName(), "josh");
        }
        assertEquals(counter, 1);

    }

    @Test
    public void testSettingAdjacencies() {

        Person josh = framedGraph.frame(graph.getVertex(4), Person.class);
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);

        int counter = 0;
        for (Person p : josh.getKnowsPeople()) {
            counter++;
        }
        assertEquals(counter, 0);

        List<Person> knows = new ArrayList<Person>();
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

    @Test
    public void testGettingAndSettingFunctionalAdjacencies() {
        Person josh = framedGraph.frame(graph.getVertex(4), Person.class);

        Project rdfAgents = framedGraph.frame(graph.addVertex(null), Project.class);
        Project tinkerNotes = framedGraph.frame(graph.addVertex(null), Project.class);

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

    @Test
    public void testImproperSettingAdjacencies() {
        Person josh = framedGraph.frame(graph.getVertex(4), Person.class);
        boolean good = false;
        try {
            josh.setKnowsPeople(null);
        } catch (NullPointerException e) {
            good = true;
        }
        assertTrue(good);
    }

    @Test
    public void testGettingIncidences() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertEquals(created.getRange().getName(), "lop");
            assertEquals(created.getWeight(), 0.4f, 0.1f);
        }
        assertEquals(counter, 1);

        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas"));
        }
        assertEquals(counter, 2);

        counter = 0;
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        for (CreatedBy createdBy : ripple.getCreatedBy()) {
            counter++;
            assertEquals(createdBy.getRange().getName(), "josh");
            assertEquals(createdBy.getWeight(), 1.0f, 0.1f);
        }
        assertEquals(counter, 1);
    }

    @Test
    public void testAddingIncidences() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);

        Created markoCreatedRipple = marko.addCreated(ripple);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertTrue(created.getRange().getName().equals("lop") || created.getRange().getName().equals("ripple"));
        }
        assertEquals(counter, 2);
        assertNull(markoCreatedRipple.getWeight());
        markoCreatedRipple.setWeight(0.0f);
        assertEquals(markoCreatedRipple.getWeight(), 0.0f, 0.1f);

        Knows markoKnowsPeter = marko.addKnows(peter);
        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getRange().getName().equals("josh") || knows.getRange().getName().equals("vadas") || knows.getRange().getName().equals("peter"));
        }
        assertEquals(counter, 3);
        assertNull(markoKnowsPeter.getWeight());
        markoKnowsPeter.setWeight(1.0f);
        assertEquals(markoKnowsPeter.getWeight(), 1.0f, 0.1f);
    }

    @Test
    public void testAddingAdjacencies() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);

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

    @Test
    public void testRemoveIncidences() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        List<Knows> toRemove = new ArrayList<Knows>();
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


        Project lop = framedGraph.frame(graph.getVertex(3), Project.class);
        counter = 0;
        List<CreatedBy> toRemove2 = new ArrayList<CreatedBy>();
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

    @Test
    public void testRemovingAdjacencies() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person vadas = framedGraph.frame(graph.getVertex(2), Person.class);
        Project lop = framedGraph.frame(graph.getVertex(3), Project.class);

        marko.removeKnowsPerson(vadas);
        int counter = 0;
        for (Edge edge : graph.getVertex(1).getEdges(Direction.OUT, "knows")) {
            if (edge.getLabel().equals("knows")) {
                counter++;
                assertEquals(edge.getVertex(Direction.IN).getProperty("name"), "josh");
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
                assertTrue(edge.getVertex(Direction.OUT).getProperty("name").equals("josh") || edge.getVertex(Direction.OUT).getProperty("name").equals("peter"));
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

    @Test
    public void testVertexEquality() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person vadas = framedGraph.frame(graph.getVertex(2), Person.class);
        NamedObject namedMarko = framedGraph.frame(graph.getVertex(1), NamedObject.class);
        NamedObject namedVadas = framedGraph.frame(graph.getVertex(2), NamedObject.class);
        assertTrue(marko.equals(marko));
        assertFalse(marko.equals(vadas));
        // The standard equals method will not consider different
        // framed interfaces with the same underlying vertex as equal
        assertEquals(marko.asVertex(), namedMarko.asVertex());
        assertFalse(marko.equals(namedMarko));
        assertTrue(marko.asVertex().equals(namedMarko.asVertex()));
        assertFalse(marko.asVertex().equals(vadas.asVertex()));
        // The equalsVertex method should...
        assertTrue(marko.equalsVertex(namedMarko));
        assertFalse(marko.equalsVertex(namedVadas));
        // Passing a raw vertex should work as well
        assertTrue(marko.equalsVertex(namedMarko.asVertex()));
        assertFalse(marko.equalsVertex(namedVadas.asVertex()));
    }

    @Test
    public void testGetGremlinGroovy() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Person coCreator : marko.getCoCreators()) {
            counter++;
            assertTrue(coCreator.getName().equals("josh") || coCreator.getName().equals("peter"));
        }
        assertEquals(counter, 2);

        assertEquals(marko.getAStringProperty(), "aStringProperty");
        Iterator<String> itty = marko.getListOfStrings().iterator();
        assertEquals(itty.next(), "a");
        assertEquals(itty.next(), "b");
        assertEquals(itty.next(), "c");
    }

    @Test
    public void testGetGremlinGroovySingleItem() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person coCreator = marko.getRandomCoCreators();
        assertTrue(coCreator.getName().equals("josh") || coCreator.getName().equals("peter"));
    }

    @Test
    public void testGetGremlinGroovyParameters() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person coCreator = marko.getCoCreatorOfAge(32);
        assertTrue(coCreator.getName().equals("josh"));
        coCreator = marko.getCoCreatorOfAge(35);
        assertTrue(coCreator.getName().equals("peter"));
    }

    @Test
    public void testMapReturnType() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Map<Person, Long> coauthors = marko.getRankedCoauthors();

        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);
        assertEquals(coauthors.get(peter), new Long(1));

        Person josh = framedGraph.frame(graph.getVertex(4), Person.class);
        assertEquals(coauthors.get(josh), new Long(1));

        assertEquals(coauthors.size(), 2);
    }

    
    @Test
    public void testBooleanGetMethods() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        marko.setBoolean(true);
        assertTrue(marko.isBoolean());
        assertTrue(marko.isBooleanPrimitive());
        assertTrue(marko.canBoolean());
        assertTrue(marko.canBooleanPrimitive());
    }
    
}
