package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Vertex;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;


public class ClassUtilities {
    private static final String SET = "set";
    private static final String GET = "get";
    private static final String REMOVE = "remove";
    private static final String ADD = "add";
    private static final String IS = "is";
    private static final String CAN = "can";

    public static boolean isGetMethod(final Method method) {
        Class<?> returnType = method.getReturnType();
        return (method.getName().startsWith(GET) || (returnType == Boolean.class || returnType == Boolean.TYPE) && (method.getName().startsWith(IS) || method.getName().startsWith(CAN)));
    }

    public static boolean isSetMethod(final Method method) {
        return method.getName().startsWith(SET);
    }

    public static boolean isRemoveMethod(final Method method) {
        return method.getName().startsWith(REMOVE);
    }

    public static boolean acceptsIterable(final Method method) {
        return 1 == method.getParameterTypes().length && Iterable.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    public static boolean returnsIterable(final Method method) {
        return Iterable.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean returnsVertex(final Method method) {
        return Vertex.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean returnsMap(final Method method) {
        return Map.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean isAddMethod(final Method method) {
        return method.getName().startsWith(ADD);
    }

    @SuppressWarnings("rawtypes")
    public static Class getGenericClass(final Method method) {
        final Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
        	Type type = ((ParameterizedType) returnType).getActualTypeArguments()[0];
        	if (type instanceof TypeVariable) {
        		return (Class)((TypeVariable)type).getBounds()[0];
        	} else
        		return (Class)type;
        } else
            return method.getReturnType();
    }
}
