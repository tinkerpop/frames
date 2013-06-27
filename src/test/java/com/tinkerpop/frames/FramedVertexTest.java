package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.NamedObject;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class FramedVertexTest {
    private Graph graph = TinkerGraphFactory.createTinkerGraph();
    private FramedGraph<Graph> framedGraph = new FramedGraphFactory(new GremlinGroovyModule()).create(graph);

    @Test
    public void testGettingAdjacencies() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Project project : marko.getCreatedProjects()) {
            counter++;
            assertEquals("lop", project.getName());
        }
        assertEquals(1, counter);

        counter = 0;
        for (Person person : marko.getKnowsPeople()) {
            counter++;
            assertTrue(person.getName().equals("josh") || person.getName().equals("vadas"));
        }
        assertEquals(2, counter);

        counter = 0;
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        for (Person person : ripple.getCreatedByPeople()) {
            counter++;
            assertEquals("josh", person.getName());
        }
        assertEquals(1, counter);

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
        assertEquals(0, counter);

        List<Person> knows = new ArrayList<Person>();
        knows.add(peter);
        knows.add(marko);
        josh.setKnowsPeople(knows);

        counter = 0;
        for (Person person : josh.getKnowsPeople()) {
            counter++;
            assertTrue(person.getName().equals("marko") || person.getName().equals("peter"));
        }
        assertEquals(2, counter);

        knows.clear();
        knows.add(josh);
        josh.setKnowsPeople(knows);
        counter = 0;
        for (Person person : josh.getKnowsPeople()) {
            counter++;
            assertTrue(person.getName().equals("josh"));
        }
        assertEquals(1, counter);
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
        for (CreatedInfo created : marko.getCreatedInfo()) {
            counter++;
            assertEquals("lop", created.getProject().getName());
            assertEquals(0.4f, created.getWeight(), 0.01f);
        }
        assertEquals(1, counter);

        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getTerminal().getName().equals("josh") || knows.getTerminal().getName().equals("vadas"));
        }
        assertEquals(2, counter);

        counter = 0;
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        for (CreatedInfo createdBy : ripple.getCreatedInfo()) {
            counter++;
            assertEquals("josh", createdBy.getPerson().getName());
            assertEquals(1.0f, createdBy.getWeight(), 0.01f);
        }
        assertEquals(1, counter);
    }

    /**
     * Uses deprecated Domain/Range annotations
     */
    @Test
    public void testGettingIncidencesDeprecated() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertEquals("lop", created.getRange().getName());
            assertEquals(0.4f, created.getWeight(), 0.01f);
        }
        assertEquals(1, counter);

        counter = 0;
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        for (CreatedBy createdBy : ripple.getCreatedBy()) {
            counter++;
            assertEquals("josh", createdBy.getRange().getName());
            assertEquals(1.0f, createdBy.getWeight(), 0.01f);
        }
        assertEquals(1, counter);
    }
    
    @Test
    public void testAddingIncidences() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);

        CreatedInfo markoCreatedRipple = marko.addCreatedInfo(ripple);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertTrue(created.getRange().getName().equals("lop") || created.getRange().getName().equals("ripple"));
        }
        assertEquals(2, counter);
        assertNull(markoCreatedRipple.getWeight());
        markoCreatedRipple.setWeight(0.0f);
        assertEquals(0.0f, markoCreatedRipple.getWeight(), 0.01f);

        Knows markoKnowsPeter = marko.addKnows(peter);
        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getTerminal().getName().equals("josh") || knows.getTerminal().getName().equals("vadas") || knows.getTerminal().getName().equals("peter"));
        }
        assertEquals(3, counter);
        assertNull(markoKnowsPeter.getWeight());
        markoKnowsPeter.setWeight(1.0f);
        assertEquals(1.0f, markoKnowsPeter.getWeight(), 0.01f);
    }


    /**
     * Uses deprecated Domain/Range annotations
     */
    @Test
    public void testAddingIncidencesDeprecated() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);

        Created markoCreatedRipple = marko.addCreated(ripple);
        int counter = 0;
        for (Created created : marko.getCreated()) {
            counter++;
            assertTrue(created.getRange().getName().equals("lop") || created.getRange().getName().equals("ripple"));
        }
        assertEquals(2, counter);
        assertNull(markoCreatedRipple.getWeight());
        markoCreatedRipple.setWeight(0.0f);
        assertEquals(0.0f, markoCreatedRipple.getWeight(), 0.01f);
    }

    @Test
    public void testAddingAdjacencies() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project ripple = framedGraph.frame(graph.getVertex(5), Project.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);

        marko.addKnowsPerson(peter);
        Person bryn = marko.addKnowsNewPerson();
        bryn.setName("bryn");

        int counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertTrue(knows.getTerminal().getName().equals("josh") || knows.getTerminal().getName().equals("vadas") || knows.getTerminal().getName().equals("peter") || knows.getTerminal().getName().equals("bryn"));
        }
        assertEquals(4, counter);

        marko.addCreatedProject(ripple);
        counter = 0;
        for (Project project : marko.getCreatedProjects()) {
            counter++;
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
        assertEquals(2, counter);


    }
    
    @Test
    public void testRemoveIncidences() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        List<Knows> toRemove = new ArrayList<Knows>();
        for (Knows knows : marko.getKnows()) {
            counter++;
            if (knows.getTerminal().getName().equals("josh")) {
                toRemove.add(knows);
            }
        }
        assertEquals(2, counter);
        for (Knows knows : toRemove) {
            marko.removeKnows(knows);
        }
        counter = 0;
        for (Knows knows : marko.getKnows()) {
            counter++;
            assertEquals("vadas", knows.getTerminal().getName());
        }
        assertEquals(1, counter);

        Project lop = framedGraph.frame(graph.getVertex(3), Project.class);
        counter = 0;
        List<CreatedInfo> toRemove2 = new ArrayList<CreatedInfo>();
        for (CreatedInfo createdBy : lop.getCreatedInfo()) {
            counter++;
            toRemove2.add(createdBy);
        }
        assertEquals(3, counter);
        for (CreatedInfo createdBy : toRemove2) {
            lop.removeCreatedInfo(createdBy);
        }
        counter = 0;
        for (CreatedInfo createdInfo : lop.getCreatedInfo()) {
            counter++;
        }
        assertEquals(0, counter);
    }


    /**
     * Uses deprecated Domain/Range annotations
     */
    @Test
    public void testRemoveIncidencesDeprecated() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project lop = framedGraph.frame(graph.getVertex(3), Project.class);
        int counter = 0;
        List<CreatedBy> toRemove2 = new ArrayList<CreatedBy>();
        for (CreatedBy createdBy : lop.getCreatedBy()) {
            counter++;
            toRemove2.add(createdBy);
        }
        assertEquals(3, counter);
        for (CreatedBy createdBy : toRemove2) {
            lop.removeCreatedBy(createdBy);
        }
        counter = 0;
        for (CreatedBy createdBy : lop.getCreatedBy()) {
            counter++;

        }
        assertEquals(0, counter);
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
                assertEquals("josh", edge.getVertex(Direction.IN).getProperty("name"));
            }
        }
        assertEquals(1, counter);
        counter = 0;
        for (Person person : marko.getKnowsPeople()) {
            counter++;
            assertEquals("josh", person.getName());
        }
        assertEquals(1, counter);

        lop.removeCreatedByPerson(marko);
        counter = 0;
        for (Edge edge : graph.getVertex(3).getEdges(Direction.IN, "created")) {
            if (edge.getLabel().equals("created")) {
                counter++;
                assertTrue(edge.getVertex(Direction.OUT).getProperty("name").equals("josh")
                        || edge.getVertex(Direction.OUT).getProperty("name").equals("peter"));
            }
        }
        assertEquals(2, counter);
        counter = 0;
        for (Person person : lop.getCreatedByPeople()) {
            counter++;
            assertTrue(person.getName().equals("josh") || person.getName().equals("peter"));
        }
        assertEquals(2, counter);

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
        assertEquals(marko, namedMarko);
        assertEquals(marko.asVertex(), namedMarko.asVertex());
        assertNotSame(marko.asVertex(), vadas.asVertex());
    }

    @Test
    public void testGetGremlinGroovy() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        int counter = 0;
        for (Person coCreator : marko.getCoCreators()) {
            counter++;
            assertTrue(coCreator.getName().equals("josh") || coCreator.getName().equals("peter"));
        }
        assertEquals(2, counter);

        assertEquals("aStringProperty", marko.getAStringProperty());
        Iterator<String> itty = marko.getListOfStrings().iterator();
        assertEquals("a", itty.next());
        assertEquals("b", itty.next());
        assertEquals("c", itty.next());
    }

    @Test
    public void testGetGremlinGroovySingleItem() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person coCreator = marko.getRandomCoCreators();
        assertTrue(coCreator.getName().equals("josh") || coCreator.getName().equals("peter"));
    }

    @Test
    public void testGetGremlinGroovyParameters() {
        Person josh = framedGraph.frame(graph.getVertex(4), Person.class);
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);
        Project project = framedGraph.frame(graph.getVertex(6), Project.class);
        
        Person coCreator = marko.getCoCreatorOfAge(32);
        assertEquals(josh, coCreator);
        coCreator = marko.getCoCreatorOfAge(35);
        assertEquals(peter, coCreator);
        
        Iterable<Person> known = marko.getKnownRootedFromParam(josh);
        assertEquals(marko, known.iterator().next());
    }

    @Test
    public void testMapReturnType() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Map<Person, Long> coauthors = marko.getRankedCoauthors();

        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);
        assertEquals(1, coauthors.get(peter).longValue());

        Person josh = framedGraph.frame(graph.getVertex(4), Person.class);
        assertEquals(1, coauthors.get(josh).longValue());

        assertEquals(2, coauthors.size());
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

    @Test
    public void testDeprecatedKnowsPeople() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        for (Person person : marko.getDeprecatedKnowsPeople()) {
            assertTrue(person.getName().equals("vadas") || person.getName().equals("josh"));
        }
    }

    @Test
    public void testFramingInterfaces() {
        StandalonePerson marko = framedGraph.frame(graph.getVertex(1), StandalonePerson.class);
        assertTrue(marko instanceof VertexFrame);
        for (Knows knows : marko.getKnows()) {
            assertTrue(knows instanceof EdgeFrame);
        }
    }

    public static interface StandalonePerson {

        @Incidence(label = "knows")
        public Iterable<Knows> getKnows();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddAdjacencyBothError() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);
        marko.addKnowsPersonDirectionBothError(peter);
        
    }
    
    
    @Test(expected=UnsupportedOperationException.class)
    public void testSetAdjacencyBothError() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Person peter = framedGraph.frame(graph.getVertex(6), Person.class);
        marko.setKnowsPersonDirectionBothError(peter);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testAddIncidenceBothError() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project rdfAgents = framedGraph.frame(graph.addVertex(null), Project.class);
        marko.addCreatedDirectionBothError(rdfAgents);
    }

    @Test
    public void testAddAdjacencyIn() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project rdfAgents = framedGraph.addVertex(null, Project.class);
        rdfAgents.addCreatedByPersonAdjacency(marko);
        assertTrue(rdfAgents.getCreatedByPeople().iterator().hasNext());
        assertEquals(marko, rdfAgents.getCreatedByPeople().iterator().next());
    }

    @Test
    public void testAddIncidenceIn() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project rdfAgents = framedGraph.addVertex(null, Project.class);
        CreatedInfo createdInfo = rdfAgents.addCreatedByPersonInfo(marko);
           
        assertEquals(marko, createdInfo.getPerson());
        assertEquals(rdfAgents, createdInfo.getProject());
        assertTrue(rdfAgents.getCreatedByPeople().iterator().hasNext());
        assertEquals(marko, rdfAgents.getCreatedByPeople().iterator().next());
    }

    /**
     * Use deprecated Domain/Range annotations on edge
     */
    @Test
    public void testAddIncidenceInDeprecated() {
        Person marko = framedGraph.frame(graph.getVertex(1), Person.class);
        Project rdfAgents = framedGraph.addVertex(null, Project.class);
        CreatedBy createdBy = rdfAgents.addCreatedByPersonIncidence(marko);
           
        assertEquals(marko, createdBy.getRange());
        assertEquals(rdfAgents, createdBy.getDomain());
        assertTrue(rdfAgents.getCreatedByPeople().iterator().hasNext());
        assertEquals(marko, rdfAgents.getCreatedByPeople().iterator().next());
    }
}

