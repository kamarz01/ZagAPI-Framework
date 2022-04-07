package org.zaghloul.zagapi.core.runner;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.ValidationException;
import org.zaghloul.zagapi.constant.RequestConstant;
import org.zaghloul.zagapi.exception.ZagAPIException;
import org.zaghloul.zagapi.annotations.ZagREST;
import org.zaghloul.zagapi.core.domain.ZagRequest;
import org.zaghloul.zagapi.core.domain.ZagRestSpec;
import org.zaghloul.zagapi.core.http.request.HttpMethod;
import org.zaghloul.zagapi.core.http.request.RequestHandler;
import org.zaghloul.zagapi.core.http.request.ZagMethod;
import org.zaghloul.zagapi.utils.AnnotationUtils;
import org.zaghloul.zagapi.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.isStatic;

@Slf4j
public class ZagAPI {

    private static AnnotationUtils annotationUtils;
    private static RequestHandler requestHandler;
    private static JsonUtils jsonUtils;

    public static <T> T init(Class<T> klass) {
        try {
            return handleInit(klass);
        }catch (Exception e){
            throw new ZagAPIException(String.format("ZagAPI execution failed, %s",e.getMessage()));
        }
    }

    private static <T> T handleInit(Class<T> klass){
        preInit();
        handleFrameworkAnnotation(klass);
        handleSchemaValidation(klass);
        return handleZagRequestMethods(klass,handleFileMapping(klass));
    }

    public static void preInit(){
        annotationUtils = new AnnotationUtils();
        requestHandler = new RequestHandler();
        jsonUtils = new JsonUtils();
        log.info("Starting ZagRest API Framework...");
    }

    private static <T> void handleFrameworkAnnotation(Class<T> klass){
        if (!annotationUtils.checkFrameworkAnnotation(klass))
            throw new RuntimeException(String.format("Class '%s' is not a valid ZagRest annotated class or does not have an annotation value for @ZagREST", klass.getSimpleName()));
    }

    private static <T> void handleSchemaValidation(Class<T> klass){
        try {
            jsonUtils.validateJsonAPISchema(klass.getAnnotation(ZagREST.class).value());
        }catch (IOException e){
            throw new RuntimeException(String.format("API Definition file for class '%s' is not found or not in a correct format -> %s.", klass.getSimpleName(),e.getMessage()));
        }catch (ValidationException e){
            throw new RuntimeException(String.format("API Definition file validation failed for for class '%s' because of: %s.", klass.getSimpleName(),e.getMessage()));
        }
    }

    private static <T> ZagRestSpec handleFileMapping(Class<T> klass){
        try {
            return jsonUtils.mapJsonToJavaClass(klass.getAnnotation(ZagREST.class).value(), ZagRestSpec.class);
        }catch (Exception e){
            throw new RuntimeException(String.format("API Definition file mapping failed for for class '%s' because of: %s.", klass.getSimpleName(),e.getMessage()));
        }
    }

    private static <T> T handleZagRequestMethods(Class<T> klass, ZagRestSpec apiSpec) {
        T classInstance = getClassInstance(klass);
        for (Field method : getZagMethodsFromClass(klass)) {
            try {
                ZagMethod httpMethod = handleZagMethodData(method,apiSpec);
                if (isStatic(method.getModifiers())){
                    method.set(null,httpMethod);
                }else{
                    method.set(classInstance,httpMethod);
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("Can't configure method %s for class %s because method does not have @ZagRequest Annotation or it's value or not found, %s.", method.getName(), klass.getName(),e.getMessage()));
            }
        }
        return classInstance;
    }

    private static <T> T getClassInstance(Class<T> klass) {
        try {
            return klass.newInstance();
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException(String.format("Can't create an instance of class %s, class should have empty or default constructor.", klass.getName()));
        }
    }

    private static <T> List<Field> getZagMethodsFromClass(Class<T> klass) {
        return Arrays.stream(klass.getDeclaredFields()).filter(f -> f.getType().equals(ZagMethod.class)).collect(Collectors.toList());
    }

    private static ZagMethod handleZagMethodData(Field field,ZagRestSpec apiSpec) {
        //TODO: fix key/value pairs get sorted in reverse order by default
        field.setAccessible(true);
        String reqId = annotationUtils.getZagRequestAnnotationRequestId(field);
        ZagRequest requestData = annotationUtils.getZagMethodRequestDataById(reqId,apiSpec.getRequests());
        ZagMethod httpRequest = new ZagMethod();
        httpRequest.setMethod(HttpMethod.valueOf(requestData.getMethod()));
        httpRequest.setEndPoint(requestData.getEndPoint());
        JsonNode requestBody = requestData.getRequestData();
        httpRequest.data.body = requestBody.get(RequestConstant.BODY);
        requestHandler.handleQueryParameters(requestBody,httpRequest);
        requestHandler.handleHeaders(requestBody,httpRequest);
        return httpRequest;
    }




}
