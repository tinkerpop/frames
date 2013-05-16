package com.tinkerpop.frames.typed;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FrameInitializer;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.TypeResolver;

public class TypeManager implements TypeResolver, FrameInitializer {

	private TypeRegistry typeRegistry;
    public TypeManager(TypeRegistry typeRegistry) {
    	this.typeRegistry = typeRegistry;
    }
	
	@Override
	public Class<?> resolveType(Element e, Class<?> defaultType) {
		Class<?> typeHoldingTypeField = typeRegistry.getTypeHoldingTypeField(defaultType);
		if (typeHoldingTypeField != null) {
			String value = e.getProperty(typeHoldingTypeField.getAnnotation(TypeField.class).value());
			Class<?> type = value == null ? null : typeRegistry.getType(typeHoldingTypeField, value);
			if (type != null) {
				return type;
			}
		}
		return defaultType;
	}
	
	


	@Override
	public void initElement(Class<?> kind, FramedGraph<?> framedGraph, Element element) {
		Class<?> typeHoldingTypeField = typeRegistry.getTypeHoldingTypeField(kind);
		if (typeHoldingTypeField != null) {
			TypeValue typeValue = kind.getAnnotation(TypeValue.class);
			if (typeValue != null) {
				element.setProperty(typeHoldingTypeField.getAnnotation(TypeField.class).value(), typeValue.value());
			}
		}
	}


}
