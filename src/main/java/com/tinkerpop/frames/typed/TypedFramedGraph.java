package com.tinkerpop.frames.typed;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.frames.FramedGraph;

public class TypedFramedGraph<T extends Graph> extends FramedGraph<T> {

	

	public TypedFramedGraph(T baseGraph, TypeRegistry registry) {
		super(baseGraph);
		TypeManager manager = new TypeManager(registry);
		setTypeResolver(manager);
		registerFrameInitializer(manager);
	}
	
	

}
