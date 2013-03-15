package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.util.ElementHelper;

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
    protected final FramedGraph framedGraph;
    protected final Element element;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;
    private static Method asVertexMethod;
    private static Method asEdgeMethod;
    private static Method equalsVertexMethod;
    private static Method equalsEdgeMethod;

    protected static Object NO_INVOCATION_PATH = new Object();

    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
            toStringMethod = Object.class.getMethod("toString");
            asVertexMethod = VertexFrame.class.getMethod("asVertex");
            asEdgeMethod = EdgeFrame.class.getMethod("asEdge");
            equalsVertexMethod = VertexFrame.class.getMethod("equalsVertex", new Class[]{Object.class});
            equalsEdgeMethod = EdgeFrame.class.getMethod("equalsEdge", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public FramedElement(final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (null == framedGraph) {
            throw new IllegalArgumentException("FramedGraph can not be null");
        }

        if (null == element) {
            throw new IllegalArgumentException("Element can not be null");
        }

        this.element = element;
        this.framedGraph = framedGraph;
        this.direction = direction;
    }

    public FramedElement(final FramedGraph framedGraph, final Element element) {
        this(framedGraph, element, null);
    }


    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {

        if (method.equals(hashCodeMethod)) {
            return this.proxyHashCode(proxy);
        } else if (method.equals(equalsMethod)) {
            return this.proxyEquals(proxy, arguments[0]);
        } else if (method.equals(toStringMethod)) {
            return this.proxyToString(proxy);
        } else if (method.equals(asVertexMethod) || method.equals(asEdgeMethod)) {
            return this.element;
        }

        final Annotation[] annotations = method.getAnnotations();
        for (final Annotation annotation : annotations) {
            if (this.framedGraph.hasAnnotationHandler(annotation.annotationType())) {
                return this.framedGraph.getAnnotationHandler(annotation.annotationType()).processElement(annotation, method, arguments, this.framedGraph, this.element, this.direction);
            }
        }

        return NO_INVOCATION_PATH;
    }


    private Integer proxyHashCode(final Object proxy) {
        return this.element.hashCode();
    }

    private Boolean proxyEquals(final Object proxy, final Object other) {
        if (Proxy.isProxyClass(other.getClass())) {
            return ((FramedElement) (Proxy.getInvocationHandler(proxy))).getElement().equals(((FramedElement) (Proxy.getInvocationHandler(other))).getElement());
        } else if (other instanceof Element) {
            return ElementHelper.areEqual(((FramedElement) (Proxy.getInvocationHandler(proxy))).getElement(), other);
        } else {
            return Boolean.FALSE;
        }
    }

    private String proxyToString(final Object proxy) {
        return ((FramedElement) Proxy.getInvocationHandler(proxy)).getElement().toString();
    }

    public Element getElement() {
        return this.element;
    }
}
