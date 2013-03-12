package com.tinkerpop.frames.proxy;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.annotations.AnnotationHandler;

/**
 * TODO:: WRITE ME !
 *
 * @author Greg Bowyer
 */
public interface ProxyGenerator {

    <F> F generate(Class<F> clazz, FramedElement framedElement, Map<Class<? extends Annotation>, AnnotationHandler<? extends Annotation>> annotationHandlers);
}
