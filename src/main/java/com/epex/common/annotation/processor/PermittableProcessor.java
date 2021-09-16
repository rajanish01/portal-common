package com.epex.common.annotation.processor;

import com.epex.common.annotation.Permittable;
import com.epex.common.domain.auth.PermittableDTO;
import com.google.common.collect.Sets;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermittableProcessor {

    public Set<PermittableDTO> getPermittables(List<String> packages) {
        Set<PermittableDTO> permittableDTOS = Sets.newHashSet();
        try {
            if (packages.isEmpty()) return permittableDTOS;

            Set<Class> classes = Sets.newHashSet();
            packages.forEach(packageName -> {
                InputStream stream = ClassLoader.getSystemClassLoader()
                        .getResourceAsStream(packageName.replaceAll("[.]", "/"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                classes.addAll(reader.lines()
                        .filter(line -> line.endsWith(".class"))
                        .map(line -> getClass(line, packageName))
                        .collect(Collectors.toSet()));
            });
            classes.forEach(aClass -> {
                Class<?> obj = aClass;
                for (Method method : obj.getMethods()) {
                    if (method.isAnnotationPresent(Permittable.class)) {
                        Permittable permAnnotation = method.getAnnotation(Permittable.class);
                        String url = Stream.of(GetMapping.class, PutMapping.class, PostMapping.class,
                                        PatchMapping.class, DeleteMapping.class, RequestMapping.class)
                                .filter(method::isAnnotationPresent)
                                .map(clazz -> getUrl(method, clazz))
                                .findFirst().orElse(null);
                        String mappingType = Stream.of(GetMapping.class, PutMapping.class, PostMapping.class,
                                        PatchMapping.class, DeleteMapping.class, RequestMapping.class)
                                .filter(method::isAnnotationPresent)
                                .map(clazz -> getMappingType(method, clazz))
                                .findFirst().orElse(null);

                        permittableDTOS.add(new PermittableDTO(permAnnotation.groupId(),
                                mappingType, url, permAnnotation.accessLevel().name()));
                    }
                }
            });
        } catch (Exception ignored) {
        }
        return permittableDTOS;
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    private String getUrl(Method method, Class<? extends Annotation> annotationClass) {
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        try {
            value = (String[]) annotationClass.getMethod("value").invoke(annotation);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
        return value[0];
    }

    private String getMappingType(Method method, Class<? extends Annotation> annotationClass) {
        Annotation annotation = method.getAnnotation(annotationClass);
        try {
            if (annotationClass.isInstance(GetMapping.class)) {
                return "GET";
            } else if (annotationClass.isInstance(PostMapping.class)) {
                return "POST";
            } else if (annotationClass.isInstance(PutMapping.class)) {
                return "PUT";
            } else if (annotationClass.isInstance(PatchMapping.class)) {
                return "PATCH";
            } else if (annotationClass.isInstance(DeleteMapping.class)) {
                return "DELETE";
            } else if (annotationClass.isInstance(RequestMapping.class)) {
                return (String) annotationClass.getMethod("method").invoke(annotation);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
