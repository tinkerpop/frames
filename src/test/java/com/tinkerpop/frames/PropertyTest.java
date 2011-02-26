package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.example.Person;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class PropertyTest extends TestCase {

    public void testBasicProperties() throws Exception {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FrameManager manager = new FrameManager(graph);
        Person person = manager.load(Person.class, 1);
        assertEquals(person.getName(), "marko");
        assertEquals(person.getAge(), 29);
        assertEquals(person.getVertex(), graph.getVertex(1));
        //assertEquals(person.getKnows().size(), 2);
        System.out.println(person.getKnows().iterator().next().getName());

        System.out.println(person.getName() + " " + person.getAge() + " " + person.getKnows() + " " + person.getVertex());
        person.setName("jen");
        manager.save(person);
        System.out.println(graph.getVertex(1).getProperty("name"));
    }
}
