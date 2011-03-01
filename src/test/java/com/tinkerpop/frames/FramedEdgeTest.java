package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.CreatedBy;
import com.tinkerpop.frames.domain.relations.Knows;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedEdgeTest extends TestCase {

    public void testGettingDomainAndRange() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        Person vadas = manager.frame(graph.getVertex(2), Person.class);
        Knows knows = manager.frame(graph.getEdge(7), Knows.class, Relation.Direction.STANDARD);
        assertEquals(marko, knows.getDomain());
        assertEquals(vadas, knows.getRange());

        Project lop = manager.frame(graph.getVertex(3), Project.class);
        CreatedBy createdBy = lop.getCreatedBy().iterator().next();
        assertEquals(lop, createdBy.getDomain());
        assertEquals(marko, createdBy.getRange());
    }
}
