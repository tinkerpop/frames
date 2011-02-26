package com.tinkerpop.frames.example;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.Vertex;
import com.tinkerpop.frames.util.Direction;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Project {
    @Vertex
    private com.tinkerpop.blueprints.pgm.Vertex vertex;
    @Property
    private String name;

    private String language;

    @Relation(clazz = Person.class, label = "created", direction = Direction.INVERSE)
    private Collection<Person> createdBy;


    public com.tinkerpop.blueprints.pgm.Vertex getVertex() {
        return this.vertex;
    }

    public String getName() {
        return this.name;
    }

    public String getLanguage() {
        return this.language;
    }

    public Collection<Person> getCreatedBy() {
        return this.createdBy;
    }
}
