package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.Created;
import com.tinkerpop.frames.domain.relations.CreatedBy;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedElementTest extends TestCase {

    public void testGettingProperties() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        assertEquals(marko.getName(), "marko");
        assertEquals(marko.getAge(), new Integer(29));

        Project lop = manager.frame(graph.getVertex(3), Project.class);
        assertEquals(lop.getName(), "lop");
        assertEquals(lop.getLanguage(), "java");

        Created markoCreatedLop = manager.frame(graph.getEdge(9), Direction.STANDARD, Created.class);
        assertEquals(markoCreatedLop.getWeight(), 0.4f);

        CreatedBy lopCreatedByMarko = manager.frame(graph.getEdge(9), Direction.INVERSE, CreatedBy.class);
        assertEquals(lopCreatedByMarko.getWeight(), 0.4f);

        Person temp = manager.frame(graph.addVertex(null), Person.class);
        assertNull(temp.getName());
        assertNull(temp.getAge());

    }

    public void testSettingProperties() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        assertEquals(marko.getName(), "marko");
        marko.setName("pavel");
        assertEquals(marko.getName(), "pavel");
        assertEquals(marko.getAge(), new Integer(29));
        marko.setAge(31);
        assertEquals(marko.getAge(), new Integer(31));

        Created markoCreatedLop = manager.frame(graph.getEdge(9), Direction.STANDARD, Created.class);
        assertEquals(markoCreatedLop.getWeight(), 0.4f);
        markoCreatedLop.setWeight(99.0f);
        assertEquals(markoCreatedLop.getWeight(), 99.0f);
    }

    public void testRemoveProperties() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        assertEquals(marko.getAge(), new Integer(29));
        marko.removeAge();
        assertNull(marko.getAge());
    }

    public void testToString() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        Person marko = manager.frame(graph.getVertex(1), Person.class);
        System.out.println(marko);

        Created markoCreatedLop = manager.frame(graph.getEdge(9), Direction.STANDARD, Created.class);
        System.out.println(markoCreatedLop);
    }

    public void testEquality() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);

        assertEquals(manager.frame(graph.getVertex(1), Person.class), manager.frame(graph.getVertex(1), Person.class));

    }
}
