package com.tinkerpop.frames.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * TODO:: WRITE ME !
 *
 * @author Greg Bowyer
 */
public class CGLibProxyGenerator implements ProxyGenerator {

    private final WeakHashMap<Class, Factory> classCache = new WeakHashMap<Class, Factory>();

    @Override
    public <F> F generate(Class<F> clazz, final FramedElement framedElement, Map<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>> annotationHandlers) {
        synchronized (classCache) {
            if (!classCache.containsKey(clazz)) {
                Enhancer enhancer = new Enhancer();
                enhancer.setClassLoader(clazz.getClassLoader());
                enhancer.setSuperclass(clazz);
                enhancer.setCallback(new TinkerPopInterceptor(null));
                Factory factory = (Factory) enhancer.create();
                classCache.put(clazz, factory);
            }
        }

        return (F) classCache.get(clazz).newInstance(new TinkerPopInterceptor(framedElement));
    }

    private static final class TinkerPopInterceptor implements MethodInterceptor {
        private final FramedElement framedElement;

        public TinkerPopInterceptor(FramedElement framedElement) {
            this.framedElement = framedElement;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return framedElement.invoke(obj, method, args);
        }
    }
}
