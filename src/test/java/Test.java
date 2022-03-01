import org.junit.runner.RunWith;
import org.spring.boot.extender.Main;
import org.spring.boot.extender.invoker.api.RemoteHttpApI;
import org.spring.boot.extender.invoker.bean.Input;
import org.spring.boot.extender.invoker.bean.Result;
import org.spring.boot.extender.invoker.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)

@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test {

    @Resource
    RemoteHttpApI remoteHttpApI;

    @org.junit.Test
    public void test(){
        Input input=new Input();
        input.setId("1");
        Result result=remoteHttpApI.getUser(input);
        String name=result.getData().getName();
        System.out.println(name);
    }

    @org.junit.Test
    public void test2(){
        String name="asdadsasd";
        Result result=remoteHttpApI.getUser2(name);

        System.out.println(result.getData().getName());
    }
}
