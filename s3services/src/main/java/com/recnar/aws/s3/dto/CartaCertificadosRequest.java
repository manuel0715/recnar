package com.recnar.aws.s3.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartaCertificadosRequest {
    private String nombre;
    private String identificacion;
    private String codigoCertificado;
    private String fecha;
    private String anio;
}
