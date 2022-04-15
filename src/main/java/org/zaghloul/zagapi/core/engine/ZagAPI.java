package org.zaghloul.zagapi.core.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.ValidationException;
import org.zaghloul.zagapi.constant.FileConstant;
import org.zaghloul.zagapi.constant.RequestConstant;
import org.zaghloul.zagapi.core.domain.ZagEnvironment;
import org.zaghloul.zagapi.core.domain.ZagEnvironmentData;
import org.zaghloul.zagapi.core.http.proxy.ProxyHandler;
import org.zaghloul.zagapi.core.transformers.Transformer;
import org.zaghloul.zagapi.exception.ZagAPIException;
import org.zaghloul.zagapi.annotations.ZagREST;
import org.zaghloul.zagapi.core.domain.ZagRequest;
import org.zaghloul.zagapi.core.domain.ZagRestSpec;
import org.zaghloul.zagapi.core.http.request.HttpMethod;
import org.zaghloul.zagapi.core.http.request.RequestHandler;
import org.zaghloul.zagapi.core.http.request.ZagMethod;
import org.zaghloul.zagapi.utils.AnnotationUtils;
import org.zaghloul.zagapi.utils.JsonUtils;
import org.zaghloul.zagapi.utils.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.isStatic;

@Slf4j
public class ZagAPI {

    private static AnnotationUtils annotationUtils;
    private static RequestHandler requestHandler;
    private static ProxyHandler proxyHandler;
    private static ReflectionUtils reflectionUtils;
    private static JsonUtils jsonUtils;
    private static Transformer transformer;


    public static void init(String environmentName){
        try {
            preInit();
            handleFrameworkClasses(null,environmentName);
        }catch (Exception e){
            throw new ZagAPIException(String.format("ZagAPI execution failed, %s",e.getMessage()));
        }
    }

    public static void init(String apiClassesPackageName,String environmentName){
        try {
            preInit();
            handleFrameworkClasses(apiClassesPackageName,environmentName);
        }catch (Exception e){
            throw new ZagAPIException(String.format("ZagAPI execution failed, %s",e.getMessage()));
        }
    }

    public static <T> T init(Class<T> klass,String environmentName) {
        try {
            preInit();
            return handleInit(klass,environmentName);
        }catch (Exception e){
            throw new ZagAPIException(String.format("ZagAPI execution failed, %s",e.getMessage()));
        }
    }

    private static <T> T handleInit(Class<T> klass,String envName){
        handleFrameworkAnnotation(klass);
        verifyAndHandleEnvironment(envName);
        handleAPISchemaValidation(klass);
        return handleZagRequestMethods(klass, mapAPIDefinitionFile(klass));
    }

    private static void preInit(){
        transformer = new Transformer();
        annotationUtils = new AnnotationUtils();
        requestHandler = new RequestHandler();
        reflectionUtils = new ReflectionUtils();
        proxyHandler = new ProxyHandler();
        jsonUtils = new JsonUtils(transformer);
        log.info("Starting ZagRest API Framework...");
    }

    private static void handleFrameworkClasses(String apiClassesPackageName,String environmentName){
        Set<Class<?>> zagRestClasses = reflectionUtils.getClassesAnnotatedWithFrameworkAnnotation(apiClassesPackageName);
        for (Class<?> klass:zagRestClasses) {
            handleInit(klass,environmentName);
        }
    }

    private static <T> void handleFrameworkAnnotation(Class<T> klass){
        if (!annotationUtils.checkFrameworkAnnotation(klass))
            throw new RuntimeException(String.format("Class '%s' is not a valid ZagRest annotated class or does not have an annotation value for @ZagREST", klass.getSimpleName()));
    }

    private static <T> void handleAPISchemaValidation(Class<T> klass){
        try {
            jsonUtils.validateJsonAPISchema(klass.getAnnotation(ZagREST.class).value());
        }catch (IOException e){
            throw new RuntimeException(String.format("API Definition file for class '%s' is not found or not in a correct format -> %s.", klass.getSimpleName(),e.getMessage()));
        }catch (ValidationException e){
            throw new RuntimeException(String.format("API Definition file validation failed for for class '%s' because of: %s.", klass.getSimpleName(),e.getMessage()));
        }
    }

    private static void verifyAndHandleEnvironment(String envName) {
        handleEnvironmentSchemaValidation();
        ZagEnvironment env = mapEnvironmentFile();
        Optional<ZagEnvironmentData> targetEnv = env.getEnvironments().stream().filter(te -> te.getEnvironment().equals(envName)).findFirst();
        if (!targetEnv.isPresent())
            throw new RuntimeException(String.format("Environment '%s' is not found on environments list", envName));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> envData = mapper.convertValue(targetEnv.get().getData(), new TypeReference<Map<String, String>>(){});
        envData.put("envName",envName);
        envData.put("baseURL",targetEnv.get().getBaseURL());
        transformer.addAllToEnv(envData);
    }

    private static void handleEnvironmentSchemaValidation(){
        String envFile = FileConstant.ENVIRONMENT_FILE;
        try {
            jsonUtils.validateJsonEnvironmentSchema(envFile);
        }catch (IOException e){
            throw new RuntimeException(String.format("Environment file for file '%s' is not found or not in a correct format -> %s.", envFile,e.getMessage()));
        }catch (ValidationException e){
            throw new RuntimeException(String.format("Environment file validation failed for for file '%s' because of: %s.", envFile,e.getMessage()));
        }
    }

    private static ZagEnvironment mapEnvironmentFile(){
        try {
            return jsonUtils.mapJsonToJavaClass(FileConstant.ENVIRONMENT_FILE, ZagEnvironment.class);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Environment file mapping failed for file '%s' because of: %s.",FileConstant.ENVIRONMENT_FILE,e.getMessage()));
        }
    }

    private static <T> ZagRestSpec mapAPIDefinitionFile(Class<T> klass){
        try {
            return jsonUtils.mapJsonAPIDefinitionToJavaClass(klass.getAnnotation(ZagREST.class).value(), ZagRestSpec.class);
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
        ZagMethod method = new ZagMethod(transformer);
        method.setMethod(HttpMethod.valueOf(requestData.getMethod()));
        method.setEndPoint(transformValueIfAny(requestData.getEndPoint()));
        JsonNode requestBody = requestData.getRequestData();
        method.getData().body = requestBody.get(RequestConstant.BODY);
        requestHandler.handleQueryParameters(requestBody,method);
        requestHandler.handleQueryParametersInURI(requestData.getEndPoint(),method);
        requestHandler.handleFormParameters(requestBody,method);
        requestHandler.handleHeaders(requestBody,method);
        proxyHandler.handleRequestProxy(apiSpec,method);
        return method;
    }

    private static String transformValueIfAny(String source){
        return transformer.transform(source);
    }
}
