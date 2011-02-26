package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.example.Person;
import com.tinkerpop.frames.example.Project;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class PropertyTest extends TestCase {

    public void testGettingProperties() throws Exception {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FrameManager manager = new FrameManager(graph);
        Person person = manager.load(Person.class, 1);
        assertEquals(person.getName(), "marko");
        assertEquals(person.getAge(), 29);
        assertEquals(person.getVertex(), graph.getVertex(1));

        person = manager.load(Person.class, 6);
        assertEquals(person.getName(), "peter");
        assertEquals(person.getAge(), 35);
        assertEquals(person.getVertex(), graph.getVertex(6));

        Project project = manager.load(Project.class, 3);
        assertNull(project.getLanguage());
        assertEquals(project.getName(), "lop");
        assertEquals(project.getVertex(), graph.getVertex(3));

    }

    public void testSettingProperties() throws Exception {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FrameManager manager = new FrameManager(graph);

        Person person = manager.load(Person.class, 1);
        assertEquals(person.getName(), "marko");
        assertEquals(person.getAge(), 29);
        assertEquals(graph.getVertex(1).getProperty("name"), "marko");
        assertEquals(graph.getVertex(1).getProperty("age"), 29);

        person.setName("jen");
        person.setAge(28);
        manager.save(person);
        assertEquals(person.getName(), "jen");
        assertEquals(person.getAge(), 28);
        assertEquals(graph.getVertex(1).getProperty("name"), "jen");
        assertEquals(graph.getVertex(1).getProperty("age"), 28);
    }
}
