package com.tinkerpop.frames.modules.javahandler;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Knows;

public class JavaHandlerTest {

	@Test
	public void testJavaHandlerVertices() {
		TinkerGraph base = TinkerGraphFactory.createTinkerGraph();
		FramedGraph<TinkerGraph> g = new FramedGraphFactory(new JavaHandlerModule()).create(base);
		Person person = g.getVertex(1, Person.class);
		String profile = person.getNameAndAge();
		Assert.assertEquals("marko (29)", profile);

		Person person2 = g.getVertex(2, Person.class);
		String profile2 = person2.getNameAndAge();
		Assert.assertEquals("vadas (27)", profile2);

		Set<Person> coCreators = new HashSet<Person>();

		Iterables.addAll(coCreators, person.getCoCreatorsJava());
		Assert.assertEquals(Sets.newHashSet(g.getVertex(4), g.getVertex(6)), coCreators);

	}

	@Test
	public void testJavaHandlerEdges() {
		TinkerGraph base = TinkerGraphFactory.createTinkerGraph();
		FramedGraph<TinkerGraph> g = new FramedGraphFactory(new JavaHandlerModule()).create(base);
		Knows knows = g.getEdge(7, Direction.OUT, Knows.class);
		Assert.assertEquals("marko<->vadas", knows.getNames());
	}
	
	
	@Test
	public void testJavaHandlerClassAnnotation() {
		TinkerGraph base = TinkerGraphFactory.createTinkerGraph();
		FramedGraph<TinkerGraph> g = new FramedGraphFactory(new JavaHandlerModule()).create(base);
		Project project = g.getVertex(5, Project.class);
		Assert.assertEquals("java", project.getLanguageUsingMixin());
	}
	
	
}
