package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Element;

public interface TypeResolver {
	public static TypeResolver DEFAULT = new TypeResolver() {

		@Override
		public Class<?> resolveType(Element e, Class<?> defaultType) {
			return defaultType;
		}
		
	};

	Class<?> resolveType(Element e, Class<?> defaultType);
}
