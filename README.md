# spring-ext
支持
Body
Cache
Get
Head
InterfaceClient
POST
Query
Url
注解
spring-ext 快速调用第三方接口的框架

<dependency>
<groupId>org.spring.boot.extender</groupId>
<artifactId>spring-boot-extender</artifactId>
<version>1.0-SNAPSHOT</version>
</dependency>

只需要写个接口

@InterfaceClient("${baseUrl}")
public interface RemoteHttpApI {

    @POST
Result getUser(Input input, @Url String url);

    @POST("/getUser2")
    @Cache(expire = 10)
    Result getUser2(@Query("name") String name,@Query("aaa") String aaa);
}


然后注入到Spring容器中例如
public class Test {

    @Resource
    RemoteHttpApI remoteHttpApI;

    @Value("${baseUrl}/${getUser}")
    String url;

    @org.junit.Test
    public void test(){
        Input input=new Input();
        input.setId("1");
        Result result=remoteHttpApI.getUser(input,url);
        String name=result.getData().getName();
        System.out.println(name);
    }

    @org.junit.Test
    public void test2(){
        String name="asdadsasd";
        Result result=remoteHttpApI.getUser2(name,"AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Result result2=remoteHttpApI.getUser2(name,"AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(result.getData().getName());
    }
}
直接调用
