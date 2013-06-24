package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface CreatedBy {

	// Domain and Range are respective to the actual edge, which is a "created" edge, with a Person
	// at the domain side. So the Person is the domain here, because it is the domain (source) of the
	// "created" edge.
	// Typically you would call these methods getPerson() and getProject() to avoid confusion.
	@Range
    public Project getDomain();

    @Domain
    public Person getRange();

    @Property("weight")
    public Float getWeight();
}
