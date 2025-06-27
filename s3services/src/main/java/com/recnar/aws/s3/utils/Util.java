package com.recnar.aws.s3.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import com.recnar.aws.s3.domain.GeneralErrorException;
import com.recnar.aws.s3.dto.RequestAwsS3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Util {

    public static ArchivosRecnar buildArchivosRecnar(RequestAwsS3 request){
        ArchivosRecnar archivosS3 = new ArchivosRecnar();

        archivosS3.setEstado("");
        archivosS3.setModulo(request.getModulo());
        archivosS3.setNombreProceso(request.getNombreProceso());
        archivosS3.setCodigo(request.getCodigo());
        archivosS3.setNombreArchivo(request.getNombreArchivo());
        archivosS3.setExtensionArchivo(request.getExtensionArchivo());
        archivosS3.setBucketAws3(request.getExtensionArchivo());
        archivosS3.setBucketAws3(request.getBucketAwsS3());
        archivosS3.setPathAwsS3(request.getPathAwsS3());
        archivosS3.setUriAwsS3(request.getUriAwsS3());
        archivosS3.setFullPathAwsS3(request.getFullPathAwsS3());
        archivosS3.setUsuarioCreador(request.getUsuarioCreador());
        archivosS3.setUsuarioUltimaModificacion(request.getUsuarioCreador());
        archivosS3.setCodigoPadre(request.getCodigoPadre());
        return archivosS3;

    }

    public static String objectToJsonString(Object var1) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(var1);

        } catch (Exception e) {
            throw new GeneralErrorException("Error: "+e);
        }
    }

    public static String getExtensionFromMimeTypeMap(String fileType) {
        Map<String, String> mimeTypeMap = new HashMap<>();
        mimeTypeMap.put("data:application/pdf", "pdf");
        mimeTypeMap.put("data:application/msword", "doc");
        mimeTypeMap.put("data:application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        mimeTypeMap.put("data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        mimeTypeMap.put("data:application/vnd.ms-excel", "xls");
        mimeTypeMap.put("data:image/jpg", "jpg");
        mimeTypeMap.put("data:image/jpeg", "jpeg");
        mimeTypeMap.put("data:image/png", "png");
        mimeTypeMap.put("data:application/octet-stream", "pgp");
        mimeTypeMap.put("data:text/plain", "txt");
        mimeTypeMap.put("data:audio/ogg","ogg");
        mimeTypeMap.put("data:audio/mpeg","mp3");
        mimeTypeMap.put("data:video/mp4","mp4");
        mimeTypeMap.put("data:audio/aac","aac");
        return mimeTypeMap.get(fileType);
    }

    public static byte[] mergePdfDocuments(List<byte[]> pdfByteArrays) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, outputStream);
        document.open();

        for (byte[] pdfBytes : pdfByteArrays) {
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                copy.addPage(copy.getImportedPage(reader, i));
            }
            reader.close();
        }
        document.close();
        return outputStream.toByteArray();
    }
}
