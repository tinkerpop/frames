package com.tinkerpop.frames.example;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.Vertex;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Person {

    @Vertex
    private com.tinkerpop.blueprints.pgm.Vertex vertex;
    @Property
    private String name;
    @Property
    private int age;
    @Relation(clazz = Person.class)
    private Collection<Person> knows;
    @Relation(clazz = Project.class)
    private Collection<Project> created;


    public com.tinkerpop.blueprints.pgm.Vertex getVertex() {
        return this.vertex;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Collection<Person> getKnows() {
        return this.knows;
    }

    public void addKnows(Person person) {
        this.knows.add(person);
    }

    public void addCreated(Project project) {
        this.created.add(project);
    }

    public Collection<Project> getCreated() {
        return this.created;
    }

    //public Iterable<Pair<Map,Person>> knows;
}
