package Test.AnotherTest;

import org.zaghloul.zagapi.annotations.ZagREST;
import org.zaghloul.zagapi.annotations.ZagRequest;
import org.zaghloul.zagapi.core.http.request.ZagMethod;

@ZagREST("testAPI-form.json")
public class TestClass3 {
    @ZagRequest("request-form")
    public static ZagMethod testMethod2;
}
