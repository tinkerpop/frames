package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Link;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * This class frames "created" edges (just like Created and CreatedBy), but uses
 * the {@link Out} and {@link Link} annotations instead of the now deprecated
 * Source/Target.
 */
public interface CreatedInfo extends EdgeFrame {
	@Link(Direction.OUT)
	Person getPerson();

	@Link(Direction.IN)
	Project getProject();

	@Property("weight")
	public Float getWeight();

	@Property("weight")
	public void setWeight(float weight);
}
