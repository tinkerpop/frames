package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * The proxy class of a framed element.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedElement implements InvocationHandler {

    private final Direction direction;
    protected final FramesManager manager;
    protected final Element element;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;

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

    public FramedElement(final FramesManager manager, final Element element, final Direction direction) {
        if (null == manager) {
            throw new IllegalArgumentException("null manager");
        }

        if (null == element) {
            throw new IllegalArgumentException("null element");
        }

        this.element = element;
        this.manager = manager;
        this.direction = direction;
    }

    public FramedElement(final FramesManager manager, final Element element) {
        this(manager, element, null);
    }


    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {

        if (method.equals(hashCodeMethod)) {
            return proxyHashCode(proxy);
        } else if (method.equals(equalsMethod)) {
            return proxyEquals(proxy, arguments[0]);
        } else if (method.equals(toStringMethod)) {
            return proxyToString(proxy);
        }

        if (isVertexGetter(method) && element instanceof Vertex) {
            return (Vertex) getElement();
        } else if (isEdgeGetter(method) && element instanceof Edge) {
            return (Edge) getElement();
        }

        final Annotation[] annotations = method.getAnnotations();
        for (final Annotation annotation : annotations) {
            if (manager.hasAnnotationHandler(annotation.annotationType())) {
                if (element instanceof Vertex) {
                    return manager.getAnnotationHandler(annotation.annotationType())
                            .processVertex(annotation, method, arguments, this.manager, (Vertex) this.element);
                } else if (element instanceof Edge) {
                    return manager.getAnnotationHandler(annotation.annotationType())
                            .processEdge(annotation, method, arguments, this.manager, (Edge) this.element, direction);
                }
            }
        }

        return NO_INVOCATION_PATH;

    }


    private Integer proxyHashCode(final Object proxy) {
        return System.identityHashCode(proxy) + this.element.hashCode();
    }

    private Boolean proxyEquals(final Object proxy, final Object other) {
        if (proxy.getClass().equals(other.getClass())) {
            return ((FramedElement) (Proxy.getInvocationHandler(proxy))).getElement().getId().equals(((FramedElement) (Proxy.getInvocationHandler(other))).getElement().getId());
        } else {
            return Boolean.FALSE;
        }
    }

    private String proxyToString(final Object proxy) {
        FramedElement f = (FramedElement) Proxy.getInvocationHandler(proxy);
        Element el = f.getElement();
        return "framed[" + el.toString() + "]";
    }

    public Element getElement() {
        return this.element;
    }

    protected boolean isVertexGetter(final Method method) {
        return method.getName().equals("asVertex");
    }

    protected boolean isEdgeGetter(final Method method) {
        return method.getName().equals("asEdge");
    }
}
