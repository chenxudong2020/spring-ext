package org.spring.ext.interfacecall.proxy;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 87260
 */
public class UploadResource extends InputStreamResource {

    private long contentLength;


    public UploadResource(InputStream inputStream) {
        super(inputStream);
    }


    @Override
    public long contentLength() throws IOException {
        return this.contentLength;
    }


}