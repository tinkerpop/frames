package com.tinkerpop.frames.domain.objects;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.domain.relations.Knows;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Person extends NamedObject {

    @Property("age")
    public Integer getAge();

    @Relation(type = Relation.Type.HALF, label = "knows", direction = Relation.Direction.STANDARD)
    public Collection<Knows> getKnows();

    @Relation(type = Relation.Type.FULL, label = "knows", direction = Relation.Direction.STANDARD)
    public Collection<Person> getKnowsPerson();

    @Relation(type = Relation.Type.FULL, label = "created", direction = Relation.Direction.STANDARD)
    public Collection<Project> getCreatedProject();
}
