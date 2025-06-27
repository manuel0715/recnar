package com.recnar.aws.s3.dto;

import org.springframework.http.HttpStatus;


public class ResponseUploadFile {

    private String serviceName;
    private HttpStatus statusCode;
    private String message;
    private String nombreArchivoCargado;
    private String pathS3;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNombreArchivoCargado() {
        return nombreArchivoCargado;
    }

    public void setNombreArchivoCargado(String nombreArchivoCargado) {
        this.nombreArchivoCargado = nombreArchivoCargado;
    }

    public String getPathS3() {
        return pathS3;
    }

    public void setPathS3(String pathS3) {
        this.pathS3 = pathS3;
    }
}
