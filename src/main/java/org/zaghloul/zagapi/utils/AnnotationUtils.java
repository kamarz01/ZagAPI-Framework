package org.zaghloul.zagapi.utils;

import org.zaghloul.zagapi.annotations.ZagREST;
import org.zaghloul.zagapi.core.domain.ZagRequest;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnnotationUtils {

    public boolean checkFrameworkAnnotation(Class<?> klass) {
        ZagREST frameworkAnnotatedClass = klass.getAnnotation(ZagREST.class);
        if (!Objects.isNull(frameworkAnnotatedClass)) {
            return frameworkAnnotatedClass.value() != null && !frameworkAnnotatedClass.value().equals("");
        }
        return false;
    }

    public ZagRequest getZagMethodRequestDataById(String requestId, List<ZagRequest> requests){
        Optional<ZagRequest> req = requests.stream().filter(r->r.getId().equals(requestId)).findFirst();
        return req.orElse(null);
    }

    public String getZagRequestAnnotationRequestId(Field method){
        org.zaghloul.zagapi.annotations.ZagRequest  annot = method.getAnnotation(org.zaghloul.zagapi.annotations.ZagRequest.class);
        return annot==null ? null : (annot.value() == null ? null : annot.value());
    }
}
