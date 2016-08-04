package com.tinkerpop.frames.annotations;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.tinkerpop.frames.ClassUtilities.isAddMethod;
import static com.tinkerpop.frames.ClassUtilities.isGetMethod;
import static com.tinkerpop.frames.ClassUtilities.isHasMethod;
import static com.tinkerpop.frames.ClassUtilities.isIterable;
import static com.tinkerpop.frames.ClassUtilities.isRemoveMethod;
import static com.tinkerpop.frames.ClassUtilities.isSetMethod;
import static com.tinkerpop.frames.ClassUtilities.returnsIterable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;

public class PropertyAnnotationHandler implements AnnotationHandler<Property> {

    @Override
    public Class<Property> getAnnotationType() {
        return Property.class;
    }

    @Override
    public Object processElement(final Property annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Element element, final Direction direction) {
      String property = annotation.value();

      if (isGetMethod(method)) {
        return processGet(element, property, method);
      } else if (isAddMethod(method)) {
        processAdd(element, property, arguments[0]);
      } else if (isSetMethod(method)) {
        element.removeProperty(property);
        processAdd(element, property, arguments[0]); 
      } else if (isHasMethod(method)) {
        return hasProperty(element, property);
      } else if (isRemoveMethod(method)) {
        element.removeProperty(property);
      }

      return null;
    }

    boolean hasProperty(Element node, String property) {
      return (null != node.getProperty(property));
    }

    Object checkClass(Class<?> klass, Object object) {
      if (!klass.isAssignableFrom(object.getClass())) {
        throw new ClassCastException();
      }
      return object;
    }
    
    void processAdd(Element node, String property, Object value) {
      if (null != value) {
        if (hasProperty(node, property)) {
          Object originalValue = (Object)node.getProperty(property);
          Set<Object> valueSet = new LinkedHashSet<Object>();
          if (isIterable(originalValue)) {
            valueSet = newLinkedHashSet((Iterable<?>)originalValue);
          } else {
            valueSet.add(originalValue);
          }
          Class<?> klass = getFirst(valueSet, null).getClass();
          if (isIterable(value)) {
            for (Object val: (Iterable<?>) value) {
              valueSet.add(checkClass(klass, val));
            }
          } else {
            valueSet.add(checkClass(klass, value));
          }
          node.setProperty(property, valueSet);
        } else {
          if (isIterable(value)) {
            for (Object o: (Iterable<?>) value) {
              processAdd(node, property, o);
            }
          } else {
            node.setProperty(property, value);
          }
        }
      }
    }

    Object processGet(Element node, String property, Method method) {
      if (returnsIterable(method)) {
        if (!hasProperty(node, property)) {
          return Collections.emptySet();
        } else if (isIterable(node.getProperty(property))) {
          return (Set<?>)node.getProperty(property);
        } else {
          return newHashSet(node.getProperty(property));
        }
      } else {
        if (!hasProperty(node, property)) {
          return null;
        } else if (isIterable(node.getProperty(property))) {
          throw new IllegalStateException("Can't call " + method.getName() + " when its property is multivalued");
        } else {
          return node.getProperty(property);
        }
      }
    }

}
