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
    // Domain and Range are the inverse of the direction of the Edge. Typically you would call these methods getPerson and getProject to avoid confusion...
	
    @Range
    public Project getDomain();

    @Domain // inverse direction of the edge
    public Person getRange();

    @Property("weight")
    public Float getWeight();
}
