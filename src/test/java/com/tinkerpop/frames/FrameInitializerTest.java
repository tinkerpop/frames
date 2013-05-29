package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.incidences.Knows;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Bryn Cooke
 */
public class FrameInitializerTest {

    private FramedGraph<Graph> framedGraph;

    @Before
    public void setup() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        framedGraph = new FramedGraph<Graph>(graph);
        framedGraph.registerFrameInitializer(nameDefaulter);
        framedGraph.registerFrameInitializer(weightDefaulter);
    }

    @Test
    public void testVertexInitialization() {
        Person person = framedGraph.addVertex(null, Person.class);
        assertEquals("Defaulted", person.getName());
    }

    @Test
    public void testEdgeInitialization() {
        Person person1 = framedGraph.addVertex(null, Person.class);
        Person person2 = framedGraph.addVertex(null, Person.class);
        person1.addKnows(person2);
        assertEquals(Float.valueOf(1.0f), person1.getKnows().iterator().next().getWeight());
    }

    public static FrameInitializer nameDefaulter = new FrameInitializer() {

        @Override
        public void initElement(Class<?> kind, FramedGraph<?> framedGraph, Element element) {
            if (kind == Person.class) {
                assertNotNull(framedGraph);
                element.setProperty("name", "Defaulted");
            }
        }
    };

    public static FrameInitializer weightDefaulter = new FrameInitializer() {

        @Override
        public void initElement(Class<?> kind, FramedGraph<?> framedGraph, Element element) {
            assertNotNull(framedGraph);
            if (kind == Knows.class) {
                element.setProperty("weight", 1.0f);
            }
        }
    };

}
