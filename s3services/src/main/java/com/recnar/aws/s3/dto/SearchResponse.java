package com.recnar.aws.s3.dto;

import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import org.springframework.http.HttpStatus;

import java.io.InputStream;

public class SearchResponse<T> extends ArchivosRecnar {

    private String serviceName;
    private HttpStatus statusCode;
    InputStream resource;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public InputStream getResource() {
        return resource;
    }

    public void setResource(InputStream resource) {
        this.resource = resource;
    }
}
