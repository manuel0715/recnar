package com.recnar.aws.s3.dto;


public class RequestAwsS3 {

    private Long id;
    private String modulo;
    private String nombreProceso;
    private String codigo;
    private String nombreArchivo;
    private String extensionArchivo;
    private String usuarioCreador;
    private String imageBase64;
    private Long idBucketAwsS3;
    private String bucketAwsS3;
    private String pathAwsS3;
    private String uriAwsS3;
    private String fullPathAwsS3;
    private String codigoPadre;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getNombreProceso() {
        return nombreProceso;
    }

    public void setNombreProceso(String nombreProceso) {
        this.nombreProceso = nombreProceso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getExtensionArchivo() {
        return extensionArchivo;
    }

    public void setExtensionArchivo(String extensionArchivo) {
        this.extensionArchivo = extensionArchivo;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Long getIdBucketAwsS3() {
        return idBucketAwsS3;
    }

    public void setIdBucketAwsS3(Long idBucketAwsS3) {
        this.idBucketAwsS3 = idBucketAwsS3;
    }
    public String getBucketAwsS3() {
        return bucketAwsS3;
    }

    public void setBucketAwsS3(String bucketAwsS3) {
        this.bucketAwsS3 = bucketAwsS3;
    }

    public String getPathAwsS3() {
        return pathAwsS3;
    }

    public void setPathAwsS3(String pathAwsS3) {
        this.pathAwsS3 = pathAwsS3;
    }

    public String getUriAwsS3() {
        return uriAwsS3;
    }

    public void setUriAwsS3(String uriAwsS3) {
        this.uriAwsS3 = uriAwsS3;
    }

    public String getFullPathAwsS3() {
        return fullPathAwsS3;
    }

    public void setFullPathAwsS3(String fullPathAwsS3) {
        this.fullPathAwsS3 = fullPathAwsS3;
    }

    public String getCodigoPadre() {
        return codigoPadre;
    }

    public void setCodigoPadre(String codigoPadre) {
        this.codigoPadre = codigoPadre;
    }
}