package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class FramedElement implements InvocationHandler {

    protected final FramesManager manager;
    protected final Element element;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;

    private static final String SET = "set";
    private static final String GET = "get";

    protected static Object NO_INVOCATION_PATH = new Object();


    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode", null);
            equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
            toStringMethod = Object.class.getMethod("toString", null);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public FramedElement(final FramesManager manager, final Element element) {
        this.element = element;
        this.manager = manager;
    }


    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        if (method.equals(hashCodeMethod)) {
            return proxyHashCode(proxy);
        } else if (method.equals(equalsMethod)) {
            return proxyEquals(proxy, arguments[0]);
        } else if (method.equals(toStringMethod)) {
            return proxyToString(proxy);
        }

        Annotation[] anns = method.getAnnotations();
        for (Annotation ann : anns) {
            if (ann instanceof Property & isGetMethod(method)) {
                return this.element.getProperty(((Property) ann).value());
            } else if (ann instanceof Property & isSetMethod(method)) {
                this.element.setProperty(((Property) ann).value(), arguments[0]);
                return null;
            }
        }
        return NO_INVOCATION_PATH;

    }

    protected boolean isGetMethod(final Method method) {
        return method.getName().startsWith(GET);
    }

    protected boolean isSetMethod(final Method method) {
        return method.getName().startsWith(SET);
    }


    protected Integer proxyHashCode(final Object proxy) {
        return System.identityHashCode(proxy);
    }

    protected Boolean proxyEquals(final Object proxy, final Object other) {
        return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }

    protected String proxyToString(final Object proxy) {
        return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }

    protected Element getElement() {
        return this.element;
    }

}
