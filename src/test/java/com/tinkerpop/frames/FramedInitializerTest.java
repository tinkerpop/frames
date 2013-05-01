package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
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
public class FramedInitializerTest {

    private FramedGraph<Graph> framedGraph;

    @Before
    public void setup() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        framedGraph = new FramedGraph<Graph>(graph);
        framedGraph.registerFrameIntercept(nameDefaulter);
        framedGraph.registerFrameIntercept(weightDefaulter);
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

    public static FrameEventListener nameDefaulter = new AbstractEventListener() {
        @Override
        public void postCreateVertex(Class<?> kind, FramedGraph<?> framedGraph, Vertex element) {
            if (kind == Person.class) {
                assertNotNull(framedGraph);
                element.setProperty("name", "Defaulted");
            }
        }
    };

    public static FrameEventListener weightDefaulter = new AbstractEventListener() {

        @Override
        public void postCreateEdge(Class<?> kind, FramedGraph<?> framedGraph, Edge element) {
            assertNotNull(framedGraph);
            if (kind == Knows.class) {
                element.setProperty("weight", 1.0f);
            }
        }
    };

}
