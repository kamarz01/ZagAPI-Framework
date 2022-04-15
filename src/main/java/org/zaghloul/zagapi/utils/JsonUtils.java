package org.zaghloul.zagapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.zaghloul.zagapi.constant.FileConstant;
import org.zaghloul.zagapi.core.transformers.Transformer;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
@AllArgsConstructor
public class JsonUtils {
    private Transformer transformer;

    public <T> T mapJsonToJavaClass(String path, Class<T> klass) throws IOException {
        return new ObjectMapper().readValue(mapJsonToString(path), klass);
    }

    public <T> T mapJsonAPIDefinitionToJavaClass(String path, Class<T> klass) throws IOException {
        return new ObjectMapper().readValue(transformer.transform(mapJsonToString(path)), klass);
    }

    public String mapJsonToString(String jsonFile) throws IOException {
        FileUtils fileUtils = new FileUtils();
        if (fileUtils.isValidAbsolutePath(jsonFile))
            return fileUtils.readFile(jsonFile, UTF_8);
        return Resources.toString(Resources.getResource(jsonFile), defaultCharset());

    }

    public void validateJsonAPISchema(String path) throws IOException, ValidationException {
        JSONObject jsonSchema = new JSONObject(new JSONTokener(Resources.toString(Resources.getResource(FileConstant.API_SCHEMA_FILE), defaultCharset())));
        JSONObject jsonFile = new JSONObject(new JSONTokener(mapJsonToString(path)));
        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonFile);
    }

    public void validateJsonEnvironmentSchema(String path) throws IOException, ValidationException {
        JSONObject jsonSchema = new JSONObject(new JSONTokener(Resources.toString(Resources.getResource(FileConstant.ENVIRONMENT_SCHEMA_FILE), defaultCharset())));
        JSONArray jsonFile = new JSONArray(new JSONTokener(mapJsonToString(path)));
        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonFile);
    }
}
