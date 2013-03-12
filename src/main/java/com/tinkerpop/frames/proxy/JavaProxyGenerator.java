package com.tinkerpop.frames.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.annotations.AnnotationHandler;

/**
 * Proxy generator that uses the inbuild java proxy mechanisms for creating framedElement proxies
 *
 * @author Greg Bowyer
 */
public class JavaProxyGenerator implements ProxyGenerator {

    @Override
    public <F> F generate(Class<F> clazz, FramedElement framedElement, Map<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>> annotationHandlers) {
        return (F) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new JavaProxyWrapper(framedElement));
    }

    private static final class JavaProxyWrapper implements InvocationHandler {
        private final FramedElement delegate;

        private JavaProxyWrapper(FramedElement delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return this.delegate.invoke(proxy, method, args);
        }

    }

}
