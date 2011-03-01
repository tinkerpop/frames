package com.tinkerpop.frames.domain.objects;

import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.domain.relations.CreatedBy;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Project extends NamedObject {

    @Relation(type = Relation.Type.FULL, label = "created", direction = Relation.Direction.INVERSE)
    public Collection<Person> getCreatedByPerson();

    @Relation(type = Relation.Type.HALF, label = "created", direction = Relation.Direction.INVERSE)
    public Collection<CreatedBy> getCreatedBy();
}
