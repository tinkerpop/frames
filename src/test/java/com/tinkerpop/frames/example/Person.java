package com.tinkerpop.frames.example;

import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.FullRelation;
import com.tinkerpop.frames.HalfRelation;
import com.tinkerpop.frames.Property;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Person {

    @Property("name")
    public String getName();

    @Property("age")
    public Integer getAge();

    @Property("name")
    public void setName(final String name);

    @HalfRelation(label = "knows", direction = Direction.STANDARD, kind = Knows.class)
    public Collection<Knows> getKnows();

    @FullRelation(label = "knows", direction = Direction.STANDARD, kind = Person.class)
    public Collection<Person> getKnowsPerson();

    @FullRelation(label = "created", direction = Direction.STANDARD, kind = Project.class)
    public Collection<Project> getCreatedProject();
}
