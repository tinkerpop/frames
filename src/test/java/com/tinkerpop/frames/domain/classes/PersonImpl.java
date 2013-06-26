package com.tinkerpop.frames.domain.classes;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerImpl;

public abstract class PersonImpl implements JavaHandlerImpl<Vertex>, Person {

	@Override
	@JavaHandler
	public String getNameAndAge() {
		return getName() + " (" + getAge() + ")";
	}
	
	@Override
	@JavaHandler
	public Iterable<Person> getCoCreatorsJava() {
		return frame(gremlin().as("x").out("created").in("created").except("x"));
		
	}
}
