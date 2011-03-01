package com.tinkerpop.frames.domain.classes;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.domain.classes.NamedObject;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.relations.CreatedBy;

import java.util.Collection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Project extends NamedObject {

    @Property("lang")
    public String getLanguage();

    @Relation(type = Relation.Type.FULL, label = "created", direction = Relation.Direction.INVERSE)
    public Collection<Person> getCreatedByPerson();

    @Relation(type = Relation.Type.HALF, label = "created", direction = Relation.Direction.INVERSE)
    public Collection<CreatedBy> getCreatedBy();
}

