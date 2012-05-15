package com.tinkerpop.frames.util;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

public class ClassUtilities {
    private static final String SET = "set";
    private static final String GET = "get";
    private static final String REMOVE = "remove";
    private static final String ADD = "add";

    public static boolean isGetMethod(final Method method) {
        return method.getName().startsWith(GET);
    }

    public static boolean isSetMethod(final Method method) {
        return method.getName().startsWith(SET);
    }

    public static boolean isRemoveMethod(final Method method) {
        return method.getName().startsWith(REMOVE);
    }

    public static boolean acceptsCollection(final Method method) {
        return 1 == method.getParameterTypes().length && Collection.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    public static boolean returnsCollection(final Method method) {
        return Collection.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean isAddMethod(final Method method) {
        return method.getName().startsWith(ADD);
    }

    @SuppressWarnings("rawtypes")
    public static Class getGenericClass(final Method method) {
        final Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedTypeImpl)
            return (Class) ((ParameterizedTypeImpl) returnType).getActualTypeArguments()[0];
        else
            return method.getReturnType();
    }
}
