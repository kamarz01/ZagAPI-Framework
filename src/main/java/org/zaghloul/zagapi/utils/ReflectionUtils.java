package org.zaghloul.zagapi.utils;

import org.reflections.Reflections;
import org.zaghloul.zagapi.annotations.ZagREST;

import java.util.Objects;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public class ReflectionUtils {

    public Set<Class<?>> getClassesAnnotatedWithFrameworkAnnotation(String apiClassesPackageName){
        Reflections reflections = Objects.isNull(apiClassesPackageName) ? new Reflections() : new Reflections(apiClassesPackageName);
        return  reflections.get(SubTypes.of(TypesAnnotated.with(ZagREST.class)).asClass());
    }
}
