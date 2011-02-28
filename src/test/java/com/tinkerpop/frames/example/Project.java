package com.tinkerpop.frames.example;

import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FullRelation;
import com.tinkerpop.frames.HalfRelation;
import com.tinkerpop.frames.Property;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Project {

    @Property("name")
    public String getName();

    @FullRelation(label = "created", direction = Direction.INVERSE, kind = Person.class)
    public Collection<Person> getCreatedByPerson();

    @HalfRelation(label = "created", direction = Direction.INVERSE, kind = CreatedBy.class)
    public Collection<CreatedBy> getCreatedBy();
}
