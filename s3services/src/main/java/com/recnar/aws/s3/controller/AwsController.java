package com.recnar.aws.s3.controller;

import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import com.recnar.aws.s3.dto.RequestAwsS3;
import com.recnar.aws.s3.dto.GenericResponse;
import com.recnar.aws.s3.dto.ResponseUploadFile;
import com.recnar.aws.s3.service.IAwsS3Services;
import com.recnar.aws.s3.utils.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value="/s3")
public class AwsController {

    private static final Logger LOGGER = LogManager.getLogger(AwsController.class);

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private IAwsS3Services iAwsS3Services;

    GenericResponse<String> genericResponse = new GenericResponse<>();
    ResponseEntity<GenericResponse<String>> response;

    @PostMapping(value = "/upload",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseUploadFile> uploadFile(@RequestBody RequestAwsS3 request) throws IOException {

        LOGGER.info("REQUEST /upload: "+ Util.objectToJsonString(request.getNombreArchivo()));
        ResponseEntity<ResponseUploadFile> resp;
        ResponseUploadFile responseUploadFile = new ResponseUploadFile();
        responseUploadFile = iAwsS3Services.uploadFile(request,responseUploadFile);
        responseUploadFile.setMessage("Archivo cargado correctamente");
        responseUploadFile.setStatusCode(HttpStatus.OK);
        responseUploadFile.setServiceName(serviceName);
        resp = new ResponseEntity(responseUploadFile, HttpStatus.OK);

        return resp;
    }

    @PostMapping(value = "/list")
    public  ResponseEntity<List<ArchivosRecnar>> listFiles(@RequestParam("codigo") String codigo) {
        List<ArchivosRecnar> resp = iAwsS3Services.getArchivoRecnar(codigo);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<GenericResponse<String>> download(@RequestParam("id")  Long id) throws IOException {

        InputStream resource = iAwsS3Services.downloadFile(id);
        byte[] sourceBytes = IOUtils.toByteArray(resource);
        String encoded = Base64.getEncoder().encodeToString(sourceBytes);

        genericResponse.setMessage(encoded);
        genericResponse.setStatusCode(HttpStatus.OK);
        genericResponse.setServiceName(serviceName);
        response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        return response;


    }

    @PostMapping(value = "/file-parent-code")
    public  ResponseEntity<List<ArchivosRecnar>> getFileParentCode(@RequestParam("codigo_padre") String codigoPadre) {
        List<ArchivosRecnar> resp = iAwsS3Services.getFileParentCode(codigoPadre);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/restore-files")
    public  ResponseEntity<GenericResponse<String>> restoreFiles(@RequestParam("id_detalle_orden_ingreso") Long idDetalle) {
        iAwsS3Services.restoreFiles(idDetalle);

        genericResponse.setMessage("Proceso de restauración ejecutado correctamente");
        genericResponse.setStatusCode(HttpStatus.OK);
        genericResponse.setServiceName(serviceName);
        response = new ResponseEntity<>(genericResponse, HttpStatus.OK);
        return response;
    }

}
