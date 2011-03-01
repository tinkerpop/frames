package com.tinkerpop.frames.domain.objects;

import com.tinkerpop.frames.*;
import com.tinkerpop.frames.domain.relations.CreatedBy;
import com.tinkerpop.frames.domain.objects.Person;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Project {

    @Property("name")
    public String getName();

    @Relation(type= Relation.Type.FULL, label = "created", direction = Relation.Direction.INVERSE)
    public Collection<Person> getCreatedByPerson();

    @Relation(type= Relation.Type.HALF, label = "created", direction = Relation.Direction.INVERSE)
    public Collection<CreatedBy> getCreatedBy();
}
