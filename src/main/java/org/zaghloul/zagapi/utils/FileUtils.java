package org.zaghloul.zagapi.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileUtils {

    public  String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding == null ? UTF_8 : encoding);
    }

    public  boolean isValidAbsolutePath(String path) {
        Path actualPath = Paths.get(path).toAbsolutePath();
        File file = actualPath.toFile();
        return file.isAbsolute() && file.exists();
    }

}
