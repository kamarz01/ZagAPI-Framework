package Test;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.zaghloul.zagapi.core.engine.ZagAPI;

@Slf4j
public class TestRunner {
    public static void main(String[] args){
        //ZagAPI.init(TestClass.class);
        ZagAPI.init("Test");
        //ZagAPI.init();
        Response response = TestClass.testMethod.getResponse();
        response.prettyPrint();
    }

}
