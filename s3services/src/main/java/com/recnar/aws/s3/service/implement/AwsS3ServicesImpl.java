package com.recnar.aws.s3.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import com.recnar.aws.s3.db.entity.ConfiguracionServiciosS3;
import com.recnar.aws.s3.db.repository.IArchivosRecnar;
import com.recnar.aws.s3.db.repository.IConfiguracionServiciosS3Repository;
import com.recnar.aws.s3.db.repository.IOrdenIngresoDetalleRepository;
import com.recnar.aws.s3.dto.DetalleOrdenIngresoDTO;
import com.recnar.aws.s3.dto.RequestAwsS3;
import com.recnar.aws.s3.dto.ResponseUploadFile;
import com.recnar.aws.s3.dto.SearchResponse;
import com.recnar.aws.s3.service.IAwsS3Services;
import com.recnar.aws.s3.utils.Util;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AwsS3ServicesImpl implements IAwsS3Services {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.bucket}")
    private String bucketS3;

    @Value("${cloud.aws.url}")
    private String urlBucketS3;

    @Value("${cloud.aws.uri}")
    private String uriBucketS3;

    @Autowired
    private IArchivosRecnar iArchivosRecnar;

    @Autowired
    private IConfiguracionServiciosS3Repository iConfiguracionserviciosS3Repository;

    @Autowired
    private IOrdenIngresoDetalleRepository iOrdenIngresoDetalleRepository;

    @Override
    public ResponseUploadFile uploadFile(RequestAwsS3 request, ResponseUploadFile responseUploadFile) throws IOException {
        byte[] bI = Base64.decodeBase64(request.getImageBase64().split(";")[1].split(",")[1].getBytes());
        InputStream imageInputStream = new ByteArrayInputStream(bI);
        String mimeType = URLConnection.guessContentTypeFromStream(imageInputStream);
        String ext = Util.getExtensionFromMimeTypeMap(request.getImageBase64().split(";")[0]);
        Optional<ConfiguracionServiciosS3> configurationServicesS3 = iConfiguracionserviciosS3Repository.findById(request.getIdBucketAwsS3());
        String bucket=configurationServicesS3.get().getBucket()+"/"+configurationServicesS3.get().getFolder();
        String pathDocumentS3=bucket+"/"+request.getCodigo();
        String newFileName =  request.getNombreArchivo()+ "_"+System.currentTimeMillis()+"."+ext ;
        String fullPathAwsS3=request.getCodigo()+"/"+newFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bI.length);
        metadata.setContentType(mimeType);
        metadata.setCacheControl("public, max-age=31536000");

        PutObjectRequest requestS3 = new PutObjectRequest(pathDocumentS3, newFileName,imageInputStream, metadata);
        amazonS3.putObject(requestS3);

        request.setBucketAwsS3(bucket);
        request.setPathAwsS3(fullPathAwsS3);
        request.setNombreArchivo(newFileName);
        request.setExtensionArchivo("."+ext);
        request.setUriAwsS3(uriBucketS3+bucket+"/"+fullPathAwsS3);
        request.setFullPathAwsS3(urlBucketS3+configurationServicesS3.get().getFolder()+"/"+fullPathAwsS3);
        responseUploadFile.setNombreArchivoCargado(newFileName);
        responseUploadFile.setPathS3(configurationServicesS3.get().getFolder()+"/"+fullPathAwsS3);
        iArchivosRecnar.save(Util.buildArchivosRecnar(request));


        return responseUploadFile;
    }

    @Override
    public List<String> getObjectsFromS3() {

        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketS3);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());

    }


    @Override
    public InputStream downloadFile(Long id) {

        Optional<ArchivosRecnar> file = iArchivosRecnar.findById(id);

        S3Object object = amazonS3.getObject(file.get().getBucketAws3()+"/"+file.get().getCodigo(), file.get().getNombreArchivo());
        return object.getObjectContent();
    }

    @Override
    public  List<ArchivosRecnar> getArchivoRecnar(String codigo) {
        return  iArchivosRecnar.findByCodigo(codigo);
    }

    @Override
    public SearchResponse searchFile(Long id) {

        Optional<ArchivosRecnar> file = iArchivosRecnar.findById(id);
        S3Object object = amazonS3.getObject(file.get().getBucketAws3()+"/"+file.get().getCodigo(), file.get().getNombreArchivo());

        SearchResponse response = new SearchResponse();
        response.setServiceName("/search");
        response.setStatusCode(HttpStatus.OK);
        response.setResource(object.getObjectContent());
        response.setNombreArchivo(file.get().getNombreArchivo());

        return response;
    }

    @Override
    public List<ArchivosRecnar> getFileParentCode(String codigoPadre) {
        return  iArchivosRecnar.findByCodigoPadre(codigoPadre);
    }

    @Override
    public void restoreFiles(Long idDetalle) {
        Optional<ConfiguracionServiciosS3> configurationServicesS3 = iConfiguracionserviciosS3Repository.findById(1L);
        if (!configurationServicesS3.isPresent()) {
            throw new RuntimeException("No se encontró configuración de S3.");
        }

        DetalleOrdenIngresoDTO dto = iOrdenIngresoDetalleRepository
                .findDetalleOrdenIngresoById(idDetalle)
                .orElseThrow(() -> new RuntimeException("No se encontró el detalle con id: " + idDetalle));

        ConfiguracionServiciosS3 config = configurationServicesS3.get();
        String bucket = config.getBucket();

        // Procesar cada valor por separado
        processS3Folder(bucket, config.getFolder(), dto.getCodigoIngresoRemoto());
        processS3Folder(bucket, config.getFolder(), dto.getCodigoIngresoLocal());
        processS3Folder(bucket, config.getFolder(), dto.getCodigoOrdenIngreso());
        processS3Folder(bucket, config.getFolder(), dto.getCodigoDesintegracion());
    }

    private void processS3Folder(String bucket, String baseFolder, String subFolder) {
        if (subFolder == null ) return;

        String s3Prefix = baseFolder + "/Documentos_Naranjos/" + subFolder + "/";

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(s3Prefix);

        ListObjectsV2Result result;

        do {
            result = amazonS3.listObjectsV2(request);
            List<S3ObjectSummary> summaries = result.getObjectSummaries();

            for (S3ObjectSummary summary : summaries) {
                String key = summary.getKey();
                String rutaLocal = "D:/Naranjos 2.0/apache-tomcat-8.5.69/webapps/ROOT" + key.replace("naranjo-2", "");

                File destino = new File(rutaLocal);
                File parent = destino.getParentFile();

                if (!parent.exists()) {
                    parent.mkdirs();
                }

                amazonS3.getObject(new GetObjectRequest(bucket, key), destino);
            }

            request.setContinuationToken(result.getNextContinuationToken());

        } while (result.isTruncated());
    }
}
