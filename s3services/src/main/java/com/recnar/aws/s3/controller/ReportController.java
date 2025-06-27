package com.recnar.aws.s3.controller;

import com.recnar.aws.s3.dto.CartaCertificadosRequest;
import com.recnar.aws.s3.dto.GenericResponse;
import com.recnar.aws.s3.dto.IdRequest;
import com.recnar.aws.s3.dto.ReportRequest;
import com.recnar.aws.s3.service.IReportServices;
import com.recnar.aws.s3.utils.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@RestController
@RequestMapping(value="/report")
public class ReportController {

    private static final Logger LOGGER = LogManager.getLogger(ReportController.class);

    @Autowired
    private IReportServices iReportServices;


    @Value("${spring.application.name}")
    private String serviceName;

    GenericResponse<String> genericResponse = new GenericResponse<>();
    ResponseEntity<GenericResponse<String>> response;

    @GetMapping(value = "/generar-certificacion-final")
    public ResponseEntity<GenericResponse<String>> generarCertificacionFinal(@RequestParam("id") int idDetalleOrdenIngreso) {
        LOGGER.info("REQUEST /generar-certificacion-final: {}", idDetalleOrdenIngreso);
        String pdfBase64 = iReportServices.generarCertificacionFinal(idDetalleOrdenIngreso);

        genericResponse.setMessage(pdfBase64);
        genericResponse.setStatusCode(HttpStatus.OK);
        genericResponse.setServiceName(serviceName);
        response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        LOGGER.info("RESPONSE /generar-certificacion-final: {}", Util.objectToJsonString(response.getBody().getStatusCode()));
        return response;
    }

    @GetMapping(value = "/generar-remision")
    public ResponseEntity<GenericResponse<String>> genererarRemision(@RequestParam("codigoSolicitud") String codigoSolicitud) {
        LOGGER.info("REQUEST /generar-remision: {}", codigoSolicitud);

        String pdfBase64 = iReportServices.genererarRemision(codigoSolicitud);
        genericResponse.setMessage(pdfBase64);
        genericResponse.setStatusCode(HttpStatus.OK);
        genericResponse.setServiceName(serviceName);
        response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        LOGGER.info("RESPONSE /generar-remision: {}", Util.objectToJsonString(response.getBody().getStatusCode()));
        return response;
    }

    @PostMapping(value = "/generar-certificacion-final-masivo")
    public ResponseEntity<GenericResponse<String>> generarCertificacionFinal(@RequestBody List<IdRequest> idRequests) {

        try {
            List<byte[]> pdfByteArrays = new ArrayList<>();

            for (IdRequest idRequest : idRequests) {
                LOGGER.info("REQUEST /generar-certificacion-final-masiva: {}", idRequest.getId());
                String pdfBase64 = iReportServices.generarCertificacionFinal(idRequest.getId());
                byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
                pdfByteArrays.add(pdfBytes);
            }

            byte[] mergedPdfBytes = Util.mergePdfDocuments(pdfByteArrays);

            String pdfBase64 = Base64.getEncoder().encodeToString(mergedPdfBytes);

            LOGGER.info("RESPONSE /generar-certificacion-final-masiva {}", "GENERADO");

            genericResponse.setMessage(pdfBase64);
            genericResponse.setStatusCode(HttpStatus.OK);
            genericResponse.setServiceName(serviceName);
            response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error al generar la certificación final masiva", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


        return response;
    }

    @PostMapping(value = "/generar-carta-certificado")
    public ResponseEntity<GenericResponse<String>> generarCartaCertificado(@RequestBody CartaCertificadosRequest cartaCertificadosRequest) {
        LOGGER.info("REQUEST /generar-carta-certificado: {}", cartaCertificadosRequest.getNombre());

        String pdfBase64 = iReportServices.generarCartaCertificado(cartaCertificadosRequest);
        genericResponse.setMessage(pdfBase64);
        genericResponse.setStatusCode(HttpStatus.OK);
        genericResponse.setServiceName(serviceName);
        response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        LOGGER.info("RESPONSE /generar-carta-certificado: {}", "GENERADO");
        return response;
    }

    @PostMapping(value = "/generar-reporte")
    public ResponseEntity<GenericResponse<String>> generarReporte(@RequestBody ReportRequest reportRequest) {
        LOGGER.info("REQUEST /generar-reporte: {}", Util.objectToJsonString(reportRequest));

        String pdfBase64 = iReportServices.generarReporte(reportRequest);
        genericResponse.setMessage    (pdfBase64);
        genericResponse.setStatusCode(HttpStatus.OK);
        genericResponse.setServiceName(serviceName);
        response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        LOGGER.info("RESPONSE /generar-reporte: {}", Util.objectToJsonString(response.getBody().getStatusCode()));
        return response;
    }
}
