package Test;

import org.zaghloul.zagapi.annotations.ZagREST;
import org.zaghloul.zagapi.annotations.ZagRequest;
import org.zaghloul.zagapi.core.http.request.ZagMethod;
import org.zaghloul.zagapi.utils.FileUtils;

@ZagREST("testAPI.json")
public class TestClass {

    @ZagRequest("request-1")
    public static ZagMethod testMethod;
    private FileUtils testMethod2;
}
