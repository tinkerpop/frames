package com.tinkerpop.frames.domain.objects;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.domain.relations.Knows;
import com.tinkerpop.frames.domain.objects.Project;

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

    @Relation(type = Relation.Type.HALF, label = "knows", direction = Relation.Direction.STANDARD)
    public Collection<Knows> getKnows();

    @Relation(type = Relation.Type.FULL, label = "knows", direction = Relation.Direction.STANDARD)
    public Collection<Person> getKnowsPerson();

    @Relation(type = Relation.Type.FULL, label = "created", direction = Relation.Direction.STANDARD)
    public Collection<Project> getCreatedProject();
}
