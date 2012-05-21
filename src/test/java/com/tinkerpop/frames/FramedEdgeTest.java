package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.Knows;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedEdgeTest extends TestCase {

    public void testGettingDomainAndRange() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);

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
}
