package com.tinkerpop.frames.domain.classes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.domain.incidences.CreatedBy;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Project extends NamedObject {

    @Property("lang")
    public String getLanguage();

    @Adjacency(label = "created", direction = Direction.IN)
    public Iterable<Person> getCreatedByPeople();

    @Incidence(label = "created", direction = Direction.IN)
    public Iterable<CreatedBy> getCreatedBy();

    @Adjacency(label = "created", direction = Direction.IN)
    public void removeCreatedByPerson(Person person);

    @Incidence(label = "created", direction = Direction.IN)
    public void removeCreatedBy(CreatedBy createdBy);

    @Adjacency(label = "created", direction = Direction.IN)
    public void addCreatedByPeople(final Person person);
}

