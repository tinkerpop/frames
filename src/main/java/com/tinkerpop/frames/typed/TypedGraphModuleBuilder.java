package com.tinkerpop.frames.typed;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.Module;

/**
 * TODO
 */
public class TypedGraphModuleBuilder {
	private TypeRegistry typeRegistry = new TypeRegistry();
	
	public TypedGraphModuleBuilder() {
		
	}
	
	public TypedGraphModuleBuilder withClass(Class<?> type) {
		typeRegistry.add(type);
		return this;
	}
	
	public Module build() {
		final TypeManager manager = new TypeManager(typeRegistry);
		return new Module() {
			
			@Override
			public Graph configure(Graph baseGraph, FramedGraphConfiguration config) {
				config.addTypeResolver(manager);
				config.addFrameInitializer(manager);
				return baseGraph;
			}

			
		};
	}
}
