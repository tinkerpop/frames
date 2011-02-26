package com.tinkerpop.frames.example;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Element;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Person {

    @Element
    private Vertex vertex;
    @Property
    private String name;
    @Property
    private int age;
    @Relation(clazz = Person.class)
    private Iterable<Person> knows;

    public Vertex getVertex() {
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

    public Iterable<Person> getKnows() {
        return this.knows;
    }

    //public Iterable<Pair<Map,Person>> knows;
}
