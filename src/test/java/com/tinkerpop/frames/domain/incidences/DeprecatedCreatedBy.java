package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * Used for testing the deprecated edge-framing via {@link FramedGraph#frame(com.tinkerpop.blueprints.Edge, com.tinkerpop.blueprints.Direction, Class)}. 
 */
public interface DeprecatedCreatedBy {
	
    @Domain
    public Project getDomain();

    @Range
    public Person getRange();

    @Property("weight")
    public Float getWeight();
}
