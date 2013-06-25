package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.domain.incidences.WeightedEdge;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedEdgeTest extends TestCase {

    public void testGettingInitialAndTerminal() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Person marko = framedGraph.getVertex(1, Person.class);
        Person vadas = framedGraph.getVertex(2, Person.class);
        Knows knows = framedGraph.getEdge(7, Knows.class);
        assertEquals(marko, knows.getInitial());
        assertEquals(vadas, knows.getTerminal());

        Project lop = framedGraph.getVertex(3, Project.class);
        CreatedInfo created = lop.getCreatedInfo().iterator().next();
        assertEquals(lop, created.getProject());
        assertEquals(marko, created.getPerson());
        
        created = marko.getCreatedInfo().iterator().next();
        assertEquals(lop, created.getProject());
        assertEquals(marko, created.getPerson());
    }
    
    public void testGettingDomainAndRange() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Person marko = framedGraph.getVertex(1, Person.class);
        Project lop = framedGraph.getVertex(3, Project.class);
        CreatedBy createdBy = lop.getCreatedBy().iterator().next();
        assertEquals(lop, createdBy.getDomain());
        assertEquals(marko, createdBy.getRange());
        
        Created created = marko.getCreated().iterator().next();
        //Please note: the below results are actually incorrect: the domain and range are incorrectly tagged
        // in Created for usage with @Incidence. I'm not going to fix that in the test-cases as Domain and
        // Range are deprecated now. The incorrect annotations probable show better than anything that
        // the now deprecated annotations are quite confusing:
        assertEquals(lop, created.getRange()); //range actually returns a Person, not a Project...
        assertEquals(marko, created.getDomain()); //domain actually returns a Project, not a Person...
    }

    /**
     * Uses deprecated Domain/range annotations
     */
    public void testGettingIterableDeprecated() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Iterator<Edge> edges = framedGraph.getEdges("weight", 0.4f).iterator();
        Iterator<Created> createds = framedGraph.getEdges("weight", 0.4f, Direction.OUT, Created.class).iterator();

        int counter = 0;
        while (edges.hasNext()) {
            assertEquals(edges.next(), createds.next().asEdge());
            counter++;
        }
        assertEquals(counter, 2);
        assertFalse(edges.hasNext());
        assertFalse(createds.hasNext());

    }
    
    public void testGettingIterable() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Iterator<Edge> edges = framedGraph.getEdges("weight", 0.4f).iterator();
        Iterator<CreatedInfo> createds = framedGraph.getEdges("weight", 0.4f, CreatedInfo.class).iterator();

        int counter = 0;
        while (edges.hasNext()) {
            assertEquals(edges.next(), createds.next().asEdge());
            counter++;
        }
        assertEquals(counter, 2);
        assertFalse(edges.hasNext());
        assertFalse(createds.hasNext());

    }

    /**
     * Uses deprecated Domain/Range annotations
     */
    public void testEqualityOfIterableMethodsDeprecated() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Iterator<Created> createds1 = framedGraph.frameEdges(framedGraph.getEdges("weight", 0.4f), Direction.OUT, Created.class).iterator();
        Iterator<Created> createds2 = framedGraph.getEdges("weight", 0.4f, Direction.OUT, Created.class).iterator();

        int counter = 0;
        while (createds1.hasNext()) {
            assertEquals(createds1.next(), createds2.next());
            counter++;
        }
        assertEquals(counter, 2);
        assertFalse(createds1.hasNext());
        assertFalse(createds2.hasNext());

    }
    
    public void testEqualityOfIterableMethods() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Iterator<CreatedInfo> createds1 = framedGraph.frameEdges(framedGraph.getEdges("weight", 0.4f), CreatedInfo.class).iterator();
        Iterator<CreatedInfo> createds2 = framedGraph.getEdges("weight", 0.4f, CreatedInfo.class).iterator();

        int counter = 0;
        while (createds1.hasNext()) {
            assertEquals(createds1.next(), createds2.next());
            counter++;
        }
        assertEquals(counter, 2);
        assertFalse(createds1.hasNext());
        assertFalse(createds2.hasNext());

    }


    public void testEquality() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Person marko = framedGraph.getVertex(1, Person.class);
        Person vadas = framedGraph.getVertex(2, Person.class);
        
        //Deprecated Domain/Range:
        Created created = marko.getCreated().iterator().next();
        WeightedEdge weightedEdge = framedGraph.frame(created.asEdge(), Direction.OUT, WeightedEdge.class);
        assertEquals(created, weightedEdge);
        
        //Initial/Terminal:
        CreatedInfo createdInfo = marko.getCreatedInfo().iterator().next();
        assertEquals(createdInfo, weightedEdge);
    }
}
