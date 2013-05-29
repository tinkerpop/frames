package com.tinkerpop.frames.typed;

import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;

/**
 * @see TypeRegistryBuilder
 * @see FramedGraph
 */
public class TypeRegistry {
	private Map<Class<?>, Class<?>> typeFields = new HashMap<Class<?>, Class<?>>();
	private Map<TypeDiscriminator, Class<?>> typeDiscriminators = new HashMap<TypeDiscriminator, Class<?>>();
	
	TypeRegistry(Map<Class<?>, Class<?>> typeFields, Map<TypeDiscriminator, Class<?>> typeDiscriminators) {
		//create copies to assert immutability:
		this.typeFields = new HashMap<Class<?>, Class<?>>(typeFields);
		this.typeDiscriminators = new HashMap<TypeRegistry.TypeDiscriminator, Class<?>>(typeDiscriminators);
	}
	
	/**
	 * @return The interface that has the {@link TypeField} annotation for this class. (Either the class itself, or a base class if the class was registered).
	 */
	public Class<?> getTypeHoldingTypeField(Class<?> type) {
		if (type.getAnnotation(TypeField.class) != null)
			return type;
		return typeFields.get(type);
	}
	
	/**
	 * @param typeHoldingTypeField The type that has the {@link TypeField} annotation for the Proxy class to be constructed. 
	 * @param typeValue The actual persisted value for a type field on an {@link Element} in the graph
	 * @return The type that needs to be constructed, or null if there is no registered class that matches the input values.
	 */
	public Class<?> getType(Class<?> typeHoldingTypeField, String typeValue) {
		Class<?> result = typeDiscriminators.get(new TypeDiscriminator(typeHoldingTypeField, typeValue));
		return result;
	}
	
	static final class TypeDiscriminator {
		private Class<?> typeHoldingTypeField;
		private String value;
		
		TypeDiscriminator(Class<?> typeHoldingTypeField, String value) {
			Validate.assertNotNull(typeHoldingTypeField, value);
			this.typeHoldingTypeField = typeHoldingTypeField;
			this.value = value;
		}

		@Override public int hashCode() {
			return 31 * (31 + typeHoldingTypeField.hashCode()) + value.hashCode();
		}

		@Override public boolean equals(Object obj) {
			if (obj instanceof TypeDiscriminator) {
				TypeDiscriminator other = (TypeDiscriminator)obj;
				//fields are never null:
				return typeHoldingTypeField.equals(other.typeHoldingTypeField) && value.equals(other.value);
			}
			return false;
		}
	}
}
