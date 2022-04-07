package Test.Runner;

import Test.AnotherTest.TestClass3;
import Test.TestClass;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.zaghloul.zagapi.core.runner.ZagAPI;

@Slf4j
public class TestRunner {
    public static void main(String[] args){
        //ZagAPI.init(TestClass.class);
        //ZagAPI.init();
        ZagAPI.init("Test");
        Response response = TestClass.testMethod.getResponse();
        Response response2 = TestClass3.testMethod2.getResponse();
        response.prettyPrint();
        response2.prettyPrint();
    }

}
