package com.recnar.aws.s3.dto;

public interface DetalleOrdenIngresoDTO {

    Long getIdDetalleOrdenIngreso();
    String getCodigoOrdenIngreso();
    String getCodigoIngresoRemoto();
    String getCodigoIngresoLocal();
    String getCodigoDesintegracion();
}
