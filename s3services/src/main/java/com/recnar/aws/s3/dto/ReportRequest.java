package com.recnar.aws.s3.dto;

import java.util.Map;

public class ReportRequest {

    private String nombreReporte;
    private Map<String, Object> parameters;

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
