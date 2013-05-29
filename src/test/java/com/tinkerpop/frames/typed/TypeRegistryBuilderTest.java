package com.tinkerpop.frames.typed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TypeRegistryBuilderTest {
	@Test(expected = IllegalArgumentException.class) public void noAnotations() {
		//You can't register interfaces when there is no @TypeField on it or on any of the parents:
		new TypeRegistryBuilder().add(Empty.class);
	}
	
	@Test(expected = IllegalArgumentException.class) public void multipleTypeField() {
		//Only one type in the hierarchy can hold a @TypeField annotation:
		new TypeRegistryBuilder().add(MultipleTypeField.class);
	}
	
	@Test(expected = IllegalArgumentException.class) public void multipleTypeFieldInParents() {
		//Only one type in the hierarchy can hold a @TypeField annotation:
		new TypeRegistryBuilder().add(MultipleTypeFieldInParents.class);
	}
	
	@Test(expected = IllegalArgumentException.class) public void onlyTypeField() {
		new TypeRegistryBuilder().add(Abstract.class);
	}
	
	@Test(expected = IllegalArgumentException.class) public void onlyTypeField2() {
		new TypeRegistryBuilder().add(SubAbstract.class);
	}
	
	@Test public void registerDuplicate() {
		TypeRegistryBuilder builder = new TypeRegistryBuilder().add(A.class).add(A.class);
	    assertEquals(1, builder.typeFields.size());
	}
	
	@Test public void registerHierarchy() {
		TypeRegistryBuilder builder = new TypeRegistryBuilder().add(A.class).add(B.class);
	    assertEquals(2, builder.typeFields.size());
		assertEquals(2, builder.typeDiscriminators.size());
		TypeRegistry reg = builder.build();
		assertEquals(Abstract.class, reg.getTypeHoldingTypeField(A.class));
		assertEquals(Abstract.class, reg.getTypeHoldingTypeField(B.class));
		assertEquals(Abstract.class, reg.getTypeHoldingTypeField(Abstract.class));
		assertNull(reg.getTypeHoldingTypeField(C.class));
		
		reg = builder.add(C.class).build();
	    assertEquals(3, builder.typeFields.size());
		assertEquals(3, builder.typeDiscriminators.size());
		assertEquals(Abstract.class, reg.getTypeHoldingTypeField(C.class));
		assertEquals(A.class, reg.getType(Abstract.class, "A"));
		assertEquals(B.class, reg.getType(Abstract.class, "B"));
		assertEquals(C.class, reg.getType(Abstract.class, "C"));
	}
	
	@Test public void registerParentsSeparately() {
		TypeRegistryBuilder builder = new TypeRegistryBuilder().add(C.class);
		//Allthough C extends A and B, registering C doesn't imply that A and B are also registered:
	    assertEquals(1, builder.typeFields.size());
		assertEquals(1, builder.typeDiscriminators.size());
		assertNull(builder.build().getType(Abstract.class, "A"));
	}
	
	@Test public void registerMultipleHierarchies() {
		TypeRegistryBuilder builder = new TypeRegistryBuilder().add(A.class).add(B.class).add(C.class).add(X.class).add(Y.class);
		TypeRegistry reg = builder.build();
		assertEquals(5, builder.typeFields.size());
		assertEquals(5, builder.typeDiscriminators.size());
		assertEquals(Abstract.class, reg.getTypeHoldingTypeField(C.class));
		assertEquals(X.class, reg.getTypeHoldingTypeField(X.class));
		assertEquals(X.class, reg.getTypeHoldingTypeField(Y.class));
		assertEquals(A.class, reg.getType(Abstract.class, "A"));
		assertEquals(X.class, reg.getType(X.class, "X"));
		assertEquals(Y.class, reg.getType(X.class, "Y"));
		assertNull(reg.getType(X.class, "A"));
	}
	
	public static interface Empty {};
	public static @TypeField("type") interface Abstract extends Empty {};
	public static @TypeField("type") interface Abstract2 {};
	public static interface SubAbstract extends Abstract {};
	
	
    public static @TypeValue("multipleTypeField") @TypeField("type") interface MultipleTypeField extends Abstract{};
    public static @TypeValue("multipleTypeFieldChildren") interface MultipleTypeFieldInParents extends Abstract, Abstract2{};

	public static @TypeValue("A") interface A extends Abstract {};
	public static @TypeValue("B") interface B extends Abstract {};
	public static @TypeValue("C") interface C extends A, B {}; // diamond shape inheritance diagram (multiple paths from C to Abstract)
	
	public static @TypeValue("X") @TypeField("what") interface X {};
	public static @TypeValue("Y") interface Y extends X {};
}