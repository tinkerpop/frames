package com.tinkerpop.frames;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.lang.reflect.Method;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.incidences.Knows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FrameEventListenerTest {
    private FramedGraph<Graph> framedGraph;

    @Before
    public void setup() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        framedGraph = new FramedGraph<Graph>(graph);
        framedGraph.registerFrameIntercept(exceptionInterceptor);
        framedGraph.registerFrameIntercept(typeSetterInterceptor);
    }
    @After
    public void tearDown(){
        framedGraph.shutdown();
    }
    @Test
    public void testListInterceptor(){
        assertThat(framedGraph.getFrameEventListeners(), hasItems(exceptionInterceptor, typeSetterInterceptor));
    }
    @Test
    public void testPostCreateVertex(){
        Person person= framedGraph.addVertex(100, Person.class);
        assertThat(person.asVertex().getProperty("type").toString(),is(Person.class.getName()));
        person.setAge(22);
        person.setGender(Person.Gender.MALE);
        person.setName("marko");

        Person person2= framedGraph.addVertex(200, Person.class);
        person2.setAge(22);
        person2.setGender(Person.Gender.MALE);
        person2.setName("polo");
        assertThat(person2.asVertex().getProperty("type").toString(),is(Person.class.getName()));

        exceptionInterceptor.setOn(false);
        Person person3 = person.addKnowsNewPerson();
        person3.setAge(22);
        person3.setGender(Person.Gender.FEMALE);
        assertThat(person3.asVertex().getProperty("type").toString(),is(Person.class.getName()));
        exceptionInterceptor.setOn(true);
    }

    @Test
    public void testPreNPostCreateEdge(){
        Person marko = framedGraph.getVertex(1,Person.class);
        Person vadas = framedGraph.getVertex(2,Person.class);
        Person josh  = framedGraph.getVertex(3,Person.class);

        Knows know = marko.addKnows(vadas);
        assertThat(((EdgeFrame) know).asEdge().getProperty("type").toString(), is(Knows.class.getName()));

        Person person= framedGraph.addVertex(101, Person.class);
        Person person2= framedGraph.addVertex(201, Person.class);

        exceptionInterceptor.setOn(false);

        person.setAge(22);
        person.setGender(Person.Gender.MALE);
        person.setName("marko");
        person2.setAge(25);
        person2.setGender(Person.Gender.MALE);
        person2.setName("polo");

        exceptionInterceptor.setOn(true);
        try{
            person.addKnowsPerson(person2);
            assertFalse(true);
        }catch (Exception e){}

        try{
            framedGraph.addEdge(150,person.asVertex(),person2.asVertex(),"knows",Direction.BOTH,Knows.class);
            assertFalse (true);
        }catch (Exception e){}

        try{
            Person person3 = person.addKnowsNewPerson();
            person3.setAge(22);
            person3.setGender(Person.Gender.FEMALE);
            assertFalse (true);
        }catch (Exception e){ }

        try{
            framedGraph.addEdge(150,person.asVertex(),person2.asVertex(),"blockIt");
        }catch (Exception e){
            assertFalse (true);
        }

    }
    @Test
    public void testPreNPostUpdateProperty(){
        Person marko = framedGraph.getVertex(1,Person.class);
        try{
            marko.setAge(24);
            assertFalse(true);
        }catch (Exception e){}
        try{
            marko.removeAge();
            assertFalse(true);
        }catch (Exception e){}
        assertThat(marko.getAge(), notNullValue());
    }

    @Test
    public void testPreDeleteVertex(){
        Person person= framedGraph.addVertex(102, Person.class);
        person.setAge(22);
        person.setGender(Person.Gender.MALE);
        person.setName("marko");
        try{
            framedGraph.removeVertex(Person.class,person.asVertex());
            assertFalse(true);
        }catch (Exception e){

        }
        assertThat(framedGraph.getVertex(102), notNullValue());
    }

    @Test
    public void testPreDeleteEdge(){
        exceptionInterceptor.setOn(false);
        Person person= framedGraph.addVertex(104, Person.class);
        person.setAge(24);
        person.setGender(Person.Gender.MALE);
        person.setName("marko");

        Person person2= framedGraph.addVertex(105, Person.class);
        person2.setAge(22);
        person2.setGender(Person.Gender.MALE);
        person2.setName("polo");

        Knows knows = framedGraph.addEdge(201,person.asVertex(),person2.asVertex(),"knows", Direction.BOTH, Knows.class);
        exceptionInterceptor.setOn(true);
        try{
            framedGraph.removeEdge(Knows.class,((EdgeFrame) knows).asEdge());
            assertFalse(true);
        }catch (Exception e){ }
        assertThat(framedGraph.getEdge(((EdgeFrame) knows).asEdge().getId()), notNullValue());
    }




    private static TestFrameEventListener exceptionInterceptor = new TestFrameEventListener();
    private static FrameEventListener typeSetterInterceptor = new AbstractEventListener(){

        @Override
        public void postCreateEdge(Class<?> kind, FramedGraph<?> framedGraph, Edge edge) {
            if (kind!= null)
                edge.setProperty("type",kind.getName());
            else
                edge.setProperty("type","unkown");
        }

        @Override
        public void postCreateVertex(Class<?> kind, FramedGraph<?> framedGraph, Vertex vertex) {
            if (kind!= null)
                vertex.setProperty("type",kind.getName());
            else
                vertex.setProperty("type","unkown");
        }
    };
    public static class TestFrameEventListener extends AbstractEventListener {
        private boolean on = true;

        public void setOn(boolean on) {
            this.on = on;
        }

        @Override
        public void preDeleteEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final Edge edge) {
            if (!on){return;}
            throw new IllegalStateException("cannot delete");
        }

        @Override
        public void preDeleteVertex(final Class<?> kind, final FramedGraph<?> framedGraph, final Vertex vertexFrame) {
            if (!on){return;}
            throw new IllegalStateException("cannot delete");
        }

        @Override
        public void preDeleteProperty(final FramedGraph<?> framedGraph, final Element element, final Method method, final Object fieldName) {
            if (!on){return;}
            throw new IllegalStateException("cannot delete");
        }

        @Override
        public void preUpdateProperty(final FramedGraph<?> framedGraph, final Element element, final Method method, final Object fieldName, final Object newValue) {
            if (!on){return;}
            if (fieldName.equals("age") && (Integer)newValue != 22){
                throw new IllegalArgumentException("cannot update age");
            }
        }

        @Override
        public void postCreateVertex(final Class<?> kind, final FramedGraph<?> framedGraph, final Vertex vertex) {
            if (!on){return;}
            vertex.setProperty("type",kind.getName());
        }

        @Override
        public void postCreateEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final Edge edge) {
            if (!on){return;}
        }

        @Override
        public void preCreateEdge(final Class<?> kind, final FramedGraph<?> framedGraph, final String label, final Vertex outVertex, final Vertex inVertex) {
            if (!on){return;}
            if (outVertex.getProperty("type") != null
                    && outVertex.getProperty("type").equals(Person.class.getName())
                    && inVertex.getProperty("type") != null
                    && inVertex.getProperty("type").equals(Person.class.getName())){
                throw new IllegalArgumentException("cannot create edge");
            }else if (label.equals("blockIt")){
                throw new IllegalArgumentException("cannot create edge");
            }

        }
    }
}
