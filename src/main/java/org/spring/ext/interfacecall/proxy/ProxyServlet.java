package org.spring.ext.interfacecall.proxy;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author 87260
 */
public class ProxyServlet extends HttpServlet {

    private RestTemplate restTemplate;
    private String urlMapping;
    private String proxy;

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getUrlMapping() {
        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public ProxyServlet() {

    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpHeaders headers=new HttpHeaders();
        Enumeration<String> headerNames= req.getHeaderNames();
         while(headerNames.hasMoreElements()){
             String headerName=headerNames.nextElement();
             headers.add(headerName,req.getHeader(headerName));
        }
         String paramJson = StreamUtils.copyToString(req.getInputStream(), Charset.forName("UTF-8"));
         headers.remove("accept-encoding");
         HttpEntity formEntity = new HttpEntity(paramJson,headers);
         ResponseEntity<String> responseEntity=restTemplate.exchange(proxy,HttpMethod.resolve(req.getMethod()),formEntity,String.class,req.getParameterMap());
         HttpHeaders responseEntityHeaders=responseEntity.getHeaders();
         resp.setHeader("Content-Type",responseEntityHeaders.get("Content-Type").get(0));
         resp.setStatus(responseEntity.getStatusCodeValue());
         resp.getOutputStream().write(responseEntity.getBody().toString().getBytes(StandardCharsets.UTF_8));

    }
}
