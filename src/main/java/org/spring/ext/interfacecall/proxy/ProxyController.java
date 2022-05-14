package org.spring.ext.interfacecall.proxy;

import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import java.lang.reflect.Field;

/**
 * @author 87260
 */
public class ProxyController extends ServletWrappingController {



    private ProxyRestTemplate restTemplate;
    private String urlMapping;
    private String proxy;


    public ProxyController(ProxyRestTemplate restTemplate, String urlMapping, String proxy) {
        this.restTemplate = restTemplate;
        this.urlMapping = urlMapping;
        this.proxy = proxy;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setServletClass(ProxyServlet.class);
        super.afterPropertiesSet();
        Field filed=ReflectionUtils.findField(ServletWrappingController.class,"servletInstance");
        filed.setAccessible(true);
        ProxyServlet proxyServlet= (ProxyServlet) filed.get(this);
        proxyServlet.setProxy(proxy);
        proxyServlet.setRestTemplate(restTemplate);
        proxyServlet.setUrlMapping(urlMapping);


    }

}
