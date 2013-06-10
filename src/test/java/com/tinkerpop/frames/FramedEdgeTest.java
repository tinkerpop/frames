package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.domain.incidences.WeightedEdge;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedEdgeTest extends TestCase {

    public void testGettingDomainAndRange() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Person marko = framedGraph.getVertex(1, Person.class);
        Person vadas = framedGraph.getVertex(2, Person.class);
        Knows knows = framedGraph.getEdge(7, Direction.OUT, Knows.class);
        assertEquals(marko, knows.getDomain());
        assertEquals(vadas, knows.getRange());

        Project lop = framedGraph.getVertex(3, Project.class);
        CreatedBy createdBy = lop.getCreatedBy().iterator().next();
        assertEquals(lop, createdBy.getDomain());
        assertEquals(marko, createdBy.getRange());
    }

    public void testGettingIterable() {
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

    public void testEqualityOfIterableMethods() {
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

    public void testEquality() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraphFactory().create(graph);

        Person marko = framedGraph.getVertex(1, Person.class);
        Person vadas = framedGraph.getVertex(2, Person.class);
        Created created = marko.getCreated().iterator().next();
        WeightedEdge weightedEdge = framedGraph.frame(created.asEdge(), Direction.OUT, WeightedEdge.class);

        assertEquals(created, weightedEdge);
    }
}
