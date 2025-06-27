package com.recnar.aws.s3.service;

import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import com.recnar.aws.s3.dto.RequestAwsS3;
import com.recnar.aws.s3.dto.ResponseUploadFile;
import com.recnar.aws.s3.dto.SearchResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface IAwsS3Services {

    ResponseUploadFile uploadFile(RequestAwsS3 request, ResponseUploadFile responseUploadFile) throws IOException;

    List<String> getObjectsFromS3();

    InputStream downloadFile(Long id);

    List<ArchivosRecnar> getArchivoRecnar(String codigo);

    SearchResponse searchFile(Long id);

    List<ArchivosRecnar> getFileParentCode(String codigoPadre);

    void restoreFiles(Long idDetalle);
}
