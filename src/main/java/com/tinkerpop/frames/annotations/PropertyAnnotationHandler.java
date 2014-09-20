package com.tinkerpop.frames.annotations;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newArrayList;
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
        /*if (ClassUtilities.isGetMethod(method)) {
            Object value = element.getProperty(annotation.value());
            if (method.getReturnType().isEnum())
                return getValueAsEnum(method, value);
            else
                return value;
        } else if (ClassUtilities.isSetMethod(method)) {
            Object value = arguments[0];
            if (null == value) {
                element.removeProperty(annotation.value());
            } else {
                if (value.getClass().isEnum()) {
                    element.setProperty(annotation.value(), ((Enum<?>) value).name());
                } else {
                    element.setProperty(annotation.value(), value);
                }
            }
            return null;
        } else if (ClassUtilities.isRemoveMethod(method)) {
            element.removeProperty(annotation.value());
            return null;
        }*/
      String property = annotation.value();

      if (isGetMethod(method)) {
        return processGet(element, property, method);
      } else if (isAddMethod(method)) {
        processAdd(element, property, arguments[0], method);
      } else if (isSetMethod(method)) {
        element.removeProperty(property);
        processAdd(element, property, arguments[0], method); 
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
    
    void processAdd(Element node, String property, Object value, Method method) {
      if (null == value) {
        node.removeProperty(property);
      } else if (hasProperty(node, property)) {
        Object originalValue = (Object)node.getProperty(property);
        Set<Object> originalCollection = new LinkedHashSet<Object>();
        if (isIterable(originalValue)) {
          originalCollection = newLinkedHashSet((Iterable<?>)originalValue);
        } else {
          originalCollection.add(originalValue);
        }
        Class<?> klass = getFirst(originalCollection, null).getClass();
        if (isIterable(value)) {
          for (Object val: (Iterable<?>)value) {
            originalCollection.add(checkClass(klass, val));
          }
        } else {
          originalCollection.add(checkClass(klass, value));
        }
        node.setProperty(property, originalCollection);
      } else {
        if (isIterable(value)) {
          for (Object o: (Iterable<?>)value) {
            processAdd(node, property, o, method);
          }
        } else {
          node.setProperty(property, value);
        }
      }
    }

    Object processGet(Element node, String property, Method method) {
      if (returnsIterable(method)) {
        if (null == node.getProperty(property)) {
          return Collections.emptySet();
        } else if (isIterable(node.getProperty(property))) {
          return (Set<?>)node.getProperty(property);
        } else {
          return newHashSet(node.getProperty(property));
        }
      } else {
        if (null == node.getProperty(property)) {
          return null;
        } else if (isIterable(node.getProperty(property))) {
          throw new IllegalStateException("Can't call " + method.getName() + " when its property is multivalued");
        } else {
          return node.getProperty(property);
        }
      }
    }
    

    protected Enum getValueAsEnum(final Method method, final Object value) {
        Class<Enum> en = (Class<Enum>) method.getReturnType();
        if (value != null)
            return Enum.valueOf(en, value.toString());

        return null;
    }
}
