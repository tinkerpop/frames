package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public interface TypeResolver {
	public static TypeResolver DEFAULT = new TypeResolver() {
		@Override public Class<?>[] resolveType(Vertex v, Class<?> defaultType) {
			return new Class<?>[] { defaultType, VertexFrame.class };
		}

		@Override public Class<?>[] resolveType(Edge e, Class<?> defaultType) {
			return new Class<?>[] { defaultType, EdgeFrame.class };
		}
	};

	Class<?>[] resolveType(Vertex v, Class<?> defaultType);
	Class<?>[] resolveType(Edge e, Class<?> defaultType);
}