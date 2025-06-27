package com.recnar.aws.s3.service;

import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import com.recnar.aws.s3.dto.CartaCertificadosRequest;
import com.recnar.aws.s3.dto.ReportRequest;

import java.util.List;

public interface IReportServices {


    String generarCertificacionFinal(int idDetalleOrdenIngreso);

    String genererarRemision(String codigoSolicitud);

    String generarCartaCertificado(CartaCertificadosRequest cartaCertificadosRequest);

    String generarReporte(ReportRequest reportRequest);
}
