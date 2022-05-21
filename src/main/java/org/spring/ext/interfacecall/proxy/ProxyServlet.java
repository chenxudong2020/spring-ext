package org.spring.ext.interfacecall.proxy;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
        if (req instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest request=(MultipartHttpServletRequest)req;
            this.uploadDispatch(req, resp, proxy,getMultiValueMap(request));
        } else if (req instanceof MultipartRequest) {
            MultipartRequest request=(MultipartRequest)req;
            this.uploadDispatch(req, resp, proxy,getMultiValueMap(request));
        } else {
            this.doDispatch(req, resp, proxy);
        }


    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse res, String toUrl) throws ServletException {
        RequestEntity<byte[]> requestEntity = null;
        try {
            requestEntity = this.createRequestEntity(req, toUrl);
        } catch (URISyntaxException | IOException e) {
            throw new ServletException(e);
        }
        ResponseEntity<byte[]> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(requestEntity, byte[].class);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        // 开始执行跳转
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();
            for (String headerValue : headerValues) {
                res.addHeader(headerName, headerValue);
            }
        }
        if (responseEntity.hasBody()) {
            ServletOutputStream outputStream = null;
            try {
                outputStream = res.getOutputStream();
                outputStream.write(responseEntity.getBody());
                outputStream.flush();
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }


    private MultiValueMap<String, Object> getMultiValueMap(MultipartRequest req){
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        MultiValueMap<String, MultipartFile> map=req.getMultiFileMap();
        map.forEach((x,y)->{
            parts.add(x,y);
        });
        return parts;
    }

    private MultiValueMap<String, Object> getMultiValueMap(MultipartHttpServletRequest req){
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        MultiValueMap<String, MultipartFile> map=req.getMultiFileMap();
        map.forEach((x,y)->{
            parts.add(x,y);
        });
        Map<String, String[]> parameterMap=req.getParameterMap();
        parameterMap.forEach((x,y)->{
            parts.add(x,y);
        });
        return parts;
    }


    public void uploadDispatch(ServletRequest request, ServletResponse response, String toUrl,MultiValueMap<String, Object> parts)
            throws IOException, ServletException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> mutiReq = new HttpEntity<>(parts, headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(toUrl, HttpMethod.POST, mutiReq, byte[].class,
                new HashMap<String, Object>());
        if (responseEntity.hasBody()) {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(responseEntity.getBody());
                outputStream.flush();
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }


    private RequestEntity<byte[]> createRequestEntity(HttpServletRequest request, String url)
            throws URISyntaxException, IOException {
        String method = request.getMethod();
        // 1、封装请求头
        HttpMethod httpMethod = HttpMethod.resolve(method);
        // 2、封装请求体
        MultiValueMap<String, String> headers = createRequestHeaders(request);
        // 3、构造出RestTemplate能识别的RequestEntity
        byte[] body = createRequestBody(request);
        RequestEntity<byte[]> requestEntity = new RequestEntity<byte[]>(body, headers, httpMethod, new URI(url));
        return requestEntity;
    }

    private byte[] createRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    private MultiValueMap<String, String> createRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headerValue : headerValues) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }

}
