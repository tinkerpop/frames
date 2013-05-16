package com.tinkerpop.frames.typed;

import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.frames.FramedGraph;

/**
 * Build a TypeRegistry for a {@link FramedGraph}. With a {@link TypeRegistry} the {@link FramedGraph} is able to store type-information on edges,
 * and use stored type-information to construct vertices/edges based on type-information stored in the graph (runtime).
 * 
 * @see TypeField
 * @see TypeValue
 * @see FramedGraph
 */
public class TypeRegistryBuilder {
	// maps to the (sub-)type that holds the TypeField annotation:
	Map<Class<?>, Class<?>> typeFields = new HashMap<Class<?>, Class<?>>();
	// maps from a typediscriminator (typefield-holding-type + typevalue) to the interface that should be implemented when that discriminator is used:
	Map<TypeRegistry.TypeDiscriminator, Class<?>> typeDiscriminators = new HashMap<TypeRegistry.TypeDiscriminator, Class<?>>();
	
	/**
	 * @param Add the interface to the registry. The interface should have a {@link TypeValue} annotation, and there should be a {@link TypeField} annotation
	 * on the interface or its parents.
	 */
	public TypeRegistryBuilder add(Class<?> type) {
		Validate.assertArgument(type.isInterface(), "Not an interface: %s", type.getName());
		if (!typeFields.containsKey(type)) {
			Class<?> typeHoldingTypeField = findTypeHoldingTypeField(type);
			Validate.assertArgument(typeHoldingTypeField != null, "The type and its supertypes don't have a @TypeField annotation: %s", type.getName());
			typeFields.put(type, typeHoldingTypeField);
			registerTypeValue(type, typeHoldingTypeField);
		}
		return this;
	}
	
	/**
	 * @return A {@link TypeRegistry} build from all added interfaces.
	 * 
	 * @see #add(Class)
	 */
	public TypeRegistry build() {
		return new TypeRegistry(typeFields, typeDiscriminators);
	}
	
	private Class<?> findTypeHoldingTypeField(Class<?> type) {
		Class<?> typeHoldingTypeField = type.getAnnotation(TypeField.class) == null ? null : type;
		for (Class<?> parentType: type.getInterfaces()) {
			Class<?> parentTypeHoldingTypeField = findTypeHoldingTypeField(parentType);
			Validate.assertArgument(parentTypeHoldingTypeField == null || typeHoldingTypeField == null || parentTypeHoldingTypeField == typeHoldingTypeField, "You have multiple TypeField annotations in your class-hierarchy for %s", type.getName());
			if (typeHoldingTypeField == null)
				typeHoldingTypeField = parentTypeHoldingTypeField;
		}
		return typeHoldingTypeField;
	}
	
	private void registerTypeValue(Class<?> type, Class<?> typeHoldingTypeField) {
		TypeValue typeValue = type.getAnnotation(TypeValue.class);
		Validate.assertArgument(typeValue != null, "The type does not have a @TypeValue annotation: %s", type.getName());
		typeDiscriminators.put(new TypeRegistry.TypeDiscriminator(typeHoldingTypeField, typeValue.value()), type);
	}
}