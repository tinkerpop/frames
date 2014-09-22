package com.tinkerpop.frames;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedElementTest {

    Graph graph;
    FramedGraph<Graph> framedGraph;
    Person marko;

    @Before
    public void buildPerson() {
      graph = TinkerGraphFactory.createTinkerGraph();
      framedGraph = new FramedGraphFactory().create(graph);
      marko = framedGraph.getVertex(1, Person.class);
    }
  
	@Test
    public void testGettingProperties() {
        assertEquals(marko.getName(), "marko");
        assertEquals(marko.getAge(), new Integer(29));

        Project lop = framedGraph.getVertex(3, Project.class);
        assertEquals(lop.getName(), "lop");
        assertEquals(lop.getLanguage(), "java");

        CreatedInfo markoCreatedLopInfo = framedGraph.getEdge(9, CreatedInfo.class);
        assertEquals(markoCreatedLopInfo.getWeight(), 0.4f, 0.1f);
        //Same with using deprecated Domain/Range annotations:
        Created markoCreatedLop = framedGraph.getEdge(9, Direction.OUT, Created.class);
        assertEquals(markoCreatedLop.getWeight(), 0.4f, 0.1f);
        CreatedBy lopCreatedByMarko = framedGraph.getEdge(9, Direction.IN, CreatedBy.class);
        assertEquals(lopCreatedByMarko.getWeight(), 0.4f, 0.1f);

        Person temp = framedGraph.frame(graph.addVertex(null), Person.class);
        assertNull(temp.getName());
        assertNull(temp.getAge());

    }

	@Test
    public void testSettingProperties() {
        assertEquals(marko.getName(), "marko");
        marko.setName("pavel");
        assertEquals(marko.getName(), "pavel");
        assertEquals(marko.getAge(), new Integer(29));
        marko.setAge(31);
        assertEquals(marko.getAge(), new Integer(31));
        
        CreatedInfo markoCreatedLopInfo = framedGraph.getEdge(9, CreatedInfo.class);
        assertEquals(markoCreatedLopInfo.getWeight(), 0.4f, 0.1f);
        markoCreatedLopInfo.setWeight(99.0f);
        assertEquals(markoCreatedLopInfo.getWeight(), 99.0f, 0.1f);
        markoCreatedLopInfo.setWeight(0.4f);
        
        //Same with deprecated Domain/Range annotations:
        Created markoCreatedLop = framedGraph.getEdge(9, Direction.OUT, Created.class);
        assertEquals(markoCreatedLop.getWeight(), 0.4f, 0.1f);
        markoCreatedLop.setWeight(99.0f);
        assertEquals(markoCreatedLop.getWeight(), 99.0f, 0.1f);
    }

    @Test
    public void testRemoveProperties() {
        assertEquals(marko.getAge(), new Integer(29));
        marko.removeAge();
        assertNull(marko.getAge());
    }

    @Test
    public void testSetPropertiesToNull() {
        assertEquals(marko.getAge(), new Integer(29));
        marko.setAge(null);
        assertNull(marko.getAge());
    }

    @Test
    public void testEnumProperty() {
        assertEquals(marko.getGender(), null);
        marko.setGender(Person.Gender.MALE);
        assertEquals(Person.Gender.MALE, marko.getGender());
        marko.setGender(null);
        assertEquals(null, marko.getGender());
        marko.setGender(Person.Gender.MALE);
        marko.removeGender();
        assertEquals(marko.getGender(), null);
    }
    
    @Test
    public void testMultipleEnumProperties() {
      marko.addTitle(Person.Title.MR);
      marko.addTitle(Person.Title.DR);
      assertThat(marko.getTitles(), hasItems(Person.Title.MR, Person.Title.DR));
    }

    @Test
    public void testAddSingleProperty() {
      marko.addInterest("music");
      assertThat("Single interest should be added", marko.getInterests(), hasItems("music"));
    }

    @Test
    public void testAddPropertyCollection() {;
      marko.addInterests(newArrayList("music", "food"));
      assertThat("Multiple interests should be added", marko.getInterests(), hasItems("music", "food"));
    }

    @Test
    public void testAddDuplicateSingleProperties() {
      marko.addInterest("music");
      marko.addInterest("music");
      assertThat("Collected properties should stay unique", marko.getInterests(), hasItems("music"));
    }

    @Test
    public void testDuplicatePropertyCollection() {
      marko.addInterests(newArrayList("music", "music"));
      assertThat("Collected properties should stay unique", marko.getInterests(), hasItems("music"));
    }

    @Test
    public void testAddMultipleProperties() {
      marko.addInterest("music");
      marko.addInterest("food");
      assertThat("Multiple interests should be added", marko.getInterests(), hasItems("music", "food"));
    }
    
    @Test
    public void testAddNullProperty() {
      marko.addInterest("music");
      marko.addInterest("food");
      marko.addInterest(null);
      assertThat("Multiple interests should be added", marko.getInterests(), hasItems("music", "food"));
    }

    @Test
    public void testSetAndAddDuplicateProperties() {
      marko.setInterest("music");
      marko.addInterests(newArrayList("music", "food"));
      assertThat("Multiple interests should be added", marko.getInterests(), hasItems("music", "food"));
    }

    @Test
    public void testResetToSingleProperty() {
      marko.addInterests(newArrayList("music", "food"));
      marko.setInterest("music");
      assertThat("Properties should reset", marko.getInterests(), hasItems("music"));
      assertThat("Properties should reset", marko.getInterest(), is("music"));
    }

    @Test
    public void testNonStringTypes() {
      marko.addFavoriteNumber(1);
      marko.addFavoriteNumber(2);
      assertThat(marko.getFavoriteNumbers(), hasItems(1, 2));
    }

    @Test(expected=IllegalStateException.class)
    public void testPrimitiveGetMethodOnMultivaluedProperty() {
      marko.addInterest("a");
      marko.addInterest("b");
      marko.getInterest();
    }

    @Test
    public void testRemove() {
      marko.addInterest("a");
      marko.removeInterests();
      assertThat(marko.getInterest(), is(nullValue()));
      assertThat(Iterables.isEmpty(marko.getInterests()), is(true));
    }

    @Test(expected=ClassCastException.class)
    public void testIncompatibleTypes() {
      marko.addFavoriteNumber(1);
      marko.addFavoriteNumber(false);
    }

    @Test(expected=ClassCastException.class)
    public void testIncompatibleTypesCollections() {
      marko.addFavoriteNumber(1);
      Iterable<Boolean> bools = marko.getFavoriteNumbersAsBoolean();
      bools.iterator().next().booleanValue();
    }

    @Test
    public void testHasMethod() {
      assertThat("Node should not have any interests", marko.hasInterests(), is(false));
      marko.addInterest("music");
      assertThat("Node should now have interests", marko.hasInterests(), is(true));
    }

    @Test
    public void testIsMethods() {
      marko.setAwesome(true);
      assertThat(marko.isAwesome(), is(true));
      marko.setAwesome(false);
      assertThat(marko.isAwesome(), is(false));
    }

    @Test
    public void testToString() {
        assertEquals("v[1]", marko.toString());

        CreatedInfo markoCreatedLopInfo = framedGraph.getEdge(9, CreatedInfo.class);
        assertEquals("e[9][1-created->3]", markoCreatedLopInfo.toString());
        //Using deprecated Domain/Range annotations:
        Created markoCreatedLop = framedGraph.getEdge(9, Direction.OUT, Created.class);
        assertEquals("e[9][1-created->3]", markoCreatedLop.toString());
    }

    @Test
    public void testEquality() {
        assertEquals(framedGraph.getVertex(1, Person.class), framedGraph.frame(graph.getVertex(1), Person.class));
    }
    
    @Test(expected=UnhandledMethodException.class)
    public void testUnhandledMethodNoAnnotation() {
        marko.unhandledNoAnnotation();
    }
    
    @Test(expected=UnhandledMethodException.class)
    public void testUnhandledMethodWithAnnotation() {
        marko.unhandledNoHandler();
    }
}
