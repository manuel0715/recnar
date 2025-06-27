package com.recnar.aws.s3.db.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_recnar",schema="public")
public class ArchivosRecnar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id",unique=true, nullable = false)
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "modulo")
    private String modulo;

    @Column(name = "nombre_proceso")
    private String nombreProceso;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "extension_archivo")
    private String extensionArchivo;

    @Column(name = "bucket_aws_s3")
    private String bucketAws3;

    @Column(name = "path_aws_s3")
    private String pathAwsS3;

    @Column(name = "uri_aws_s3")
    private String uriAwsS3;

    @Column(name = "full_path_aws_s3")
    private String fullPathAwsS3;

    @Column(name = "usuario_creador")
    private String usuarioCreador;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = true, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_ultima_modificacion")
    private String usuarioUltimaModificacion;

    @CreationTimestamp
    @Column(name = "fecha_ultima_modificacion", nullable = true, updatable = false)
    private LocalDateTime fechaUltimaModificacion;

    @Column(name = "codigo_padre")
    private String codigoPadre;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getBucketAws3() {
        return bucketAws3;
    }

    public void setBucketAws3(String bucketAws3) {
        this.bucketAws3 = bucketAws3;
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

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuarioUltimaModificacion() {
        return usuarioUltimaModificacion;
    }

    public void setUsuarioUltimaModificacion(String usuarioUltimaModificacion) {
        this.usuarioUltimaModificacion = usuarioUltimaModificacion;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;

    }

    public String getCodigoPadre() {
        return codigoPadre;
    }

    public void setCodigoPadre(String codigoPadre) {
        this.codigoPadre = codigoPadre;
    }
}
